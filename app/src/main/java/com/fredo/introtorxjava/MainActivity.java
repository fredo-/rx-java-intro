package com.fredo.introtorxjava;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("TAG","app starting");

        //THIS TASK GETS OUR DATA IN THE BACKGROUND
        new AsyncTask<Void, Void, DiscoResponse>() {
            @Override
            protected DiscoResponse doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("https://app.ticketmaster.com/discovery/v2/events?apikey=7elxdku9GGG5k8j0Xm8KWdANDgecHMV0&size=2&page=0&countryCode=US")
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        DiscoResponse discoResponse = new Gson().fromJson(response.body().charStream(), DiscoResponse.class);
                        return discoResponse;
                    } else {
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("TAG", "Got an IO exception when getting disco results in background");
                    return null;
                }
            }

            @Override
            protected void onPostExecute(DiscoResponse discoResponse) {
                //YOU CAN UPDATE UI AND DO THINGS WITH THE RESULTS RIGHT HERE
                super.onPostExecute(discoResponse);
                Log.i("TAG", "Got our response with first event name: "
                        + discoResponse._embedded.events[0].name);
            }
        }.execute();

    }


}
