package fj.com.craftyapps.backgroundhttpsrequests;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends BaseActivity<MainActivityViewModel> {

    Button btnGetData;
    TextView tvServerResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init UI elements
        tvServerResponse = findViewById(R.id.tvServerResponse);
        btnGetData = findViewById(R.id.btnGetData);

        //initialise view model, we get the instance from the super class
        configureViewModel(MainActivityViewModel.class);


        btnGetData.setOnClickListener(view -> {
            viewModel.loadData();
        });

        //observe for when the server response gets updated then show it on the UI
        viewModel.serverResponse().observe(this, response -> {
            tvServerResponse.setText(response);
        });
    }
}