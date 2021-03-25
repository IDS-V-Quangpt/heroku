package jp.co.hyas.hpf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AwsConfig {
	@Value("${cloud.aws.credentials.accessKey}")
	private String accessKey;

	@Value("${cloud.aws.credentials.secretKey}")
	private String secretKey;

	@Value("${cloud.aws.region.static}")
	private String region;

	@Bean
	public BasicAWSCredentials basicAWSCredentials() {
		return new BasicAWSCredentials(accessKey, secretKey);
	}

	// https://qiita.com/hmatsu47/items/496a8a49c4bb089f3749
	// https://docs.aws.amazon.com/ja_jp/sdk-for-java/v1/developer-guide/credentials.html
	@Bean
	public AmazonS3 amazonS3Client(AWSCredentials awsCredentials) {
		return AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
				.withRegion(Regions.fromName(region)).build();
	}
}
