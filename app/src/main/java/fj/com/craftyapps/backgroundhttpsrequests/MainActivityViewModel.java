package fj.com.craftyapps.backgroundhttpsrequests;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import lib.background.BackgroundTask;
import lib.background.ResponseListener;
import lib.http.requests.StringHttpRequest;
import lib.network.NetworkUtils;

/**
 * ******************************************************************************************
 * Created by:          Sanjeet Chand
 * Created When:        14/05/2023
 * URL:                 https://www.linkedin.com/in/sanjeetchand/
 * <p>
 * Package Name:
 * Job Number:          v
 * Description:         //TODO
 * Dependencies:        //TODO
 * Change History:
 * Date       Name        Job Number      Description/Reason      Reviewed By      Review Date
 * ******************************************************************************************
 */
public class MainActivityViewModel extends AppStateViewModel {

    private final MutableLiveData<String> serverResponse = new MutableLiveData<>("");

    public MutableLiveData<String> serverResponse() {
        return serverResponse;
    }

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadData() {
        if (NetworkUtils.isNetworkAvailable(getApplication())) {
            //set the loading state
            setLoadingState();
            BackgroundTask.execute(() -> {
                StringHttpRequest connectionRequest = new StringHttpRequest("https://catfact.ninja/fact");
                return connectionRequest.makeRequest();
            }, new ResponseListener<String>() {
                @Override
                public void onResponse(String response) {
                    serverResponse.setValue(response);
                    setLoadedState();
                }

                @Override
                public void onError(Exception ex) {
                    setErrorState(ex.getMessage());
                }
            });
        }
    }
}
