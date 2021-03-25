package jp.co.hyas.hpf.auth;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.servlet.ServletUtils;

import jp.co.hyas.hpf.database.base.ApiResult;
import jp.co.hyas.hpf.database.base.AppConst;
import jp.co.hyas.hpf.database.base.BaseApiController;
import jp.co.hyas.hpf.database.base.Util;
import xyz.downgoon.snowflake.Snowflake;


@RestController
//@RequestMapping("auth")
public class AuthRestController extends BaseApiController {

	//@Autowired
	//private StringRedisTemplate redisTemplate;

	@Autowired
	AuthService service;
	@Autowired
	RedisService samlval;
	//RedisService samlval = new RedisService();


	public static final String DEFAULT_LOGIN_URL = "/portal/login";

	// API
	// --------------------------------------------
	// 認可状態取得API
	@RequestMapping(value="api/auth", method=GET)
	public ApiResult auth(@RequestParam Map<String, String> qs, HttpServletResponse response, HttpServletRequest request) {
		authorize(request);
		String token = qs.getOrDefault("token", "");
		ApiResult failure = this.getResultForError(null);

		String msg = null;
		if ("".equals(token) || token == null) {
			msg = "パラメータ(トークン)が未指定です";
			failure.put("error_kind", "request-error");
			failure.put("error_message", msg);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return failure;
		}
		//RedisService samlval = new RedisService();
		Map<String, List<String>> saml = samlval.loadSamlAttrs(token, false);
		//samlval.removeAttrs(token);
		List<String> userIds = saml.getOrDefault("userId", null);
		if (userIds == null || userIds.size() <= 0) {
			failure.put("error_kind", "system-error");
			failure.put("error_message", "SAML情報不備:userId値が存在しません");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return failure;
		}
		String userId = userIds.get(0);
		String userId18 = userId;
		if (userId.length() == 15 || userId.length() == 18) {
			userId18 = Util.sfId15to18(userId);
		}// else

		try {
			ApiResult body = service.getAuthInfo(userId18);
			if (body == null) {
				failure.put("error_kind", "user-role-error");
				failure.put("error_message", "該当ユーザにはサービス利用(ログイン)権限がありません");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return failure;
			}

			ApiResult result = this.getResultForSuccess(body);
			return result;
		} catch (Exception e) {
			failure.put("error_kind", "system-error");
			failure.put("error_message", "不測のエラーが発生しました。:"+  e.getMessage());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return failure;
		}
	}

