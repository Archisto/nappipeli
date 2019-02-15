package com.laurikosonen.nappipeli;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private int counter;
    private String nickname;
    private TextView counterText;
    private TextView nicknameText;
    private TextView prizeText;
    private TextView pressesText;
    private Button increaseButton;
    private BottomNavigationView navigation;

    private GameService service;

    private static final int pressesForPrize1 = 100;
    private static final int pressesForPrize2 = 200;
    private static final int pressesForPrize3 = 500;

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
        setContentView(R.layout.activity_main);

        increaseButton = (Button) findViewById(R.id.button_increase);
        counterText = (TextView) findViewById(R.id.text_counter);
        prizeText = (TextView) findViewById(R.id.text_prize);
        pressesText = (TextView) findViewById(R.id.text_pressesUntilPrize);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        nicknameText = (TextView) findViewById(R.id.text_nickname);
        counterText.setText(String.format(getString(R.string.string_counter), counter));
        prizeText.setText("");
        pressesText.setText(String.format(getString(R.string.string_pressesUntilPrize),
                getPressesUntilNextPrize()));
        navigation.setSelectedItemId(R.id.navigation_home);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://nappipeli-db.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(GameService.class);

        initNickname();
        initIncreaseButton();
    }

    private void raiseCounter() {
        // TODO: Get counter value from Heroku, raise it by one and send the new value back.
        counter++;
        if (counter % pressesForPrize3 == 0) {
            givePrize(3);
        }
        else if (counter % pressesForPrize2 == 0) {
            givePrize(2);
        }
        else if (counter % pressesForPrize1 == 0) {
            givePrize(1);
        }

        pressesText.setText(String.format(getString(R.string.string_pressesUntilPrize),
                getPressesUntilNextPrize()));
        counterText.setText(String.format(getString(R.string.string_counter), counter));
    }

    private void givePrize(int prizeTier) {
        switch (prizeTier) {
            case 1: {
                break;
            }
            case 2: {
                break;
            }
            case 3: {
                break;
            }
            default: {
                Log.e("npeli", "Prize error!");
                break;
            }
        }

        Log.d("npeli", "Won " + prizeTier);
        prizeText.setText(String.format(getString(R.string.string_prize), "" + prizeTier));
    }

    private int getPressesUntilNextPrize() {
        return pressesForPrize1 - counter % pressesForPrize1;
    }

    private void initNickname() {
        Bundle extras = getIntent().getExtras();
        Log.d("npeli", "extras exist: " + (extras != null));
        if (extras != null) {
            nickname = extras.getString("nickname");
            nicknameText.setText(nickname);
        }
        else {
            nickname = "Anonymous";
        }
        nicknameText.setText(String.format(getString(R.string.string_username), nickname));
    }

    private void initIncreaseButton() {
        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                raiseCounter();
            }
        });
    }

    private void onMainScreenButtonClick() {
        // Does nothing
    }

    private void onSetNicknameButtonClick() {
        Intent i = new Intent(MainActivity.this, SetNicknameActivity.class);
        i.putExtra("nickname", nickname);
        startActivity(i);
    }

    private void onWinnersButtonClick() {
        Intent i = new Intent(MainActivity.this, WinnersActivity.class);
        i.putExtra("nickname", nickname);
        startActivity(i);

        // First test
//        Call<ResponseBody> call = serviceTest.hello();
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> c,
//                                   Response<ResponseBody> response) {
//                try {
//                    herokuText.setText(response.body().string());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    herokuText.setText(e.getMessage());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> c, Throwable t) {
//                t.printStackTrace();
//                herokuText.setText(t.getMessage());
//            }
//        });
    }
}
