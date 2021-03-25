package jp.co.hyas.hpf.database;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.MethodNotAllowedException;

import com.fasterxml.jackson.core.JsonParseException;

import jp.co.hyas.hpf.database.base.ApiResult;
import jp.co.hyas.hpf.database.base.BaseRestController;
import jp.co.hyas.hpf.database.base.BaseSearchEntity;

@RestController
@RequestMapping("api/users")
public class UserRestController extends BaseRestController<User, BaseSearchEntity, UserRepository, UserService> {
	// 一件取得
	@RequestMapping(method = GET, value = "/sfid/{ident}")
	public ApiResult getBySfid(@PathVariable String ident,
			HttpServletResponse response, HttpServletRequest request) {
		// 認証
		authorize(request);

		User body = service.findOne(ident);
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

	@Override
	// @RequestMapping(method = GET, value = "/")
	public ApiResult getList(BaseSearchEntity ts, Pageable pageable, HttpServletRequest request) {
		throw new MethodNotAllowedException("index", null);
	}

	@Override
	// @RequestMapping(method = GET, value = "{ident}")
	public ApiResult get(String ident, HttpServletResponse response, HttpServletRequest request) {
		throw new MethodNotAllowedException("get", null);
	}

	@Override
	public ApiResult post(User t, BindingResult bind, HttpServletResponse response, HttpServletRequest request) {
		throw new MethodNotAllowedException("post", null);
	}

	@Override
	public ApiResult putPartial(HttpServletRequest req, String ident, String body, HttpServletResponse response)
			throws JsonParseException {
		throw new MethodNotAllowedException("put", null);
	}

	@Override
	public ApiResult delete(String ident, HttpServletResponse response, HttpServletRequest request) {
		throw new MethodNotAllowedException("delete", null);
	}

	@Override
	public ApiResult patch(String ident, User t, HttpServletRequest request) {
		throw new MethodNotAllowedException("patch", null);
	}

}
