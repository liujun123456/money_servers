package org.example.config;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

@Configuration
public class OkHttpConfig {

    @Value("${okhttp.connect-timeout:5000}")
    private int connectTimeout;

    @Value("${okhttp.read-timeout:10000}")
    private int readTimeout;

    @Value("${okhttp.write-timeout:10000}")
    private int writeTimeout;

    @Value("${okhttp.max-idle-connections:5}")
    private int maxIdleConnections;

    @Value("${okhttp.keep-alive-duration:5}")
    private int keepAliveDuration;

    @Value("${okhttp.retry-on-connection-failure:true}")
    private boolean retryOnConnectionFailure;

    /**
     * 创建OkHttpClient Bean
     */
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .connectionPool(new ConnectionPool(maxIdleConnections, keepAliveDuration, TimeUnit.MINUTES))
                .retryOnConnectionFailure(retryOnConnectionFailure)
                .sslSocketFactory(sslSocketFactory(), x509TrustManager())
                .hostnameVerifier((hostname, session) -> true) // 信任所有主机名
//                .addInterceptor(new LoggingInterceptor()) // 添加日志拦截器
//                .addInterceptor(new RetryInterceptor())   // 添加重试拦截器
                .build();
    }

    /**
     * SSL Socket Factory
     */
    @Bean
    public SSLSocketFactory sslSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{x509TrustManager()}, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to create SSL socket factory", e);
        }
    }

    /**
     * X509 Trust Manager
     */
    @Bean
    public X509TrustManager x509TrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }
}
