package org.openpaas.ieda.api.director.utility;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

public class ExSSLSocketFactory implements ProtocolSocketFactory {
	
	private TrustManager[] getTrustManager() {

		TrustManager[] trustAllCerts = new TrustManager[] {
			new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				public void checkClientTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
				}
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] certs, String authType) {
				}
			}
		};

		return trustAllCerts;
	}

	public Socket createSocket(String host, int port) throws IOException, UnknownHostException {

		TrustManager[] trustAllCerts = getTrustManager();

		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			SocketFactory socketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
			return socketFactory.createSocket(host, port);
		}
		catch (Exception ex) {
			throw new UnknownHostException("Problems to connect " + host + ex.toString());
		}

	}

	public Socket createSocket(Socket socket, String host, int port, boolean flag)
			throws IOException, UnknownHostException {

		TrustManager[] trustAllCerts = getTrustManager();

		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			SocketFactory socketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
			return socketFactory.createSocket(host, port);
		}
		catch (Exception ex) {
			throw new UnknownHostException("Problems to connect " + host + ex.toString());
		}
	}

	public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort)
			throws IOException, UnknownHostException {

		TrustManager[] trustAllCerts = getTrustManager();

		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			SocketFactory socketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
			return socketFactory.createSocket(host, port, clientHost, clientPort);
		}
		catch (Exception ex) {
			throw new UnknownHostException("Problems to connect " + host + ex.toString());
		}
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress localAddress, int localPort,
			HttpConnectionParams arg4) throws IOException, UnknownHostException, ConnectTimeoutException {
		TrustManager[] trustAllCerts = getTrustManager();

		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			SocketFactory socketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
			return socketFactory.createSocket(host, port);
		}
		catch (Exception ex) {
			throw new UnknownHostException("Problems to connect " + host + ex.toString());
		}
	}
}
