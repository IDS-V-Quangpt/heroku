package jp.co.hyas.hpf.portal;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.hyas.hpf.database.base.Util;
import jp.co.hyas.hpf.database.AccountRepository;

@Controller
@RequestMapping("portal")
public class LoginController extends HpfBaseController {
	@Value("${auth.app.key.HPF}")
	protected String appKeyHPF;
	@Value("${logout.service.url}")
	protected String logoutServiceUrl;
	@Value("${serviceIBX.url}")
	protected String serviceIbxUrl;
	@Value("${serviceKDX.url}")
	protected String serviceKdxUrl;
	@Value("${serviceRGO.url}")
	protected String serviceRgoUrl;
	

	@Autowired
	AccountRepository accountRep;
	
	public String isLogin(HttpSession session) {
		// ログイン済みであればTOP画面へリダイレクト
		String userId = (String) session.getAttribute("user_id");
		return userId;
	}
	public UriComponentsBuilder getFullURL(HttpServletRequest request, String path) {
		try {
			return UriComponentsBuilder.fromUri(new URI(request.getRequestURL().toString())).replacePath(path);
		} catch (URISyntaxException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}//.queryParam("token", token).build().encode().toUri();
		return null;
	}

	@RequestMapping("logout")
	public String logout(HttpServletRequest request, HttpSession session, Model model) {
		String userId = isLogin(session);

		if (userId != null) session.removeAttribute("user_id");
		if (session.getAttribute("user_info") != null) session.removeAttribute("user_info");
		//セッション削除
		if (session.getAttribute("role_of_iekachi") != null) session.removeAttribute("role_of_iekachi");
		if (session.getAttribute("role_of_pms") != null) session.removeAttribute("role_of_pms");
		if (session.getAttribute("role_of_cms") != null) session.removeAttribute("role_of_cms");
		if (session.getAttribute("role_of_r_de_go") != null) session.removeAttribute("role_of_r_de_go");
		if (session.getAttribute("ibxMax") != null) session.removeAttribute("ibxMax");
		if (session.getAttribute("pmsMax") != null) session.removeAttribute("pmsMax");
		if (session.getAttribute("cmsMax") != null) session.removeAttribute("cmsMax");
		if (session.getAttribute("rgoMax") != null) session.removeAttribute("rgoMax");
		if (session.getAttribute("accountRec") != null) session.removeAttribute("accountRec");
		
		// return "redirect:/portal/login";
		return "redirect:" + logoutServiceUrl;
	}

