package cz.xtf.core.http;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Slf4j
public class Https {

	public static int httpGetCode(String url) {
		return httpGetCode(Https.urlFromString(url));
	}

	public static int httpGetCode(URL url) {
		HttpURLConnection connection = Https.getHttpConnection(url);
		return Https.getConnectionCode(connection);
	}

	public static String httpGetContent(String url) {
		return httpGetContent(Https.urlFromString(url));
	}

	public static String httpGetContent(URL url) {
		HttpURLConnection connection = Https.getHttpConnection(url);
		return Https.getConnectionContent(connection);
	}

	private static HttpURLConnection getHttpConnection(URL url) {
		try {
			HttpURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			return connection;
		} catch (ProtocolException e) {
			throw new IllegalStateException("Seems that time and IT has changed. Please contact creators for feature update!", e);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public static int httpsGetCode(String url) {
		return httpsGetCode(Https.urlFromString(url));
	}

	public static int httpsGetCode(URL url) {
		HttpsURLConnection connection = Https.getHttpsConnection(url);
		return Https.getConnectionCode(connection);
	}

	public static String httpsGetContent(String url) {
		return httpsGetContent(Https.urlFromString(url));
	}

	public static String httpsGetContent(URL url) {
		HttpsURLConnection connection = Https.getHttpsConnection(url);
		return Https.getConnectionContent(connection);
	}

	private static HttpsURLConnection getHttpsConnection(URL url) {
		try {
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, new TrustManager[]{new TrustAllManager()}, new SecureRandom());

			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setSSLSocketFactory(sslContext.getSocketFactory());
			connection.setHostnameVerifier((s, session) -> true);
			connection.setRequestMethod("GET");

			return connection;
		} catch (NoSuchAlgorithmException | KeyManagementException | ProtocolException e) {
			throw new IllegalStateException("Seems that time and IT has changed. Please contact creators for feature update!", e);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private static int getConnectionCode(HttpURLConnection connection) {
		try {
			connection.connect();

			int code = connection.getResponseCode();

			connection.disconnect();
			return code;
		} catch (IOException e) {
			throw new IllegalStateException();
		}
	}

	private static String getConnectionContent(HttpURLConnection connection) {
		try {
			connection.connect();

			String content = Https.readContent(connection.getInputStream());

			connection.disconnect();
			return content;
		} catch (IOException e) {
			throw new IllegalStateException();
		}
	}

	private static String readContent(InputStream inputStream) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder content = new StringBuilder();
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine).append("\n");
		}
		in.close();
		return content.toString();
	}

	private static URL urlFromString(String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new IllegalStateException("Ivalid url: " + url);
		}
	}

	public static class TrustAllManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException { }

		@Override
		public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException { }

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}
}
