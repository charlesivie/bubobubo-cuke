package uk.co.bubobubo.cuke.utils;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
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

	public static HttpResponse httpGet(String relativeUri) throws IOException {
		return httpGet(relativeUri, Collections.<String, String>emptyMap());
	}

	public static HttpResponse httpGet(String relativeUri, Map<String, String> headers) throws IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(relativeUri);
		addHeadersToMethod(headers, httpGet);
		return httpClient.execute(httpGet);
	}

	public static HttpResponse httpPost(String relativeUri, Map<String, Object> parameters) throws IOException {
		return httpPost(relativeUri, parameters, Collections.<String, String>emptyMap());
	}

	public static HttpResponse httpPost(String relativeUri, Map<String, Object> parameters, Map<String, String> headers) throws IOException {
		return httpPost(relativeUri, parameters, headers, null);

	}

	public static HttpResponse httpPost(String relativeUri, Map<String, Object> parameters, Map<String, String> headers, InputStream inputStream) throws IOException {

		DefaultHttpClient httpClient = new DefaultHttpClient();
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

		return httpClient.execute(httpPost);

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

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpDelete httpDelete = new HttpDelete(relativeUri);
		addHeadersToMethod(headers, httpDelete);
		return httpClient.execute(httpDelete);

	}

	public static HttpResponse httpPut(String relativeUri, Map<String, Object> parameters) throws IOException {
		return httpPut(relativeUri, parameters, Collections.<String, String>emptyMap());
	}

	public static HttpResponse httpPut(String relativeUri, Map<String, Object> parameters, Map<String, String> headers) throws IOException {
		return httpPut(relativeUri, parameters, headers, null);

	}

	public static HttpResponse httpPut(String relativeUri, Map<String, Object> parameters, Map<String, String> headers, InputStream is) throws IOException {

		DefaultHttpClient httpClient = new DefaultHttpClient();
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

		return httpClient.execute(httpPut);

	}

	public static HttpResponse httpHead(String relativeUri) throws IOException {
		return httpHead(relativeUri, Collections.<String, String>emptyMap());
	}

	public static HttpResponse httpHead(String relativeUri, Map<String, String> headers) throws IOException {

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpHead httpHead = new HttpHead(relativeUri);
		addHeadersToMethod(headers, httpHead);
		return httpClient.execute(httpHead);

	}

	public static HttpResponse httpOptions(String relativeUri) throws IOException {
		return httpOptions(relativeUri, Collections.<String, String>emptyMap());
	}

	public static HttpResponse httpOptions(String relativeUri, Map<String, String> headers) throws IOException {

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpOptions httpOptions = new HttpOptions(relativeUri);
		addHeadersToMethod(headers, httpOptions);
		return httpClient.execute(httpOptions);
	}

	public static HttpResponse httpTrace(String relativeUri) throws IOException {
		return httpTrace(relativeUri, Collections.<String, String>emptyMap());
	}

	public static HttpResponse httpTrace(String relativeUri, Map<String, String> headers) throws IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpTrace httpTrace = new HttpTrace(relativeUri);
		addHeadersToMethod(headers, httpTrace);
		return httpClient.execute(httpTrace);

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

}
