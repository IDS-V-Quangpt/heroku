package jp.co.hyas.hpf.database;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;

import jp.co.hyas.hpf.database.base.ApiResult;
import jp.co.hyas.hpf.database.base.BaseRestController;

@RestController
@RequestMapping("api/owners")
public class OwnerRestController extends BaseRestController<Owner, OwnerSearchEntity, OwnerRepository, OwnerService> {
	//@Autowired
	//OwnerService service;

	@RequestMapping(method=POST, value="login")
	public ApiResult login(@RequestBody OwnerLoginBean auth) {
		Map<String, Object> responseBody = service.authenticate(auth);
		ApiResult result = this.getResultForSuccess(responseBody);
		return result;
	}


	// 一件作成
	@RequestMapping(method = POST)
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResult post(@RequestBody @Validated Owner t, BindingResult bind,
			HttpServletResponse response, HttpServletRequest request) {
		ApiResult result = super.post(t, bind, response, request);
		//super.post(t, bind, response, request);
		// 設定した記憶パスワードを返却値にセット
		Owner after = (Owner)result.getOrDefault("response_body", null);
		if (after != null && service.lastPasswd != null) {
			after.setLogin_password__c(service.lastPasswd);
		}
		return result;
	}
	// 一件部分更新
	@RequestMapping(method = PUT, value = "{ident}")
	public ApiResult putPartial(HttpServletRequest req, @PathVariable String ident, @RequestBody String body,
			HttpServletResponse response) throws JsonParseException {
		ApiResult result = super.putPartial(req, ident, body, response);
		// パスワードを更新した場合は設定した記憶パスワードを返却値にセット
		Owner after = (Owner)result.getOrDefault("response_body", null);
		if (after != null && service.lastPasswd != null) {
			after.setLogin_password__c(service.lastPasswd);
		}
		return result;

		//super.putPartial(req, ident, body, response);
		// パスワードを更新した場合は設定した記憶パスワードを返却値にセット
		//Owner after = service.findOne(ident);
		//if (service.lastPasswd != null)
		//	after.setLogin_password__c(service.lastPasswd);
		//ApiResult result = (ApiResult) this.getResultForSuccess(after);
		//return result;
	}
}