	@RequestMapping("/")
	public String index() {
		return "redirect:/portal/login";
	}
	@RequestMapping("login")
	public String login(HttpServletRequest request, HttpSession session, Model model) {
		String back_url = getFullURL(request, "/portal/auth").build().encode().toUri().toString();
		String encoded_back_url = "";
		session.setMaxInactiveInterval(36000);
		// ログイン済みであればTOP画面へリダイレクト
		String userId = isLogin(session);//(String) session.getAttribute("user_id");
		if (userId != null && !"".equals(userId)) {
			System.err.println("Login exits: " + userId);
			//return "portal/top";
			return "redirect:/portal/service";
		}

		model.addAttribute("result", session.getAttribute("result"));
		model.addAttribute("error", session.getAttribute("error"));
		if (session.getAttribute("result") != null) {
			session.removeAttribute("result");
			session.removeAttribute("error");
		}
		try {
			//model.addAttribute("back_url", URLEncoder.encode(back_url, "UTF-8"));
			encoded_back_url = URLEncoder.encode(back_url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		// return "portal/login";
		return "redirect:/auth/connect?backurl=" + encoded_back_url;
	}

	@RequestMapping("top")
	public String top(HttpServletRequest request, HttpSession session, Model model) {
		String userId = isLogin(session);
		if (userId == null) {
			System.err.println("No login");
			return "redirect:/portal/login";
		}
		//// 認証情報取得API呼び出し
		//URI apitarget = getFullURL(request, API_AUTH).queryParam("token", token).build().encode().toUri();
		//Map<String, Object> apiResult = apiCallAuth(apitarget);
		@SuppressWarnings("unchecked")
		Map<String, Object> user_info = (Map<String, Object>)session.getAttribute("user_info");
		model.addAttribute("user_info", user_info);

		ObjectMapper mapper = new ObjectMapper();
		String json = null;
		try {
			json = mapper.writeValueAsString(user_info);
		} catch (JsonProcessingException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		model.addAttribute("user_info_json", json);

		return "portal/top";
	}


	// https://qiita.com/opengl-8080/items/05d9490d6f0544e2351a
	// @RequestParam MultiValueMap<String, String> multiMap => {id=[100], name=[hoge, fuga]}

	@RequestMapping("auth")
	//public String auth(HttpSession session, @RequestParam(value = "token", required = false) String token, Model model) {
	public String auth(HttpServletRequest request, HttpSession session, @RequestParam Map<String, String> qs, Model model) {
		String token = qs.getOrDefault("token", null);
		String result = qs.getOrDefault("result", null);
		String error = qs.getOrDefault("error", null);

		model.addAttribute("result", result);
		session.setAttribute("result", result);
		if (token == null || result == null || "failure".equals(result)) {
			//model.addAttribute("error", error);
			session.setAttribute("error", error);
			return "redirect:/portal/login";
		}

		model.addAttribute("token", token);

		// 認証情報取得API呼び出し
		URI apitarget = getFullURL(request, API_AUTH).queryParam("token", token).build().encode().toUri();
		Map<String, Object> apiResult = apiCallAuth(apitarget);

		//result.put("result", "failure");
		//result.put("error_kind", "system-error");
		//result.put("error_message", "システム上に問題が発生しました");
		if (apiResult == null || !"success".equals(apiResult.getOrDefault("result", "failure"))) {
			error = "不測のエラー：※設定およびデータベース値の不備によるエラーの可能性があります。";
			if (apiResult != null)
				error = (String)apiResult.getOrDefault("error_message", "認証トークン取得APIエラー");
			//model.addAttribute("error", error);
			session.setAttribute("error", error);
			//return "redirect:/portal/login";
			return "portal/login";
		}
		String userId = null;
		@SuppressWarnings("unchecked")
		Map<String, Object> apiBody = (Map<String, Object>) apiResult.getOrDefault("response_body", null);
		if (apiBody != null)
			userId = (String)apiBody.getOrDefault("user_id", null);

		if (userId == null) {
			error = (String)apiResult.getOrDefault("error_message", "認証トークン取得APIエラー");
			//model.addAttribute("error", error);
			session.setAttribute("error", error);
			//return "redirect:/portal/login";
			return "portal/login";
		}

		// サーバミドルウェアの場合は(フルURLの)リダイレクトURL生成を別途対応する必要あり
		// https://qiita.com/rubytomato@github/items/8d132dec042f695e50f6
		// UriComponentsBuilder builder
		session.setAttribute("user_id", userId);
		session.setAttribute("user_info", apiBody);

		return "redirect:/portal/service";
	}

	final static String API_AUTH = "/api/auth";

	// 認証情報取得API呼び出し
	@Autowired
	private RestTemplate restTemplate;
	public Map<String, Object> apiCallAuth(URI url) {
		Logger logger = LoggerFactory.getLogger(LoginController.class);
		//RestTemplate restTemplate = new RestTemplate();

		logger.info("call api[/api/auth] : {}", url);
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-AUTH-APP-KEY", appKeyHPF);
		HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

		String json = null;
		try {
			ResponseEntity<String> ent = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			json = ent.getBody();
		} catch (HttpClientErrorException he) {
			json = he.getResponseBodyAsString();
			logger.info("auth error {} to map", json, he.getMessage());
		} catch (Exception e) {
			return null;
		}
		Map<String,Object> map = new HashMap<String,Object>();
		ObjectMapper mapper = new ObjectMapper();

		try {
			//convert JSON string to Map
			map = mapper.readValue(json, new TypeReference<LinkedHashMap<String,Object>>(){});
		} catch (Exception e) {
			logger.info("Exception converting {} to map", json, e);
		}
		return map;
	}

	@RequestMapping("service")
	public String service(HttpServletRequest request, HttpSession session, Model model) {
		String userId = isLogin(session);
		//追加
		Map<String, Object> accountRec = null;
		Boolean role_of_iekachi__c;
		Boolean role_of_pms__c;
		Boolean role_of_cms__c;
		Boolean role_of_r_de_go__c;
		
		Object wkIbx;
		Object wkPms;
		Object wkCms;
		Object wkRgo;
		
		Integer ibxMax = 0;
		Integer pmsMax = 0;
		Integer cmsMax = 0;
		Integer rgoMax = 0;
		
		String v_iekachi = null;
		String v_pms = null;
		String v_cms = null;
		String v_rgo = null;
		
		if (userId == null) { return "redirect:/portal/login"; }
		Map<String, Object> userInfo = getLoginUserInoBySession(session);
		if (userInfo == null) {
			return redirectLoginPage;
		}
		setPermissionToModel(model, userInfo);
		if(session.getAttribute("accountRec") == null){
			//法人情報取得
			accountRec = accountRep.findOneBySfCorporationId((String) userInfo.getOrDefault("corporationid", null));
			session.setAttribute("accountRec",accountRec);
			if(accountRec != null){
				role_of_iekachi__c = (boolean)Util.ifMapNull(accountRec, "is_available_iekachi__c", false);
				role_of_pms__c = (boolean)Util.ifMapNull(accountRec, "is_available_pms__c", false);
				role_of_cms__c = (boolean)Util.ifMapNull(accountRec, "is_available_cms__c", false);
				role_of_r_de_go__c = (boolean)Util.ifMapNull(accountRec, "is_available_r_de_go__c", false);
				wkIbx = Util.ifMapNull(accountRec, "ibxauthoritylimit__c", 0);
				wkPms = Util.ifMapNull(accountRec, "pmsauthoritylimit__c", 0);
				wkCms = Util.ifMapNull(accountRec, "cmsauthoritylimit__c", 0);
				wkRgo = Util.ifMapNull(accountRec, "rgoauthoritylimit__c", 0);
				
				ibxMax = (int)(Double.parseDouble(wkIbx.toString()));
				pmsMax = (int)(Double.parseDouble(wkPms.toString()));
				cmsMax = (int)(Double.parseDouble(wkCms.toString()));
				rgoMax = (int)(Double.parseDouble(wkRgo.toString()));
				
				v_iekachi = role_of_iekachi__c ? "利用可" : "利用不可";
				v_pms = role_of_pms__c ? "利用可" : "利用不可";
				v_cms = role_of_cms__c ? "利用可" : "利用不可";
				v_rgo = role_of_r_de_go__c ? "利用可" : "利用不可";
				
				model.addAttribute("role_of_iekachi",v_iekachi);
				model.addAttribute("role_of_pms",v_pms);
				model.addAttribute("role_of_cms",v_cms);
				model.addAttribute("role_of_r_de_go",v_rgo);
			}else{
				
				ibxMax = 0;
				pmsMax = 0;
				cmsMax = 0;
				rgoMax = 0;
				
				v_iekachi = "利用不可";
				v_pms = "利用不可";
				v_cms = "利用不可";
				v_rgo = "利用不可";
				
				model.addAttribute("role_of_iekachi",v_iekachi);
				model.addAttribute("role_of_pms",v_pms);
				model.addAttribute("role_of_cms",v_cms);
				model.addAttribute("role_of_r_de_go",v_rgo);

			}
		}else{
			accountRec = (Map<String, Object>)session.getAttribute("accountRec");
			role_of_iekachi__c = (boolean)Util.ifMapNull(accountRec, "is_available_iekachi__c", false);
			role_of_pms__c = (boolean)Util.ifMapNull(accountRec, "is_available_pms__c", false);
			role_of_cms__c = (boolean)Util.ifMapNull(accountRec, "is_available_cms__c", false);
			role_of_r_de_go__c = (boolean)Util.ifMapNull(accountRec, "is_available_r_de_go__c", false);
			wkIbx = Util.ifMapNull(accountRec, "ibxauthoritylimit__c", 0);
			wkPms = Util.ifMapNull(accountRec, "pmsauthoritylimit__c", 0);
			wkCms = Util.ifMapNull(accountRec, "cmsauthoritylimit__c", 0);
			wkRgo = Util.ifMapNull(accountRec, "rgoauthoritylimit__c", 0);

			ibxMax = (int)(Double.parseDouble(wkIbx.toString()));
			pmsMax = (int)(Double.parseDouble(wkPms.toString()));
			cmsMax = (int)(Double.parseDouble(wkCms.toString()));
			rgoMax = (int)(Double.parseDouble(wkRgo.toString()));

			v_iekachi = role_of_iekachi__c ? "利用可" : "利用不可";
			v_pms = role_of_pms__c ? "利用可" : "利用不可";
			v_cms = role_of_cms__c ? "利用可" : "利用不可";
			v_rgo = role_of_r_de_go__c ? "利用可" : "利用不可";
			
			model.addAttribute("role_of_iekachi",v_iekachi);
			model.addAttribute("role_of_pms",v_pms);
			model.addAttribute("role_of_cms",v_cms);
			model.addAttribute("role_of_r_de_go",v_rgo);
			
		}
		//法人の利用権限をセッションに設定
		session.setAttribute("role_of_iekachi",v_iekachi);
		session.setAttribute("role_of_pms",v_pms);
		session.setAttribute("role_of_cms",v_cms);
		session.setAttribute("role_of_r_de_go",v_rgo);
		
		session.setAttribute("ibxMax",ibxMax);
		session.setAttribute("pmsMax",pmsMax);
		session.setAttribute("cmsMax",cmsMax);
		session.setAttribute("rgoMax",rgoMax);
		
		model.addAttribute("user_info", userInfo);
		
		model.addAttribute("ibx_URL",serviceIbxUrl);
		model.addAttribute("kdx_URL",serviceKdxUrl);
		model.addAttribute("rgo_URL",serviceRgoUrl);
		
		return "portal/service";
	}

}
