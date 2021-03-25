package jp.co.hyas.hpf.storage;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;

import jp.co.hyas.hpf.database.base.ApiResult;
import jp.co.hyas.hpf.database.base.AppConst;
import jp.co.hyas.hpf.database.base.BaseApiController;
//*/
import xyz.downgoon.snowflake.Snowflake;

@RestController
@RequestMapping("storage")
public class AwsS3RestController extends BaseApiController {

	@Value("${cloud.aws.region.bucket}")
	public String BUCKET_NAME;
	final static String FOLDER_PATH = "hpf-storage/";
	final static int S3URL_EXPIRE_MIN = 5; // URLの有効時間: 5分
	// ID生成
	// datacenter: AppConst.IDG_MACHINEID; workerId: 1 // 0-31
	static Snowflake snowflake = new Snowflake(AppConst.IDG_MACHINEID, 29);
	public String generateObjectKey() {
		return "AWS" + snowflake.nextId(); //return "A" + super.generateId();
	}
	public GeneratePresignedUrlRequest createPresignedUrlRequest(String objectKey, int minute) {
		// Set the pre-signed URL to expire after one hour.
		java.util.Date expiration = new java.util.Date();
		long expTimeMillis = expiration.getTime();
		expTimeMillis += 1000 * 60 * minute;
		expiration.setTime(expTimeMillis);
		// Generate the pre-signed URL.
		GeneratePresignedUrlRequest generatePresignedUrlRequest =
			new GeneratePresignedUrlRequest(BUCKET_NAME, objectKey)
		//.withMethod(HttpMethod.PUT)
		.withExpiration(expiration);
		// http://zstd.github.io/amazon-presigned-url/
		// if (false)
		// 	generatePresignedUrlRequest.addRequestParameter(
		// 		Headers.S3_CANNED_ACL, CannedAccessControlList.AuthenticatedRead.toString());
		// //		Headers.S3_CANNED_ACL, CannedAccessControlList.PublicRead.toString());
		return generatePresignedUrlRequest;
	}

	// object_keyのパラメータ未指定エラー処理
	public ApiResult checkParam(String objectKey) {
		if (objectKey == null || "".equals(objectKey)) {
			ApiResult failure = this.getResultForError(null);
			failure.put("error_kind", "request-error");
			failure.put("error_message", "パラメータ{object_key}が未指定です");
			return failure;
		}
		return null;
	}

	// object_keyの存在チェックエラー処理
	// https://sysadmins.co.za/aws-java-sdk-detect-if-s3-object-exists-using-doesobjectexist/
	public ApiResult checkObjectExists(String objectKey) {
		if (!amazonS3Client.doesObjectExist(BUCKET_NAME, objectKey)) {
			ApiResult failure = this.getResultForError(null);
			failure.put("error_kind", "target-not-exists");
			failure.put("error_message", "対象の情報は存在しません");
			failure.put("error_detail", "object_key: " + objectKey);
			return failure;
		}
		return null;
	}

	@Autowired
    private AmazonS3 amazonS3Client;

	// API
	// --------------------------------------------

	// AWS S3アップロードURL取得API
	@RequestMapping(value="upload", method=GET)
	public ApiResult upload(@RequestParam Map<String, String> qs) {

		//String clientRegion = "*** Client region ***";
		String objectKey = FOLDER_PATH + generateObjectKey();

		String err_msg = null;
		URL url = null;
		GeneratePresignedUrlRequest presignedUrlRequest = null;
		try {
			presignedUrlRequest = createPresignedUrlRequest(objectKey, S3URL_EXPIRE_MIN);
			//presignedUrlRequest.withContentType("application/octet-stream");
			//presignedUrlRequest.withContentType("image/jpg");
			presignedUrlRequest.withMethod(HttpMethod.PUT);
			url = amazonS3Client.generatePresignedUrl(presignedUrlRequest);
			//System.err.println("Generating pre-signed URL:" + url);
		}
		//catch(AmazonServiceException e) {
		//catch(SdkClientException e) {
		catch(Exception e) {
			err_msg = e.getLocalizedMessage();
			e.printStackTrace();
		}
		if (url == null) {
			ApiResult failure = this.getResultForError(null);
			failure.put("error_kind", "aws-error");
			if (err_msg != null)
				failure.put("error_message", err_msg);
			return failure;
		}

		ApiResult body = new ApiResult();
		body.put("object_key", objectKey);
		body.put("upload_url", url.toString());
		body.put("expire", presignedUrlRequest.getExpiration().getTime());
		body.put("expire_info", presignedUrlRequest.getExpiration().toString());
		ApiResult result = this.getResultForSuccess(body);
		return result;
	}

