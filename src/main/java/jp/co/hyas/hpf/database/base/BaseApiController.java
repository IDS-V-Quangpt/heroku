package jp.co.hyas.hpf.database.base;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.MethodNotAllowedException;

import com.fasterxml.jackson.core.JsonParseException;

import jp.co.hyas.hpf.core.exception.AuthorizeException;
import jp.co.hyas.hpf.core.exception.ValidateException;
import jp.co.hyas.hpf.database.UserRepository;

public class BaseApiController {

	@Autowired
	UserRepository userRep;

	/** app_keyによる認証の要否判定メソッド */
	protected Boolean isAppKeyAuthRequired() { return true; }
	/** アクセス元アプリケーションコード */
	protected String appCode = null;
	/** user_idによる認証の要否判定メソッド */
	protected Boolean isUserIdAuthRequired() { return false; }
	/** 認証ユーザ */
	protected Map<String, Object> authUser;

	@Value("${auth.app.code.RGO:RGO}")
	protected String appCodeRGO;
	@Value("${auth.app.code.HPF:HPF}")
	protected String appCodeHPF;
	@Value("${auth.app.code.IBX:IBX}")
	protected String appCodeIBX;
	@Value("${auth.app.code.KDX:KDX}")
	protected String appCodeKDX;
	
	@Value("${auth.app.key.RGO}")
	protected String appKeyRGO;
	@Value("${auth.app.key.HPF}")
	protected String appKeyHPF;
	@Value("${auth.app.key.IBX}")
	protected String appKeyIBX;
	@Value("${auth.app.key.KDX}")
	protected String appKeyKDX;

	protected void authorize(HttpServletRequest request)
	{
		if (isAppKeyAuthRequired()) {
			String app_key = request.getHeader("X-AUTH-APP-KEY");
			if (app_key == null) {
				throw new AuthorizeException("X-AUTH-APP-KEY is required.");
			}
			if (appKeyRGO.equals(app_key)) {
				appCode = appCodeRGO;
			} else if (appKeyHPF.equals(app_key)) {
				appCode = appCodeHPF;
			} else if (appKeyIBX.equals(app_key)) {
				appCode = appCodeIBX;
			} else if (appKeyKDX.equals(app_key)) {
				appCode = appCodeKDX;
			} else {
				throw new AuthorizeException("X-AUTH-APP-KEY is invalid.");
			}
		}
		if (isUserIdAuthRequired()) {
			String user_id = request.getHeader("X-AUTH-USER-ID");
			if (user_id == null) {
				throw new AuthorizeException("X-AUTH-USER-ID is required.");
			}
			authUser = userRep.findOneBySfId(user_id);
			if (authUser == null) {
				throw new AuthorizeException("X-AUTH-USER-ID is invalid.");
			}
		}
	}

	protected ApiResult getResultForSuccess(Object responseBody) {
		return this.getResultForSuccess(responseBody, null, null);
	}

	protected ApiResult getResultForSuccess(Object responseBody, String metakey, Object meta) {
		ApiResult result = new ApiResult();
		result.put("result", "success");
		if (metakey != responseBody)
			result.put("response_body", responseBody);
		if (metakey != null)
			result.put("" + metakey, meta);
		return result;
	}

	protected ApiResult getResultForError(Object responseBody) {
		return this.getResultForError(responseBody, null, null);
	}
	protected ApiResult getResultForError(Object responseBody, String metakey, Object meta) {
		ApiResult result = this.getResultForSuccess(responseBody, metakey, meta);
		//ApiResult result = ApiResult();
		result.put("result", "failure");
		result.put("error_kind", "system-error");
		result.put("error_message", "システム上に問題が発生しました");
		return result;
	}

	// -------------- エラーハンドラー処理
	// 基本エラー
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult handleException(Exception e) {
    	e.printStackTrace();
		ApiResult result = this.getResultForError(null);
		if (e != null) {
			result.put("error_detail", e.getLocalizedMessage());
			result.put("error_develop_info", e.getClass());
			result.put("error_develop_trace", Util.stackTraceMsg(e));
		}
		return result;
    }
    public ApiResult handleExceptionRequestError(Exception e, String msg) {
		ApiResult result = this.getResultForError(null);
		result.put("error_kind", "request-error");
		result.put("error_message", msg);
		if (e != null) {
			result.put("error_develop_info", e.getClass());
			result.put("error_detail", e.getLocalizedMessage());
		}
		return result;
    }
    // Jsonパースエラー
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult handleExceptionJsonParse2(HttpMessageNotReadableException e) {
		return handleExceptionRequestError(e, "JSON書式上に問題があります[HttpMessageNotReadableException]");
    }
    @ExceptionHandler(JsonParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult handleExceptionJsonParse(JsonParseException e) {
		return handleExceptionRequestError(e, "JSON書式上に問題があります[JsonParseException]");
    }
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult handleExceptionIllegalArgument(IllegalArgumentException e) {
		return handleExceptionRequestError(e, "パラメータの型に問題があります[IllegalArgumentException]");
    }
    //@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "CUSTOM MESSAGE HERE")
	@ExceptionHandler(ValidateException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiResult handleExceptionValidate(ValidateException e) {
		ApiResult result = handleExceptionRequestError(null, "リクエストパラメータに問題があります[ValidateException]");
		result.put("error_develop_info", e.getClass());
		result.put("error_detail", e.getDetailMap());
		return result;
	}
	@ExceptionHandler(AuthorizeException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ApiResult handleExceptionAuthorize(AuthorizeException e) {
		ApiResult result = handleExceptionRequestError(null, "認証情報に誤りがあります[AuthorizeException]");
		result.put("error_develop_info", e.getClass());
		result.put("error_detail", e.getMessage());
		return result;
	}
	@ExceptionHandler(MethodNotAllowedException.class)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public ApiResult handleExceptionAuthorize(MethodNotAllowedException e) {
		ApiResult result = handleExceptionRequestError(null, "この操作は許可されていません");
		if (e != null) {
			result.put("error_detail", e.getLocalizedMessage());
			result.put("error_develop_info", e.getClass());
			result.put("error_develop_trace", Util.stackTraceMsg(e));
		}
		return result;
	}
}
