package test;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StreamUtils;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import io.reactivex.netty.RxNetty;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestHttpKeepAliveApplication.class)
@WebIntegrationTest("server.port:0")
public class KillConnectionControllerTest {

	@Value("${local.server.port}")
	private int port;

	private URI uri;

	@Before
	public void setup() throws Exception {
		this.uri = new URI("http", null, "localhost", port, "/", null, null);
	}

	@Test
	public void test_jre_url_connection_post() throws Exception {
		System.out.println("test_jre_url_connection_post");
		testJreUrlConnection("POST");
	}

	@Test
	public void test_jre_url_connection_get() throws Exception {
		System.out.println("test_jre_url_connection_get");
		testJreUrlConnection("GET");
	}

	private void testJreUrlConnection(String method) throws MalformedURLException, IOException, ProtocolException {
		URL url = uri.toURL();

		for (int i = 0; i < 20; i++) {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(method);
			try (InputStream inputStream = connection.getInputStream()) {
				StreamUtils.copy(inputStream, System.out);
			}
			System.out.println();
		}
	}

	@Test
	public void test_apache_http_client_get() throws Exception {
		System.out.println("test_apache_http_client_get");

		testApacheHttpClient(new HttpGet(uri));
	}

	@Test
	public void test_apache_http_client_post() throws Exception {
		System.out.println("test_apache_http_client_post");

		testApacheHttpClient(new HttpPost(uri));
	}

	private void testApacheHttpClient(HttpRequestBase request) throws Exception {
		HttpClientBuilder builder = HttpClients.custom().useSystemProperties().disableCookieManagement();

		// HttpRequestBase request = new HttpPost(uri);
		try (CloseableHttpClient httpClient = builder.build()) {
			for (int i = 0; i < 20; i++) {
				CloseableHttpResponse response = httpClient.execute(request);
				try (InputStream is = response.getEntity().getContent()) {
					StreamUtils.copy(is, System.out);
				}
				System.out.println();
			}
		}
	}

	@Test
	public void test_ok_http_client_get() throws Exception {
		System.out.println("test_ok_http_client_get");

		Request request = new Request.Builder().url(uri.toURL()).get().build();

		testOkHttpClient(request);
	}

	@Test
	public void test_ok_http_client_post() throws Exception {
		System.out.println("test_ok_http_client_post");
		RequestBody body = RequestBody.create(MediaType.parse("text/plain"), "toto");
		Request request = new Request.Builder().url(uri.toURL()).post(body).build();

		testOkHttpClient(request);
	}

	private void testOkHttpClient(Request request) throws IOException {
		OkHttpClient client = new OkHttpClient();

		for (int i = 0; i < 20; i++) {
			Response response = client.newCall(request).execute();
			try (InputStream is = response.body().byteStream()) {
				StreamUtils.copy(is, System.out);
			}
			System.out.println();
		}
	}

	@Test
	public void test_rxnetty_get() throws Exception {
		System.out.println("test_rxnetty_get");
		for (int i = 0; i < 20; i++) {
			String val = RxNetty.createHttpGet(uri.toString())//
					.flatMap(response -> response.getContent()//
							.<String> map(content -> content.toString(Charset.defaultCharset())))
					.toBlocking()//
					.toFuture().get();

			System.out.println(val);
		}

	}
}
