package com.fredo.introtorxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "app starting");

        subscription = getDiscoObservable()
                .subscribeOn(Schedulers.io()) //This runs getDiscoObservable() in Schedulers.io() thread/s?
                .observeOn(AndroidSchedulers.mainThread()) //This makes onNext() get called on the mainThread()
                .subscribe(new Subscriber<DiscoResponse>() {
                    @Override
                    public void onNext(DiscoResponse discoResponse) {
                        Log.i(TAG, "Got our response with first event name: "
                                + discoResponse._embedded.events[0].name);
                        //If you make references to resources here that should be cleaned up after the application ends
                        //you will have a memory leak if you don't unsubscribe!
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, t.getMessage(), t);
                    }

                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Nullable
    private DiscoResponse getDiscoResponse() throws IOException {
        //Actual code to get our data (this can't run on mainThread)
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://app.ticketmaster.com/discovery/v2/events?apikey=7elxdku9GGG5k8j0Xm8KWdANDgecHMV0&size=2&page=0&countryCode=US")
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            DiscoResponse discoResponse = new Gson().fromJson(response.body().charStream(), DiscoResponse.class);
            return discoResponse;
        } else {
            return null;
        }
    }

    public Observable<DiscoResponse> getDiscoObservable() {
        //Observable.defer(callable()) runs callable() when the observable returned is subscribed to
        return Observable.defer(new Func0<Observable<DiscoResponse>>() {
            @Override
            public Observable<DiscoResponse> call() {
                try {
                    //Wrap getDiscoResponse() in an observable so we can subscribe to it, specify where to run
                    //it's code, specify where to observe it's results
                    return Observable.just(getDiscoResponse());
                } catch (IOException e) {
                    return null;
                }
            }
        });
    }
}
