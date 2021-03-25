package jp.co.hyas.hpf.auth;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.settings.Saml2Settings;

import jp.co.hyas.hpf.database.base.BaseApiController;

@RestController
@RequestMapping("saml2")
public class SamlController extends BaseApiController {

	// SAML
	// --------------------------------------------
	// http://localhost:8080/saml2/metadata
	@RequestMapping("metadata")
	//public String metadata(HttpServletRequest request, HttpServletResponse response) {
	public ResponseEntity<String> metadata(HttpServletRequest request, HttpServletResponse response) {
		String metadata = null;
		HttpHeaders h = new HttpHeaders();
		try {
			Auth auth = new Auth();
			Saml2Settings settings = auth.getSettings();
			settings.setSPValidationOnly(true);
			metadata = settings.getSPMetadata();
			List<String> errors = Saml2Settings.validateMetadata(metadata);
			if (!errors.isEmpty()) {
				String errorMsgs = String.join(", ", errors);
				//response.setContentType("text/html; charset=UTF-8");
				//return "Error:" + errorMsgs;
		        h.add("Content-Type", "text/html; charset=UTF-8");
				return new ResponseEntity<>(errorMsgs, h, HttpStatus.OK);
			}
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			//response.setContentType("text/html; charset=UTF-8");
			//return "Exception:" + e.getLocalizedMessage();
			String errorMsgs = "Exception:" + e.getLocalizedMessage();
	        h.add("Content-Type", "text/html; charset=UTF-8");
			return new ResponseEntity<>(errorMsgs, h, HttpStatus.OK);
		}
		//response.setContentType("application/xhtml+xml");
        h.add("Content-Type", "application/xhtml+xml");
		return new ResponseEntity<>(metadata, h, HttpStatus.OK);
	}
}
