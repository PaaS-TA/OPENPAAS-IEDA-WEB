package org.openpaas.ieda.api;

import static org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter.DEFAULT_CHARSET;

import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLContext;

import lombok.Data;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.openpaas.ieda.web.config.setting.DirectorException;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author "Cheolho, Moon <chmoon93@gmail.com / Cloud4U, Inc>"
 *
 */

@Data
public class DirectorClientBuilder {
    private String   host;
    private Integer port;
    private String   username;
    private String   password;
    
    final private int TIMEOUT_READ = 1000 * 10;
    final private int TIMEOUT_CONNECTION = 1000 * 10;
    
    public DirectorClientBuilder withHost(String host, Integer port){
        this.host = host;
        this.port = port;
        return this;
    }
    
    public DirectorClientBuilder withCredentials(String username, String password){
        this.username = username;
        this.password = password;
        return this;
    }
    
    public DirectorClient build(){
		URI root = UriComponentsBuilder.newInstance().scheme("https")
				.host(host).port(port).build().toUri();
         
        HttpComponentsClientHttpRequestFactory requestFactory = createRequestFactory(host, port, username, password);
        requestFactory.setReadTimeout(TIMEOUT_READ);
        requestFactory.setConnectionRequestTimeout(TIMEOUT_CONNECTION);
        
		RestTemplate restTemplate = new RestTemplate(requestFactory);
        
        restTemplate.getInterceptors().add(new ContentTypeClientHttpRequestInterceptor());
        handleTextHtmlResponses(restTemplate);
        
        return new DirectorClient(root, restTemplate);
    }
    
    //private ClientHttpRequestFactory createRequestFactory(String host, int port, String username,
    private HttpComponentsClientHttpRequestFactory createRequestFactory(String host, int port, String username, String password) {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(host, port),
                new UsernamePasswordCredentials(username, password));

        SSLContext sslContext = null;
        try {
            sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, new TrustSelfSignedStrategy()).useTLS().build();
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            throw new DirectorException("Unable to configure ClientHttpRequestFactory", e);
        }

        SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext,
                new AllowAllHostnameVerifier());

        HttpClient httpClient = HttpClientBuilder.create().disableRedirectHandling()
                .setDefaultCredentialsProvider(credentialsProvider)
                .setSSLSocketFactory(connectionFactory).build();
        

        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    private void handleTextHtmlResponses(RestTemplate restTemplate) {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new StringHttpMessageConverter());
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        messageConverter.setSupportedMediaTypes(Arrays.asList(new MediaType("application", "json",
                DEFAULT_CHARSET), new MediaType("application", "*+json", DEFAULT_CHARSET),
                new MediaType("text", "html", DEFAULT_CHARSET)));
        messageConverters.add(messageConverter);
        restTemplate.setMessageConverters(messageConverters);
    }
    
    private static class ContentTypeClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                ClientHttpRequestExecution execution) throws IOException {
            ClientHttpResponse response = execution.execute(request, body);
            // some BOSH resources return text/plain and this modifies this response
            // so we can use Jackson
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return response;
        }

    }    
    
}
