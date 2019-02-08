package com.laurikosonen.nappipeli;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private int counter;
    private String username = "Anonymous";
    private TextView mTextMessage;
    private TextView usernameText;
    private TextView helloText;
    private Button helloButton;
//    private HerokuService service;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_increase:
                    counter++;
                    mTextMessage.setText(String.format(getString(R.string.string_counter), counter));
                    return true;
                case R.id.navigation_username:
                    username += "" + counter;
                    usernameText.setText(String.format(getString(R.string.string_username), username));
                    return true;
                case R.id.navigation_winners:
                    OnWinnersButtonClick();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.text_counter);
        usernameText = (TextView) findViewById(R.id.text_username);
        helloButton = (Button) findViewById(R.id.button_hello);
        helloText = (TextView) findViewById(R.id.text_hello);
        mTextMessage.setText(String.format(getString(R.string.string_counter), counter));
        usernameText.setText(String.format(getString(R.string.string_username), username));
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ratpack-demo-test.herokuapp.com/")
                .build();
        final HerokuService service = retrofit.create(HerokuService.class);
        helloText.setText("Empty");

        helloButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ResponseBody> call = service.hello();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> c,
                                           Response<ResponseBody> response) {
                        try {
                            helloText.setText(response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                            helloText.setText(e.getMessage());
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            helloText.setText(e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> c, Throwable t) {
                        t.printStackTrace();
                        helloText.setText(t.getMessage());
                    }
                });
            }
        });
    }

    private void OnWinnersButtonClick() {
//        Call<ResponseBody> call = service.hello();
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> c,
//                                   Response<ResponseBody> response) {
//                try {
//                    helloText.setText(response.body().string());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    helloText.setText(e.getMessage());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> c, Throwable t) {
//                t.printStackTrace();
//                helloText.setText(t.getMessage());
//            }
//        });
    }
}
