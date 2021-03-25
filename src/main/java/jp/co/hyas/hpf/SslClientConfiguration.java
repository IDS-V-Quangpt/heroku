package jp.co.hyas.hpf;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SslClientConfiguration {

	// API呼び出しでのSSL通信時に証明書・ホスト名のチェックを行わない処理(開発localhost対応用)
    @Bean
    RestTemplate restTemplate() throws Exception {

    	// 最終的にはSSL証明書を使う通常版にさしかえ
		//RestTemplate restTemplate = new RestTemplate();
    	//return restTemplate;

    	// ホスト名の検証を行わない
        HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String s, SSLSession ses) {
                	if (!s.equals(ses.getPeerHost()))
                        System.out.println("[WARN] Hostname is not matched.");
                    System.out.println("SslClientConfiguration[HostnameVerifier]:" + s + " => " + ses.getPeerHost());
                    return true;
                }
        };


        // 証明書の検証を行わない
        KeyManager[] km = null;
        TrustManager[] tm = { new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }
                public X509Certificate[] getAcceptedIssuers() {
                        return null;
                }
        } };
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(km, tm, new SecureRandom());

        SSLConnectionSocketFactory socketFactory =
                new SSLConnectionSocketFactory(sslContext, hv);
        HttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(socketFactory).build();
        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(factory);
    }
}
