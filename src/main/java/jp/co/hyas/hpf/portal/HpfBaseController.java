package jp.co.hyas.hpf.portal;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.util.UriComponentsBuilder;

import jp.co.hyas.hpf.database.User;
import jp.co.hyas.hpf.database.UserRepository;

public class HpfBaseController {

	@Value("${auth.app.code.HPF:HPF}")
	protected String appCodeHPF;

	@Autowired
	UserRepository userRepository;

	protected final String redirectTopPage = "redirect:/portal/service";
	protected final String redirectLoginPage = "redirect:/portal/login";

	public String isLogin(HttpSession session) {
		// ログイン済みであればTOP画面へリダイレクト
		String userId = (String) session.getAttribute("user_id");
		return userId;
	}

	public User getLoginUserBySession(HttpSession session) {
		String userId = (String) session.getAttribute("user_id");
		if (userId == null) {
			return null;
		}
		return userRepository.findOne(userId);
	}

	public Map<String, Object> getLoginUserInoBySession(HttpSession session) {
		return (Map<String, Object>) session.getAttribute("user_info");
	}

	public UriComponentsBuilder getFullURL(HttpServletRequest request, String path) {
		try {
			return UriComponentsBuilder.fromUri(new URI(request.getRequestURL().toString())).replacePath(path);
		} catch (URISyntaxException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} // .queryParam("token", token).build().encode().toUri();
		return null;
	}

	public String getKeyCode()
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String source = "HPF" + format.format(new Date());

		byte[] bytes;
		try {
			bytes = MessageDigest.getInstance("SHA-1").digest(source.getBytes());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
		String result = DatatypeConverter.printHexBinary(bytes);

		return result;
	}

	public void setPermissionToModel(Model model, Map<String, Object> userInfo) {
		// userInfo
		// {
		//    user_id=0050l000001MKTKAA4,  contactid=null, corporationid=null,
		//    user_kind=HyAS,  hpf_admin=null,
		//    user_name=s.tamura@solt-inc.com,
		//    email=s.tamura@solt-inc.com,
		//    usage_auth_iekachi=管理者, usage_auth_cms=管理者,
		//    usage_auth_r_de_go=管理者, usage_auth_pms=管理者,
		//    firstname=null, lastname=田村, username=田村
		// }
		// {
		//   user_id=0050l000001MKTKAA4, contactid=CON369394449841922040, corporationid=S10000005,
		//   user_kind=加盟店, hpf_admin=owner,
		//   user_name=s.tamura@solt-inc.com,
		//   email=,
		//   usage_auth_iekachi=利用可, usage_auth_cms=利用可,
		//   usage_auth_r_de_go=利用不可,  usage_auth_pms=利用可,
		//   firstname=秀一, lastname=田村, username=田村秀一
		// }
		// 0は不可
		// model.addAttribute("user_info", userInfo);

		// プロフィール管理: HyASユーザは利用不可
		//if (!"HyAS".equals(userInfo.getOrDefault("user_kind", null))) {
			model.addAttribute("pm_use_profile", 1);
		//} else {
		//	model.addAttribute("pm_use_profile", 0);
		//}

		// 担当者管理
		if (userInfo.getOrDefault("corporationid", null) != null) {
			model.addAttribute("pm_use_contact", 1);
		} else {
			model.addAttribute("pm_use_contact", 0);
		}

		// 会社情報管理
		if (userInfo.getOrDefault("corporationid", null) != null
			&& ("owner".equals(userInfo.get("hpf_admin")) || "admin".equals(userInfo.get("hpf_admin")))) {
			// 会社情報編集
			// Owner:  可能
			// Admin:  不可
			// Member:  不可
			model.addAttribute("pm_use_account", 1);
		} else {
			model.addAttribute("pm_use_account", 0);
		}

		//
		// 担当者新規登録
		// Owner:  可能
		// Admin:  可能
		// Member:  不可
		if ("owner".equals(userInfo.get("hpf_admin"))
				|| "admin".equals(userInfo.get("hpf_admin"))) {
			model.addAttribute("pm_contact_regist_new", 1);
		} else {
			model.addAttribute("pm_contact_regist_new", 0);
		}
	}
}
