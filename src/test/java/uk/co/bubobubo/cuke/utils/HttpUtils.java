package uk.co.bubobubo.cuke.utils;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class HttpUtils {


	private static HttpContext httpContext;
	private static boolean inSession = false;

	public static HttpResponse httpGet(String relativeUri) throws IOException {
		return httpGet(relativeUri, Collections.<String, String>emptyMap());
	}

	public static HttpResponse httpGet(String relativeUri, Map<String, String> headers) throws IOException {

		HttpGet httpGet = new HttpGet(relativeUri);
		addHeadersToMethod(headers, httpGet);
		return execute(httpGet);
	}

	public static HttpResponse httpPost(String relativeUri, Map<String, Object> parameters) throws IOException {
		return httpPost(relativeUri, parameters, Collections.<String, String>emptyMap());
	}

	public static HttpResponse httpPost(String relativeUri, Map<String, Object> parameters, Map<String, String> headers) throws IOException {
		return httpPost(relativeUri, parameters, headers, null);

	}

	public static HttpResponse httpPost(String relativeUri, Map<String, Object> parameters, Map<String, String> headers, InputStream inputStream) throws IOException {

		HttpPost httpPost = new HttpPost(relativeUri);
		if (parameters != null) {
			List<BasicNameValuePair> p = new ArrayList<BasicNameValuePair>();
			for (Map.Entry<String, Object> entry : parameters.entrySet()) {
				p.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
			}

			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(p);
			urlEncodedFormEntity.setContentEncoding("UTF-8");
			httpPost.setEntity(urlEncodedFormEntity);
		}
		addHeadersToMethod(headers, httpPost);

		addBodyToClient(inputStream, httpPost);

		return execute(httpPost);

	}


	private static void addBodyToClient(InputStream inputStream, HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase) {
		if (inputStream == null) {
			return;
		}
		try {
			httpEntityEnclosingRequestBase.setEntity(new StringEntity(isToString(inputStream)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static HttpResponse httpDelete(String relativeUri) throws IOException {
		return httpDelete(relativeUri, Collections.<String, String>emptyMap());
	}

	public static HttpResponse httpDelete(String relativeUri, Map<String, String> headers) throws IOException {

		HttpDelete httpDelete = new HttpDelete(relativeUri);
		addHeadersToMethod(headers, httpDelete);
		return execute(httpDelete);

	}

	public static HttpResponse httpPut(String relativeUri, Map<String, Object> parameters) throws IOException {
		return httpPut(relativeUri, parameters, Collections.<String, String>emptyMap());
	}

	public static HttpResponse httpPut(String relativeUri, Map<String, Object> parameters, Map<String, String> headers) throws IOException {
		return httpPut(relativeUri, parameters, headers, null);

	}

	public static HttpResponse httpPut(String relativeUri, Map<String, Object> parameters, Map<String, String> headers, InputStream is) throws IOException {

		HttpPut httpPut = new HttpPut(relativeUri);
		HttpParams httpParams = new BasicHttpParams();
		if (parameters != null) {
			for (String parameterName : parameters.keySet()) {
				httpParams.setParameter(parameterName, parameters.get(parameterName));
			}
		}
		httpPut.setParams(httpParams);
		addHeadersToMethod(headers, httpPut);

		addBodyToClient(is, httpPut);

		return execute(httpPut);

	}

	public static HttpResponse httpHead(String relativeUri) throws IOException {
		return httpHead(relativeUri, Collections.<String, String>emptyMap());
	}

	public static HttpResponse httpHead(String relativeUri, Map<String, String> headers) throws IOException {

		HttpHead httpHead = new HttpHead(relativeUri);
		addHeadersToMethod(headers, httpHead);
		return execute(httpHead);

	}

	public static HttpResponse httpOptions(String relativeUri) throws IOException {
		return httpOptions(relativeUri, Collections.<String, String>emptyMap());
	}

	public static HttpResponse httpOptions(String relativeUri, Map<String, String> headers) throws IOException {

		HttpOptions httpOptions = new HttpOptions(relativeUri);
		addHeadersToMethod(headers, httpOptions);
		return execute(httpOptions);
	}

	public static HttpResponse httpTrace(String relativeUri) throws IOException {
		return httpTrace(relativeUri, Collections.<String, String>emptyMap());
	}

	public static HttpResponse httpTrace(String relativeUri, Map<String, String> headers) throws IOException {

		HttpTrace httpTrace = new HttpTrace(relativeUri);
		addHeadersToMethod(headers, httpTrace);
		return execute(httpTrace);

	}

	private static void addHeadersToMethod(Map<String, String> headers, HttpRequestBase method) {
		if (headers != null) {
			for (String headerName : headers.keySet()) {
				method.addHeader(headerName, headers.get(headerName));
			}
		}
	}


	private static String isToString(InputStream in) throws IOException {
		return IOUtils.toString(in, "UTF-8");
	}

	public static void startSession() {
		httpContext = new BasicHttpContext();
		CookieStore cookieStore = new BasicCookieStore();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		inSession = true;
	}

	public static void endSession() {
		httpContext.removeAttribute(ClientContext.COOKIE_STORE);
		inSession = false;
	}


	private static HttpResponse execute(HttpRequestBase httpRequest) throws IOException {

		DefaultHttpClient httpClient = new DefaultHttpClient();
		if (inSession) {
			return httpClient.execute(httpRequest, httpContext);
		}
		return httpClient.execute(httpRequest);
	}
}
