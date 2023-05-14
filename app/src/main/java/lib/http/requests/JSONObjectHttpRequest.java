package lib.http.requests;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


/**
 * ******************************************************************************************
 * Created by:          Sanjeet Chand
 * Created When:        04/05/2019
 * URL:                 https://www.linkedin.com/in/sanjeetchand/
 * <p>
 * Package Name:        lib.http
 * Job Number:          v1.0
 * Description:         This class is a utility subclass of {@link HttpRequest} used to make a http request as a JSON Content-Type and receive a {@link JSONObject} as response.
 * Dependencies:        {@link HttpRequest}
 * Change History:
 * Date       Name        Job Number      Description/Reason      Reviewed By      Review Date
 * ******************************************************************************************
 */
public class JSONObjectHttpRequest extends HttpRequest<JSONObject> {
    public JSONObjectHttpRequest(String url) {
        super(url);
    }

    public JSONObjectHttpRequest(String url, Map<String, String> parameters) {
        super(url, parameters);
    }

    public JSONObjectHttpRequest(String url, Method method) {
        super(url, method);
    }

    public JSONObjectHttpRequest(String url, Method method, Map<String, String> parameters) {
        super(url, method, parameters);
    }

    public JSONObjectHttpRequest(String url, Method method, Map<String, String> parameters, Map<String, String> headers) {
        super(url, method, parameters, headers);
    }

    public JSONObjectHttpRequest(String url, Method method, Map<String, String> parameters, ContentType contentType) {
        super(url, method, parameters, contentType);
    }

    public JSONObjectHttpRequest(String url, String fileUri, Map<String, String> parameters) throws Exception {
        super(url, fileUri, parameters);
    }

    public JSONObjectHttpRequest(String url, String fileUri, Map<String, String> parameters, Map<String, String> headers) throws Exception {
        super(url, fileUri, parameters, headers);
    }

    public JSONObjectHttpRequest(String url, String fileUri, Map<String, String> parameters, ContentType contentType) throws Exception {
        super(url, fileUri, parameters, contentType);
    }

    @Override
    protected void parseResponse(String rawResponse) throws JSONException {
        setResponse(new JSONObject(rawResponse));
    }
}
