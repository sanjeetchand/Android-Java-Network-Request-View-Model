package lib.background;

public interface ResponseListener<T> {
    void onResponse(T response);

    void onError(Exception ex);
}