	// AWS S3ダウンロードURL取得API
	@RequestMapping(value="download", method=POST)
	public ApiResult download(@RequestBody Map<String, Object> json) {
		String objectKey = (String)json.getOrDefault("object_key", null);
		String filename = (String)json.getOrDefault("filename", null);

		String err_msg = null;

		// object_keyのパラメータ未指定エラー処理
		ApiResult failure = checkParam(objectKey);
		if (failure != null) return failure;

		URL url = null;
		GeneratePresignedUrlRequest presignedUrlRequest = null;
		try {
			// object_keyの存在チェックエラー処理
			//failure = checkObjectExists(objectKey);
			//if (failure != null) return failure;

			presignedUrlRequest = createPresignedUrlRequest(objectKey, S3URL_EXPIRE_MIN);
			if (filename != null) {
				String newFilename = URLEncoder.encode(filename, "UTF8");
				ResponseHeaderOverrides headers = new ResponseHeaderOverrides();
				headers.withContentDisposition("attachment;filename=" + newFilename);
				presignedUrlRequest.withResponseHeaders(headers);
			}
			presignedUrlRequest.withMethod(HttpMethod.GET);
			url = amazonS3Client.generatePresignedUrl(presignedUrlRequest);
			//System.err.println("Generating pre-signed URL:" + url);
		}
		//catch(AmazonServiceException e) {
		//catch(SdkClientException e) {
		catch(Exception e) {
			err_msg = e.getLocalizedMessage();
			e.printStackTrace();
		}
		if (url == null) {
			failure = this.getResultForError(null);
			failure.put("error_kind", "aws-error");
			if (err_msg != null)
				failure.put("error_message", err_msg);
			return failure;
		}

		ApiResult body = new ApiResult();
		body.put("object_key", objectKey);
		body.put("download_url", url.toString());
		if (filename != null)
			body.put("download_filename", filename);
		body.put("expire", presignedUrlRequest.getExpiration().getTime());
		body.put("expire_info", presignedUrlRequest.getExpiration().toString());
		ApiResult result = this.getResultForSuccess(body);
		return result;
	}


	// AWS S3ファイル削除API
	@RequestMapping(value="delete", method=POST)
	public ApiResult delete(@RequestBody Map<String, Object> json) {
		String objectKey = (String)json.getOrDefault("object_key", null);
		// object_keyのパラメータ未指定エラー処理
		ApiResult failure = checkParam(objectKey);
		if (failure != null) return failure;

		String err_msg = null;

		try {
			// object_keyの存在チェックエラー処理
//			failure = checkObjectExists(objectKey);
//			if (failure != null) return failure;

			amazonS3Client.deleteObject(new DeleteObjectRequest(BUCKET_NAME, objectKey));
		}
		//catch(AmazonServiceException e) {
		//catch(SdkClientException e) {
		catch(Exception e) {
			err_msg = e.getLocalizedMessage();
			e.printStackTrace();

			failure = this.getResultForError(null);
			failure.put("error_kind", "aws-error");
			failure.put("error_message", err_msg);
			return failure;
		}

		ApiResult body = new ApiResult();
		body.put("object_key", objectKey);
		ApiResult result = this.getResultForSuccess(body);
		return result;
	}

}
