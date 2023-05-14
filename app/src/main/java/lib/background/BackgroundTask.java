package lib.background;

import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ******************************************************************************************
 * Created by:          Sanjeet Chand
 * Created When:        26/06/2022
 * URL:                 https://www.linkedin.com/in/sanjeetchand/
 * <p>
 * Package Name:        lib.background
 * Job Number:          v568.00
 * Description:         This class is a utility class for executing long running task in the
 * background thread using ExecutorService. Refer to: https://developer.android.com/guide/background/threading
 * Dependencies:        {@link Handler}, {@link ExecutorService}, {@link ResponseListener}
 * Change History:
 * Date       Name        Job Number      Description/Reason      Reviewed By      Review Date
 * ******************************************************************************************
 */
public class BackgroundTask {

    public static <T> void execute(ExecutorService executorService, Handler handler, Callable<T> function, ResponseListener<T> listener) {
        executorService.execute(() -> {
            try {
                T response = function.call();
                notifyResponse(handler, listener, response);
            } catch (Exception exception) {
                notifyError(handler, listener, exception);
            }
        });
    }

    public static <T> void execute(ExecutorService executorService, Callable<T> function, ResponseListener<T> listener) {
        Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());
        execute(executorService, handler, function, listener);
    }

    public static <T> void execute(Callable<T> function, ResponseListener<T> listener) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        execute(executorService, function, listener);
    }

    public static <T> void execute(Callable<T> function) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        execute(executorService, function, null);
    }

    public static <T> void execute(ExecutorService executorService, Handler handler, Runnable runnable, ResponseListener<T> listener) {
        executorService.execute(() -> {
            try {
                runnable.run();
                notifyResponse(handler, listener, null);
            } catch (Exception exception) {
                notifyError(handler, listener, exception);
            }
        });
    }

    public static <T> void execute(ExecutorService executorService, Runnable runnable, ResponseListener<T> listener) {
        Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());
        execute(executorService, handler, runnable, listener);
    }

    public static <T> void execute(Runnable runnable, ResponseListener<T> listener) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());
        execute(executorService, handler, runnable, listener);
    }

    public static <T> void execute(Runnable runnable) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        execute(executorService, null, runnable, null);
    }

    private static <T> void notifyResponse(Handler handler, ResponseListener<T> listener, T response) {
        if (handler != null) {
            handler.post(() -> {
                if (listener != null) {
                    listener.onResponse(response);
                }
            });
        }
    }

    private static <T> void notifyError(Handler handler, ResponseListener<T> listener, Exception exception) {
        if (handler != null) {
            handler.post(() -> {
                if (listener != null) {
                    listener.onError(exception);
                }
            });
        }
    }
}
