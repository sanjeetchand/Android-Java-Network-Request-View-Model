package lib.http.requests;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.util.Map;


/**
 * ******************************************************************************************
 * Created by:          Sanjeet Chand
 * Created When:        26/06/2022
 * URL:                 https://www.linkedin.com/in/sanjeetchand/
 * <p>
 * Package Name:        lib.http.requests
 * Job Number:          v568.00
 * Description:         This class is used to parse the file byte array to a bitmap when downloading photos
 * Dependencies:        {@link Bitmap}, {@link BitmapFactory}, {@link Base64}
 * Change History:
 * Date       Name        Job Number      Description/Reason      Reviewed By      Review Date
 * ******************************************************************************************
 */
public class BitmapRequest extends HttpRequest<Bitmap> {
    public BitmapRequest(String url) {
        super(url);
    }

    public BitmapRequest(String url, Method method) {
        super(url, method);
    }

    public BitmapRequest(String url, Map<String, String> parameters) {
        super(url, parameters);
    }

    public BitmapRequest(String url, Method method, Map<String, String> parameters) {
        super(url, method, parameters);
    }

    public BitmapRequest(String url, Method method, Map<String, String> parameters, Map<String, String> headers) {
        super(url, method, parameters, headers);
    }

    public BitmapRequest(String url, Method method, Map<String, String> parameters, ContentType contentType) {
        super(url, method, parameters, contentType);
    }

    public BitmapRequest(String url, String fileUri, Map<String, String> parameters) throws Exception {
        super(url, fileUri, parameters);
    }

    public BitmapRequest(String url, String fileUri, Map<String, String> parameters, Map<String, String> headers) throws Exception {
        super(url, fileUri, parameters, headers);
    }

    public BitmapRequest(String url, String fileUri, Map<String, String> parameters, ContentType contentType) throws Exception {
        super(url, fileUri, parameters, contentType);
    }

    @Override
    protected void parseResponse(String rawResponse) throws Exception {
        if (rawResponse != null && !rawResponse.isEmpty()) {
            byte[] imageBytes = Base64.decode(rawResponse, Base64.DEFAULT);
            if (imageBytes != null && imageBytes.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                setResponse(bitmap);
            }
        }
    }
}
