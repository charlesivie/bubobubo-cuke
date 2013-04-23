package uk.co.bubobubo.cuke.utils;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;
import uk.co.bubobubo.cuke.bean.RequestAttribute;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class HttpUtils {


    private static HttpContext httpContext;
    private static boolean inSession = false;
    public static String responseAsString;

    public static HttpResponse httpGet(String relativeUri, List<RequestAttribute> parameters) throws IOException, URISyntaxException {

        HttpGet method = new HttpGet(relativeUri);
        URIBuilder uriBuilder = new URIBuilder(method.getURI());

        for (RequestAttribute attribute : parameters) {
            if (attribute.getType().equalsIgnoreCase("HEADER")) {
                method.addHeader(attribute.getName(), attribute.getValue());
            }
            if (attribute.getType().equalsIgnoreCase("PARAMETER")) {
                uriBuilder.addParameter(attribute.getName(), attribute.getValue());
            }
        }

        method.setURI(uriBuilder.build());

        return execute(method);
    }

    public static HttpResponse httpPost(String relativeUri, List<RequestAttribute> parameters) throws IOException, URISyntaxException {


        HttpPost method = new HttpPost(relativeUri);
        URIBuilder uriBuilder = new URIBuilder(method.getURI());

        for (RequestAttribute attribute : parameters) {
            if (attribute.getType().equalsIgnoreCase("HEADER")) {
                method.addHeader(attribute.getName(), attribute.getValue());
            }
            if (attribute.getType().equalsIgnoreCase("PARAMETER")) {
                uriBuilder.addParameter(attribute.getName(), attribute.getValue());
            }
        }

        method.setURI(uriBuilder.build());

        return execute(method);
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
        httpContext = null;
        inSession = false;
    }


    private static HttpResponse execute(HttpRequestBase httpRequest) throws IOException {

        responseAsString = null;
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

        httpClient.setRedirectStrategy(new DefaultRedirectStrategy() {
            public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) {
                boolean isRedirect = false;
                try {
                    isRedirect = super.isRedirected(request, response, context);
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                if (!isRedirect) {
                    int responseCode = response.getStatusLine().getStatusCode();
                    if (responseCode == 301 || responseCode == 302) {
                        return true;
                    }
                }
                return isRedirect;
            }
        });

        HttpResponse response;
        if (inSession) {
            response = httpClient.execute(httpRequest, httpContext);
        } else {
            response = httpClient.execute(httpRequest);
        }
        return processResponse(response);
    }

    private static HttpResponse processResponse(HttpResponse response) throws IOException {
        if (response.getEntity() != null) {
            responseAsString = EntityUtils.toString(response.getEntity());
        } else responseAsString = null;
        return response;
    }
}
