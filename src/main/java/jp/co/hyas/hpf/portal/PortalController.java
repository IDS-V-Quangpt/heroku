package jp.co.hyas.hpf.portal;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.util.UriComponentsBuilder;

public class PortalController {

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
		} // .queryParam("token", token).build().encode().toUri();
		return null;
	}
}