	// HPF
	// --------------------------------------------
	// HPF Connector
	// 各サービスからの認証要求をSalesforceにリダイレクトする。要求時のパラメータを一時的に保持する。
	@RequestMapping(value="auth/connect", method=GET)
	public void connect(HttpSession session, HttpServletRequest request, HttpServletResponse response,@RequestParam Map<String, String> qs) {
		//return service.findAll();
		//return service.findPage(pageable).getContent();
		if (qs.containsKey("backurl")) {
			qs.forEach((k, v) -> {
				System.err.printf("connect[setAttribute]:%s: %s%n", k, v);
				session.setAttribute(k, v);
			});
		}

		try {
			Auth auth = new Auth(request, response);
			if (request.getParameter("attrs") == null) {
				//auth.login();

				//public String login(String returnTo, Boolean forceAuthn, Boolean isPassive, Boolean setNameIdPolicy, Boolean stay) throws IOException, SettingsException {
				String loginUrl = auth.login(null,false,false,false,true);
	        	response.sendRedirect(loginUrl);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;// service.search(queryParameters, pageable).getContent();
	}

	// datacenter: AppConst.IDG_MACHINEID; workerId: 21
	static Snowflake idGen = new Snowflake(AppConst.IDG_MACHINEID, 21);

	// HPF Callback
	// Salesforceの認証結果を取得し、認可情報とユーザリソースを取得してキャッシュサーバに保存する。
	@RequestMapping(value="auth/callback", method=POST)
	public void callback(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		String returnUrl = DEFAULT_LOGIN_URL;
		try {
			Auth auth = new Auth(request, response);
			auth.processResponse();

			if (!auth.isAuthenticated()) {
				System.err.println("Not authenticated!");
				//System.err.println("auth.getLastRequestXML(): "+auth.getLastRequestXML());
				//System.err.println("auth.getLastResponseXML(): "+auth.getLastResponseXML());
			}

			String error_msg = null;
			List<String> errors = auth.getErrors();
		    if (!errors.isEmpty()) {
		    	String errorMsgs = String.join(", ", errors);
				System.err.println("error: "+errorMsgs);
				error_msg = errorMsgs;
		    }

			Map<String, List<String>> attributes = auth.getAttributes();
			String nameId = auth.getNameId();
			String relayState = request.getParameter("RelayState");
			if (relayState != null && !relayState.isEmpty() &&
					!relayState.equals(ServletUtils.getSelfRoutedURLNoQuery(request)) &&
					!relayState.contains("/connect")) { // We don't want to be redirected to login.jsp neither
				response.sendRedirect(relayState);
				return;
			}

			List<String> userIds = attributes.getOrDefault("userId", null);
			if (userIds == null || userIds.size() <= 0) {
				error_msg = "SAML情報不備:userId値が存在しません";
			}

			if (session.getAttribute("backurl") != null) {
				returnUrl = (String)session.getAttribute("backurl");
			}
			String dm = (returnUrl.contains("?")) ? "&" : "?";
			if (returnUrl.endsWith("?")) dm = "";

			// 成功時
			if (error_msg == null) {
				// トークンのIDを生成 & saml属性値をredis保存
				String token = "TKN" + idGen.nextId();
				session.setAttribute("token",token);

				//RedisService samlval = new RedisService();
				samlval.saveSamlAttrs(token,attributes);
				returnUrl = returnUrl + dm + "result=success&token=" + URLEncoder.encode(token, "UTF-8");
			} else {	// エラー発生時
				returnUrl = returnUrl + dm + "result=failure&error=" + URLEncoder.encode(error_msg, "UTF-8");
			}

			System.err.println("callback(sendRedirect): "+returnUrl);
			response.sendRedirect(returnUrl);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// HPF DisConnector
	// Salesforceの認証ログアウト
	@RequestMapping(value="auth/logout", method=GET)
	public void logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		try {
			String returnUrl = DEFAULT_LOGIN_URL;

			boolean valid = true;
	    	try {
				HttpSession ss = request.getSession(false);
				if (ss == null || session.isNew()) valid = false;
	    	} catch(IllegalStateException ex) {
	    		valid = false;
	    	}
	    	if (valid) {

	    		String token = (String) session.getAttribute("token");
				samlval.removeSamlAttrs(token);

				if (session.getAttribute("backurl") != null) {
					returnUrl = (String) session.getAttribute("backurl");
				}
		    	if (session != null && request.isRequestedSessionIdValid())
		    		session.invalidate();
	    	}


			Auth auth = new Auth(request, response);
			auth.processSLO();
			//public void processSLO(Boolean keepLocalSession, String requestId) throws Exception {
			//auth.processSLO(true,null);
			List<String> errors = auth.getErrors();
			if (!errors.isEmpty()) {
		    	String error_msg = String.join(", ", errors);
				System.err.println("error: "+error_msg);

				String dm = (returnUrl.contains("?")) ? "&" : "?";
				if (returnUrl.endsWith("?")) dm = "";
				returnUrl = returnUrl + dm + "result=failure&error=" + URLEncoder.encode(error_msg, "UTF-8");
				response.sendRedirect(returnUrl);
				return;
			//} else {
				//if (session.getAttribute("user_id") != null)
				//	session.removeAttribute("user_id");
				//response.sendRedirect(returnUrl);
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// HPF DisConnector
	// Salesforceの認証ログアウト
	@RequestMapping(value="auth/disconnect", method=GET)
	public void disconnect(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		try {
			//String returnUrl = DEFAULT_LOGIN_URL;
			Auth auth = new Auth(request, response);
			//if (session.getAttribute("backurl") != null) {
			//	returnUrl = (String) session.getAttribute("backurl");
			//}
			//auth.logout(returnUrl);
			auth.logout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	// HPF DisConnector
	//他APIのシングルサインオン
	@RequestMapping(value="auth/url", method=GET)
	public void ssoAuth(HttpSession session, HttpServletRequest request, HttpServletResponse response,@RequestParam Map<String, String> qs) {
		System.err.println("SSO AUTH TOKEN "+ session.getAttribute("token") + "URL " + request.getParameter("url"));
		
		try {
			System.err.println("SSO AUTH REQUEST " + request + "RESPONSE" + response);
			if((session.getAttribute("token") == null) && (request.getParameter("url") == null)){
				response.sendRedirect("redirect:/portal/service");
				return;
			}
			String token = (String) session.getAttribute("token");
			response.sendRedirect(request.getParameter("url") + "?token=" + URLEncoder.encode(token, "UTF-8"));
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
