package fj.com.craftyapps.backgroundhttpsrequests;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

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
public abstract class BaseActivity<T extends AppStateViewModel> extends AppCompatActivity {

    protected T viewModel;

    private ViewGroup initialView, loadingView, loadedView, errorView;
    private TextView tvError;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initStateViews();
    }

    public void configureViewModel(Class<T> viewModelClass) {
        viewModel = new ViewModelProvider(this).get(viewModelClass);
        observeState();
    }

    void initStateViews() {
        loadingView = findViewById(R.id.loadingView);
        initialView = findViewById(R.id.initialView);
        errorView = findViewById(R.id.errorView);
        loadedView = findViewById(R.id.loadedView);
        tvError = findViewById(R.id.tvError);
    }

    void observeState() {
        viewModel.getViewState().observe(this, state -> {
            if (viewModel.isInLoadingState()) {
                showLoadingView();
            } else if (viewModel.isInLoadedState()) {
                showLoadedView();
            } else if (viewModel.isInErrorState()) {
                showErrorView();
            } else {
                showInitialView();
            }
        });
    }

    protected void showInitialView() {
        toggleViewStates(initialView, loadedView, loadingView, errorView);
    }

    protected void showLoadedView() {
        toggleViewStates(loadedView, loadingView, initialView, errorView);
    }

    protected void showLoadingView() {
        toggleViewStates(loadingView, initialView, errorView, loadedView);
    }

    protected void showErrorView() {
        tvError.setText(viewModel.getErrorMessage());
        toggleViewStates(errorView, loadingView, initialView, loadedView);
    }

    private void toggleViewStates(ViewGroup viewToShow, ViewGroup... viewsToHide) {
        if (viewToShow != null) {
            viewToShow.setVisibility(View.VISIBLE);
        }
        for (ViewGroup viewToHide : viewsToHide) {
            if (viewToHide != null)
                viewToHide.setVisibility(View.GONE);
        }
    }
}
