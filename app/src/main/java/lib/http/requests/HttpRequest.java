package lib.http.requests;

import android.util.Xml;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public abstract class HttpRequest<T> {
    public enum Method {
        GET,
        POST,
        PUT,
        DELETE
    }

    public enum ContentType {
        JSON,
        XML
    }

    private final String rootUrl;
    private URL url;
    private Method method = Method.GET;
    private ContentType contentType = ContentType.JSON;
    private HttpURLConnection connection;
    private Map<String, String> requestHeaders = new HashMap<>();
    private Map<String, String> parameters = new HashMap<>();
    private String fileUri;

    public int connectionTimeoutInSeconds = 0;
    public int readTimeoutInSeconds = 0;
    private T response;

    public HttpRequest(String url) {
        this.rootUrl = url;
    }

    public HttpRequest(String url, Method method) {
        this(url);
        this.method = method;
    }

    public HttpRequest(String url, Map<String, String> parameters) {
        this(url);
        this.parameters = parameters;
    }

    public HttpRequest(String url, Method method, Map<String, String> parameters) {
        this(url, method);
        this.parameters = parameters;
    }

    public HttpRequest(String url, Method method, Map<String, String> parameters, Map<String, String> headers) {
        this(url, method, parameters);
        this.requestHeaders = headers;
    }

    public HttpRequest(String url, Method method, Map<String, String> parameters, ContentType contentType) {
        this(url, method, parameters);
        this.contentType = contentType;
    }

    public HttpRequest(String url, String fileUri, Map<String, String> parameters) throws Exception {
        this(url, Method.POST, parameters);

        if (fileUri == null || fileUri.isEmpty()) {
            raiseFileUriNullOrEmptyException();
        }

        this.fileUri = fileUri;
    }

    public HttpRequest(String url, String fileUri, Map<String, String> parameters, Map<String, String> headers) throws Exception {
        this(url, fileUri, parameters);
        this.requestHeaders = headers;
    }

    public HttpRequest(String url, String fileUri, Map<String, String> parameters, ContentType contentType) throws Exception {
        this(url, fileUri, parameters);
        this.contentType = contentType;
    }

    public void setRequestHeaders(Map<String, String> headers) {
        this.requestHeaders = headers;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    protected void setResponse(T response) {
        this.response = response;
    }

    public T makeRequest() throws Exception {
        buildUrl();
        openConnection();
        setRequestHeaders();

        switch (method) {
            case GET: {
                connection.setRequestMethod("GET");
                break;
            }
            case POST: {
                //if fileUri is provided we try and perform file upload
                if (fileUri != null && !fileUri.isEmpty()) {
                    uploadFile();
                } else {
                    makePostRequest();
                }
                break;
            }
        }

        String rawResponse = readResponse();

        parseResponse(rawResponse);
        return response;
    }

    private void makePostRequest() throws IOException, JSONException {
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(getRequestBody());
        outputStream.flush();
        outputStream.close();
    }

    private void uploadFile() throws Exception {
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;

        //get file and start writing to output stream
        File file = new File(fileUri);

        if (file.isFile()) {
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("ENCTYPE", "multipart/form-data");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            connection.setRequestProperty("file", fileUri);

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());

            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + fileUri + "\"" + lineEnd);
            dataOutputStream.writeBytes(lineEnd);

            FileInputStream fileInputStream = new FileInputStream(file);

            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it to output stream
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dataOutputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dataOutputStream.writeBytes(lineEnd);

            //send remainder form data here
            if (parameters != null && parameters.size() > 0) {
                for (Map.Entry<String, String> param : parameters.entrySet()) {
                    if(param != null) {
                        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + param.getKey() + "\"" + lineEnd);
                        dataOutputStream.writeBytes(lineEnd);
                        dataOutputStream.writeBytes(param.getValue());
                        dataOutputStream.writeBytes(lineEnd);
                    }
                }
            }

            dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            fileInputStream.close();
            dataOutputStream.flush();
            dataOutputStream.close();
        } else {
            throw new Exception("The provided file Uri is not a file!");
        }
    }

    private byte[] getRequestBody() throws JSONException {
        if (parameters == null || parameters.size() == 0) {
            return new byte[]{};
        }
        JSONObject postParameters = new JSONObject();
        for (Map.Entry<String, String> param : parameters.entrySet()) {
            postParameters.put(param.getKey(), param.getValue());
        }
        return postParameters.toString().getBytes();
    }

    private String readResponse() throws Exception {
        String rawResponse = "";
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            switch (contentType) {
                case JSON: {
                    rawResponse = readStream(connection.getInputStream());
                    break;
                }
                case XML: {
                    rawResponse = readXmlResponse(connection.getInputStream());
                    break;
                }
            }
            return rawResponse;
        } else {
            String detailedHttpErrorMessage = buildHttpError(responseCode);
            throw new Exception(detailedHttpErrorMessage);
        }
    }

    private String readStream(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String lineRead;
        StringBuilder stringBuilder = new StringBuilder();
        while ((lineRead = bufferedReader.readLine()) != null) {
            stringBuilder.append(lineRead);
        }
        bufferedReader.close();
        closeConnection();
        return stringBuilder.toString();
    }

    /**
     * @param inputStream
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private String readXmlResponse(InputStream inputStream) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        InputStream readStream = new BufferedInputStream(inputStream);
        parser.setInput(readStream, null);
        parser.nextTag();
        parser.next();
        String rawResponse = parser.getText();
        readStream.close();
        closeConnection();
        return rawResponse;
    }

    /**
     * If there is any HTTP error response then it is read using this method. It also contains the request details for debugging.
     *
     * @param responseCode - the actual response code received from the server. For e.g. 200, 400 etc.
     * @return - Returns the error message as string, with the request details.
     * @throws IOException - An IOException is raised if the error cannot be read successfully.
     */
    private String buildHttpError(int responseCode) throws IOException {
        StringBuilder httpErrorBuilder = new StringBuilder("Server responded with code: ");
        httpErrorBuilder.append(responseCode).append("\n")
                .append("Request Details:\n")
                .append("URL: ").append(url).append("\n")
                .append("Method: ").append(connection.getRequestMethod()).append("\n");
        if (parameters != null) {
            httpErrorBuilder.append("Parameters: ").append(parameters.toString()).append("\n");
        }
        String httpError = readStream(connection.getErrorStream());
        httpErrorBuilder.append("Http Error Response: ").append(httpError).append("\n");
        return httpErrorBuilder.toString();
    }

    /**
     * Finally close the connection after reading the response.
     */
    private void closeConnection() {
        if (connection != null) {
            connection.disconnect();
            connection = null;
        }
    }

    /**
     * This converts the raw string response to it's corresponding type.
     *
     * @param rawResponse - String raw response received from the server
     * @throws Exception - Exception is raised if the raw response cannot be converted
     */
    protected abstract void parseResponse(String rawResponse) throws Exception;

    /**
     * Opens a {@link HttpURLConnection} using the supplied request URL
     * This is where the timeouts are set.
     * Make sure to call the set timeout methods [{@link #setConnectionTimeoutInSeconds(int)} or {@link #setReadTimeoutInSeconds(int)}] prior to calling the {@link #makePostRequest()} method
     *
     * @throws IOException - IOException or MalformedURLException is thrown
     */
    private void openConnection() throws IOException {
        System.setProperty("http.keepAlive", "false");
        connection = (HttpURLConnection) url.openConnection();
        if (connectionTimeoutInSeconds > 0) {
            connection.setConnectTimeout(connectionTimeoutInSeconds * 1000);
        }
        if (readTimeoutInSeconds > 0) {
            connection.setReadTimeout(readTimeoutInSeconds * 1000);
        }
    }

    /**
     * This method sets any request headers that need to be set on a HTTP GET or POST request.
     */
    private void setRequestHeaders() {
        if (connection != null) {
            switch (contentType) {
                case JSON: {
                    if (requestHeaders != null)
                        requestHeaders.put("Content-Type", "application/json");
                    break;
                }
                case XML: {
                    if (requestHeaders != null)
                        requestHeaders.put("Content-Type", "text/xml");
                    break;
                }
                default: {
                    break;
                }
            }

            if (requestHeaders != null && requestHeaders.size() > 0) {
                for (Map.Entry<String, String> property : requestHeaders.entrySet()) {
                    connection.setRequestProperty(property.getKey(), property.getValue());
                }
            }

        }
    }

    /**
     * Builds the URL object from String url. If the request is of type HTTP GET and parameters had been provided in the constructor
     * then, these parameters are also added after "?". The final url uses UTF-8 Encoding.
     *
     * @throws MalformedURLException
     * @throws UnsupportedEncodingException
     */
    private void buildUrl() throws MalformedURLException, UnsupportedEncodingException {
        StringBuilder urlBuilder = new StringBuilder(rootUrl);
        if (method == Method.GET && parameters != null && parameters.size() > 0) {
            urlBuilder.append("?");
            for (Map.Entry<String, String> param : parameters.entrySet()) {
                urlBuilder.append(param.getKey()).append("=").append(URLEncoder.encode(param.getValue(), "utf-8")).append("&");
            }
        }
        url = new URL(urlBuilder.toString());
    }

    /**
     * Sets a specified timeout value, in seconds,  This is converted to milliseconds when used to set URLConnection.setConnectTimeout  refer to {@link java.net.URLConnection#setConnectTimeout(int)}
     *
     * @param connectionTimeoutInSeconds - connection timeout in seconds
     */
    public void setConnectionTimeoutInSeconds(int connectionTimeoutInSeconds) {
        if (connectionTimeoutInSeconds > 0) {
            this.connectionTimeoutInSeconds = connectionTimeoutInSeconds;
        }
    }

    /**
     * Sets the read timeout to a specified timeout in seconds. This is converted to milliseconds when used to set URLConnection.setReadTimeout refer to {@link java.net.URLConnection#setReadTimeout(int)}
     *
     * @param readTimeoutInSeconds - read timeout in seconds
     */
    public void setReadTimeoutInSeconds(int readTimeoutInSeconds) {
        if (readTimeoutInSeconds > 0) {
            this.readTimeoutInSeconds = readTimeoutInSeconds;
        }
    }

    /**
     * It the request is constructed using the fileUri constructor, then the fileUri cannot be null nor can it be an empty string.
     *
     * @throws Exception - throws exception if fileUri is null or an empty string
     */
    private void raiseFileUriNullOrEmptyException() throws Exception {
        throw new Exception("Please provide a non-null value for fileUri, also ensure that it is not an empty string!");
    }
}
