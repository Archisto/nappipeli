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

    private GameService service;
    private BookService serviceTest;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    onHomeNavSelected();
                    return true;
                case R.id.navigation_nickname:
                    onSetNicknameNavSelected();
                    return true;
                case R.id.navigation_winners:
                    return false;
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

        initService();
        initNickname();
        initRefreshButton();
    }

    private void onHomeNavSelected() {
        Intent i = new Intent(WinnersActivity.this, MainActivity.class);
        i.putExtra("nickname", nickname);
        startActivity(i);
    }

    private void onSetNicknameNavSelected() {
        Intent i = new Intent(WinnersActivity.this, SetNicknameActivity.class);
        i.putExtra("nickname", nickname);
        startActivity(i);
    }

    /**
     * Takes the user to the main activity.
     */
    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(WinnersActivity.this, MainActivity.class);
        i.putExtra("nickname", nickname);
        startActivity(i);
    }

    private void initService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://nappipeli-db.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(GameService.class);

        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl("https://ratpack-demo-db.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        serviceTest = retrofit2.create(BookService.class);
    }

    private void initNickname() {
        Bundle extras = getIntent().getExtras();
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
                showAllWinnersFromDatabase();
            }
        });
    }

    private void showAllWinnersFromDatabase() {
        Call<List<Winner>> createCall = service.winners();
        createCall.enqueue(new Callback<List<Winner>>() {
            @Override
            public void onResponse(Call<List<Winner>> c, Response<List<Winner>> resp) {
                allWinners.setText("ALL WINNERS by NICKNAME:\n");
                for (Winner w : resp.body()) {
                    allWinners.append(w.nickname + "\n");
                }
            }

            @Override
            public void onFailure(Call<List<Winner>> c, Throwable t) {
                t.printStackTrace();
                allWinners.setText(t.getMessage());
            }
        });
    }

    private void showAllBooksFromDatabase() {
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
}
