package jp.co.hyas.hpf.database.base;

//import org.springframework.web.bind.annotation.RequestMethod;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.hyas.hpf.core.exception.ValidateException;

public class BaseRestController<T extends BaseEntity, TS extends BaseSearchEntityInterface, R extends BaseRepository<T, TS>, S extends BaseService<T, TS, R>>
		extends BaseApiController implements ErrorController {

	@Autowired
	protected S service;

	// https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/PageImpl.html
	protected ApiResult getPageInfo(Page<T> page) {
		ApiResult result = new ApiResult();
		Pageable pageable = page.getPageable();
		result.put("page_no", page.getNumber() + 1);
		result.put("page_size", page.getSize());
		result.put("total_page", page.getTotalPages());
		result.put("item_count", page.getNumberOfElements());
		result.put("total", page.getTotalElements());
		result.put("offset", pageable.getOffset());
		return result;
	}

	protected void authorize(HttpServletRequest request) {
		super.authorize(request);
		service.appCode = appCode;
	}

	// -------------- エラーハンドラー処理
	// implements ErrorController
	@Override
	public String getErrorPath() {
		return "/error";
	}

	// エラーページ
	@RequestMapping("/error")
	public ApiResult handleError(HttpServletRequest request) {
		@SuppressWarnings("unused")
		Exception e = (Exception) request.getAttribute("javax.servlet.error.exception");
		return handleException(e);

	}

	// -------------- API処理(CRUD)
	// 全件取得
	@RequestMapping(method = GET)
	public ApiResult getList(@ModelAttribute TS ts,
			@PageableDefault(size = 500, sort = { "id" }) Pageable pageable, HttpServletRequest request) {
		// 認証
		authorize(request);
		// return service.findAll();
		// return service.findPage(pageable).getContent();
		// return service.search(queryParameters, pageable).getContent();
		Page<T> page = service.search(ts, pageable);
		List<T> body = page.getContent();
		// ApiResult result = this.getResultForSuccess(body,"pageing",page);
		ApiResult pageinfo = getPageInfo(page);
		ApiResult result = this.getResultForSuccess(body, "paging", pageinfo);
		return result;
	}

	// 一件取得
	@RequestMapping(method = GET, value = "{ident}")
	public ApiResult get(@PathVariable String ident,
			HttpServletResponse response, HttpServletRequest request) {
		// 認証
		authorize(request);
		// return service.findOne(id);
		T body = service.findOne(ident);
		if (body == null) {
			ApiResult result = this.getResultForError(null);
			result.put("error_kind", "target-not-exists");
			result.put("error_message", "対象の情報は存在しません");
			result.put("error_detail", "指定ID値: " + ident);
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return result;
		}
		ApiResult result = this.getResultForSuccess(body);
		return result;
	}

	// 一件作成
	@RequestMapping(method = POST)
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResult post(@RequestBody @Validated T t, BindingResult bind,
			HttpServletResponse response, HttpServletRequest request) {
		// 認証
		authorize(request);
		if (bind.hasErrors()) {
			MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
			for (ObjectError error : bind.getGlobalErrors()) {
				map.add("global", error.getDefaultMessage());
			}
			for (FieldError error : bind.getFieldErrors()) {
				map.add(error.getField(), error.getDefaultMessage());
			}
			throw new ValidateException(map);
		}
		String ident = service.generateId();// キーを生成
		t.setNaturalKey(ident); // キーを設定
		t.setLogicalDeleteFlag(false); // 論理削除フラグをOFF
		//service.presetEntityOnCrud("create-authorize", ident, t, null);
		service.create(t);

		// オブジェクトの再取得
		T after = service.findOne(t.getNaturalKey());
		ApiResult result = (ApiResult) this.getResultForSuccess(after);
		return result;
	}

	/*
	 * // 一件更新 //@RequestMapping(method=PUT, value="{ident}") public ApiResult
	 * put(@PathVariable String ident, @RequestBody T t) { service.update(ident,
	 * t); ApiResult result = this.getResultForSuccess(t); return result; }
	 */
	// 一件部分更新
	@RequestMapping(method = PUT, value = "{ident}")
	public ApiResult putPartial(HttpServletRequest req, @PathVariable String ident, @RequestBody String body,
			HttpServletResponse response) throws JsonParseException {
		// 認証
		authorize(req);
		T exists = service.findOne(ident);
		if (exists == null) {
			ApiResult result = this.getResultForError(null);
			result.put("error_kind", "target-not-exists");
			result.put("error_message", "対象の情報は存在しません");
			result.put("error_detail", "指定ID値: " + ident);
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return result;
		}

		// String body = Util.getRequestBody(req);

		// 既存のEntityオブジェクトに対して、パラメータをエンティティ化したクラスを上書きコピーする
		// copyPropertiesは値がnullのものは上書きしないので、差分を適用したEntityができあがる。
		// Entityの有効チェック（validate)をサービスに移動させるために、従来の方法から変更した。
		// これは、サービスに引き渡されたエンティティでvalidateするため、その時点で完成された
		// オブジェクトが必要なためである。
		BaseEntity t = exists;  // 取得した既存のEntity
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> _map = null;
		try {
			Map<String, Object> map = mapper.readValue(body, LinkedHashMap.class);
			//service.presetEntityOnCrud("modify-authorize", ident, null, map);
			BaseEntity tmp = mapper.convertValue(map, service.getEntityClass());
			// t = service.convertJson2Request(body,map);
			// t = mapper.readValue(body,service.getEntityClass());
			
			// @JsonIgnore対象をフィルタリングするためにbean=>json=>mapして残ったプロパティーのみ対象とする
			String tmpjson = mapper.writeValueAsString(tmp);
			Map<String, Object> tmpmap = mapper.readValue(tmpjson, LinkedHashMap.class);
			ArrayList<String> JsonIgnorekeys = new ArrayList<String>();
			map.forEach((k, v) -> {
				if (!tmpmap.containsKey(k)) {
					JsonIgnorekeys.add(k);
					System.err.printf("tmpmap[%s]\n", k);
				}
			});
			JsonIgnorekeys.forEach((k) -> { map.remove(k); });
			_map = map;

			Util.copyPropertiesNullSetByHash(tmp, t, map);
		} catch (JsonParseException ejson) {
			throw ejson; // "faxng__c" : truev
		} catch (IllegalArgumentException ijson) {
			throw ijson; // "capital__c" : "cccccc",
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			ApiResult result = this.getResultForError(null);
			result.put("error_kind", "request-error");
			result.put("error_message", "JSON書式上に問題があります");
			result.put("error_detail", e.getLocalizedMessage());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return result;
		}

		service.updatePartial(ident, (T) t, _map);
		// ApiResult result = (ApiResult) this.getResultForSuccess(map);
		T after = service.findOne(ident);
		ApiResult result = (ApiResult) this.getResultForSuccess(after);
		return result;
	}

	// 一件削除
	@RequestMapping(method = DELETE, value = "{ident}")
	// @ResponseStatus(HttpStatus.NO_CONTENT)
	public ApiResult delete(@PathVariable String ident, HttpServletResponse response, HttpServletRequest request) {
		// 認証
		authorize(request);
		T exists = service.findOne(ident);
		if (exists == null) {
			ApiResult result = this.getResultForError(null);
			result.put("error_kind", "target-not-exists");
			result.put("error_message", "対象の情報は存在しません");
			result.put("error_detail", "指定ID値: " + ident);
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return result;
		}
		service.delete(ident);
		ApiResult result = this.getResultForSuccess(ident);
		return result;
	}

	// ================== 調査試験コード
	// 一件部分更新
	@RequestMapping(method = PATCH, value = "{ident}")
	public ApiResult patch(@PathVariable String ident, @RequestBody T t, HttpServletRequest request) {
		// 認証
		authorize(request);
		T target = service.findOne(ident);
		Util.copyProperties(t, target);
		service.update(ident, target);
		T reg = service.findOne(ident);
		ApiResult result = this.getResultForSuccess(reg);
		return result;
	}
}
