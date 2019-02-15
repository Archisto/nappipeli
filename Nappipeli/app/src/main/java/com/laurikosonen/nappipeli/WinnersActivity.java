package com.laurikosonen.nappipeli;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WinnersActivity extends AppCompatActivity {

    private String nickname;
    private TextView allWinners;
    private Button refreshButton;
    private TextView nicknameText;
    private BottomNavigationView navigation;

    private BookService serviceTest;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    onMainScreenButtonClick();
                    return true;
                case R.id.navigation_nickname:
                    onSetNicknameButtonClick();
                    return true;
                case R.id.navigation_winners:
                    onWinnersButtonClick();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winners);

        allWinners = (TextView) findViewById(R.id.text_allWinners);
        refreshButton = (Button) findViewById(R.id.button_refresh);
        nicknameText = (TextView) findViewById(R.id.text_nickname);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_winners);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl("https://ratpack-demo-db.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        serviceTest = retrofit2.create(BookService.class);

        initNickname();
        initRefreshButton();
    }

    private void onMainScreenButtonClick() {
        Intent i = new Intent(WinnersActivity.this, MainActivity.class);
        i.putExtra("nickname", nickname);
        startActivity(i);
    }

    private void onSetNicknameButtonClick() {
        Intent i = new Intent(WinnersActivity.this, SetNicknameActivity.class);
        i.putExtra("nickname", nickname);
        startActivity(i);
    }

    private void onWinnersButtonClick() {
        // Does nothing
    }

    private void initNickname() {
        Bundle extras = getIntent().getExtras();
        Log.d("npeli", "extras exist: " + (extras != null));
        if (extras != null) {
            nickname = extras.getString("nickname");
        }
        else {
            nickname = "Anonymous";
        }
        nicknameText.setText(String.format(getString(R.string.string_username), nickname));
    }

    private void initRefreshButton() {
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<List<Book>> createCall = serviceTest.all();
                createCall.enqueue(new Callback<List<Book>>() {
                    @Override
                    public void onResponse(Call<List<Book>> c, Response<List<Book>> resp) {
                        allWinners.setText("ALL BOOKS by ISBN:\n");
                        for (Book b : resp.body()) {
                            allWinners.append(b.isbn + "\n");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Book>> c, Throwable t) {
                        t.printStackTrace();
                        allWinners.setText(t.getMessage());
                    }
                });
            }
        });
    }
}
