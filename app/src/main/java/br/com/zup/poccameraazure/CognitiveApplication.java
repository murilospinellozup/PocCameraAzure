package br.com.zup.poccameraazure;

import android.app.Application;

import br.com.zup.poccameraazure.retrofit.RetrofitClient;

public class CognitiveApplication extends Application {
    private static CognitiveApplication instance;

    public RetrofitClient mApiClient;

    public static CognitiveApplication getInstance() {
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        initializeAPIService();
    }

    private void initializeAPIService() {


    }

    public RetrofitClient getApiClient() {
        return mApiClient;
    }

}
