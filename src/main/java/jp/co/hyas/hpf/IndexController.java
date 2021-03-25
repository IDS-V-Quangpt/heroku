package jp.co.hyas.hpf;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class IndexController {
	@RequestMapping("/")
	public String index() {
		return "redirect:/portal/";
		//return "redirect:/demo/";
	}
	@RequestMapping("demo")
	public String dummy() {
		return "redirect:/portal/";
	}

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
    private AmazonS3 amazonS3Client;

	@SuppressWarnings("unchecked")
	@RequestMapping("demo2")
	public String demo(HttpServletRequest request, Model model) {
		Logger logger = LoggerFactory.getLogger(IndexController.class);

		// 認証情報取得API呼び出し
		URI uptarget = getFullURL(request, "storage/upload").build().encode().toUri();

		// 認証情報取得API呼び出し
		URI dltarget = getFullURL(request, "storage/download").build().encode().toUri();
		//Map<String, Object> apiResult = apiCallAuth(apitarget);

		ObjectMapper mapper = new ObjectMapper();


		try {

			// upload
			String up_json = restTemplate.getForObject(uptarget, String.class);
			Map<String,Object> up_map = new HashMap<String,Object>();

			//convert JSON string to Map
			up_map = mapper.readValue(up_json, new TypeReference<LinkedHashMap<String,Object>>(){});

			up_map = (Map<String, Object>) up_map.get("response_body");
			String upload_url = (String) up_map.get("upload_url");
			String object_key = (String) up_map.get("object_key");
			model.addAttribute("upload_api_url", uptarget);
			model.addAttribute("up_json", up_json);
			model.addAttribute("upload", up_map);

			System.out.println("object_key:" + object_key);

			// upload file
			writeS3File2(upload_url, up_json);

			// if (false) {
			// 	AccessControlList acl = amazonS3Client.getObjectAcl(AwsS3RestController.BUCKET_NAME, object_key);
			// 	acl.grantPermission(new CanonicalGrantee(acl.getOwner().getId()), Permission.FullControl);
			// 	amazonS3Client.setObjectAcl(AwsS3RestController.BUCKET_NAME, object_key, acl);
			// }

			// download
			Map<String,Object> dl_param = new HashMap<String,Object>();
			dl_param.put("object_key", object_key);
			dl_param.put("filename", "ダウンロードファイル名" + (new Date()).getTime() + ".txt");
			ObjectMapper objectMapper = new ObjectMapper();
			String requestJson = objectMapper.writeValueAsString(dl_param);
			//String requestJson = "{\"object_key\":\"" + object_key + "\",\"filename\":\"ダウンロードファイル名" + (new Date()).getTime() + ".txt\"}";
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			//headers.setContentType(MediaType.APPLICATION_JSON);

			//import org.springframework.http.HttpEntity;
			HttpEntity<String> entity = new HttpEntity<String>(requestJson,headers);
			String dl_json = restTemplate.postForObject(dltarget, entity, String.class);
			Map<String,Object> dl_map = new HashMap<String,Object>();

			dl_map = mapper.readValue(dl_json, new TypeReference<LinkedHashMap<String,Object>>(){});
			dl_map = (Map<String, Object>) dl_map.get("response_body");
			String download_url = (String) dl_map.get("download_url");

			model.addAttribute("download_api_url", dltarget);
			model.addAttribute("download_api_param", requestJson);
			model.addAttribute("dl_json", dl_json);
			model.addAttribute("download", dl_map);

			readS3File(download_url);

			//writeS3File2(upload_url, json);


			//json = mapper.writeValueAsString(map);
		} catch (Exception e) {
			logger.info("Exception; {}", e);
		}
		return "demo/index";
	}

	// =======================================================================
	public String readS3File(String url) throws ClientProtocolException, IOException {
		System.out.println(this.getClass().toString() + "▼処理開始▼");
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet getMethod = new HttpGet(url);
		//putMethod.addHeader("Content-Type","image/jpg");
		String v = "";
		int statusCode = -1;
		try (CloseableHttpResponse response = client.execute(getMethod)) {
			statusCode = response.getStatusLine().getStatusCode();
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				org.apache.http.HttpEntity entity = response.getEntity();
				v = EntityUtils.toString(entity, StandardCharsets.UTF_8);
				System.out.println("response:" + v);
			}
		}
		System.out.println("HTTP response code: " + statusCode);
		System.out.println(this.getClass().toString() + "▲処理終了▲");
		return v;
    }
	public boolean writeS3File2(String url, String text) throws ClientProtocolException, IOException  {
		System.out.println(this.getClass().toString() + "▼処理開始▼");
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPut putMethod = new HttpPut(url);
		//putMethod.addHeader("Content-Type","image/jpg");
		// https://docs.aws.amazon.com/ja_jp/AmazonS3/latest/dev/acl-overview.html
		//putMethod.addHeader("x-amz-acl","public-read");
		//putMethod.addHeader("x-amz-acl","authenticated-read");
		//putMethod.addHeader("x-amz-acl","private");
		putMethod.setEntity(new StringEntity(text,StandardCharsets.UTF_8));

		int statusCode = -1;
		try (CloseableHttpResponse response = client.execute(putMethod)) {
			statusCode = response.getStatusLine().getStatusCode();
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				org.apache.http.HttpEntity entity = response.getEntity();
				System.out.println("response:" + EntityUtils.toString(entity, StandardCharsets.UTF_8));
			}
		}
		System.out.println("HTTP response code: " + statusCode);
		System.out.println(this.getClass().toString() + "▲処理終了▲");
		return true;
    }

	public boolean writeS3File(String url, String text) throws MalformedURLException, IOException {
		HttpURLConnection connection = (HttpURLConnection) (new URL(url)).openConnection();
		//connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestMethod("PUT");
		//connection.setRequestProperty("Content-Type","application/octet-stream");
		connection.connect();
		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
		out.write("This text uploaded as an object via presigned URL." + "\r\n" + text);
		out.close();
		System.out.println("HTTP response code: " + connection.getResponseCode());
		return true;
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
}