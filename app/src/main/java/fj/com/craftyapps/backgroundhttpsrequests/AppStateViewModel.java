package fj.com.craftyapps.backgroundhttpsrequests;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

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
public abstract class AppStateViewModel extends AndroidViewModel {

    protected enum State {
        INITIAL,
        LOADING,
        ERROR,
        LOADED
    }

    private String errorMessage = "";
    private final MutableLiveData<State> viewState = new MutableLiveData<>(State.INITIAL);

    public String getErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<State> getViewState() {
        return viewState;
    }

    public AppStateViewModel(@NonNull Application application) {
        super(application);
    }

    private void setViewState(State viewState) {
        this.viewState.setValue(viewState);
    }

    private void postViewState(State viewState) {
        this.viewState.postValue(viewState);
    }

    protected void setLoadingState() {
        setViewState(State.LOADING);
    }

    protected void setLoadedState() {
        setViewState(State.LOADED);
    }

    protected void setErrorState(String errorMessage) {
        setViewState(State.ERROR);
        this.errorMessage = errorMessage;
    }

    public boolean isInLoadingState() {
        return this.viewState.getValue() == State.LOADING;
    }

    public boolean isInLoadedState() {
        return this.viewState.getValue() == State.LOADED;
    }

    public boolean isInErrorState() {
        return this.viewState.getValue() == State.ERROR;
    }
}
