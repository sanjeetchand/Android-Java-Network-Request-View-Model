package lib.http.requests;

import java.util.Map;

public class StringHttpRequest extends HttpRequest<String> {
    /**
     * This will peform a Http GET request with the specified url
     *
     * @param url - full url of the rest api endpoint
     */
    public StringHttpRequest(String url) {
        super(url);
    }

    public StringHttpRequest(String url, Map<String, String> parameters) {
        super(url, parameters);
    }

    public StringHttpRequest(String url, Method method) {
        super(url, method);
    }

    public StringHttpRequest(String url, Method method, Map<String, String> parameters) {
        super(url, method, parameters);
    }

    public StringHttpRequest(String url, Method method, Map<String, String> parameters, Map<String, String> headers) {
        super(url, method, parameters, headers);
    }

    public StringHttpRequest(String url, Method method, Map<String, String> parameters, ContentType contentType) {
        super(url, method, parameters, contentType);
    }

    public StringHttpRequest(String url, String fileUri, Map<String, String> parameters) throws Exception {
        super(url, fileUri, parameters);
    }

    public StringHttpRequest(String url, String fileUri, Map<String, String> parameters, Map<String, String> headers) throws Exception {
        super(url, fileUri, parameters, headers);
    }

    public StringHttpRequest(String url, String fileUri, Map<String, String> parameters, ContentType contentType) throws Exception {
        super(url, fileUri, parameters, contentType);
    }

    @Override
    protected void parseResponse(String rawResponse) {
        setResponse(rawResponse);
    }
}