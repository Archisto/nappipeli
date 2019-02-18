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

import retrofit2.Retrofit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private Counter counter;
    private int onScreenCounterValue = 0;
    private int winnerCount = 0;
    private int prizeTier = 0;
    private String nickname;
    private TextView nicknameText;
    private TextView prizeText;
    private TextView pressesText;
    private Button increaseButton;
    private BottomNavigationView navigation;

    private GameService service;
    private BookService serviceTest;

    private static final int pressesForPrize1 = 100;
    private static final int pressesForPrize2 = 200;
    private static final int pressesForPrize3 = 500;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return false;
                case R.id.navigation_nickname:
                    onSetNicknameNavSelected();
                    return true;
                case R.id.navigation_winners:
                    onWinnersNavSelected();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        counter = new Counter(0);
        increaseButton = (Button) findViewById(R.id.button_increase);
        prizeText = (TextView) findViewById(R.id.text_prize);
        pressesText = (TextView) findViewById(R.id.text_pressesUntilPrize);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        nicknameText = (TextView) findViewById(R.id.text_nickname);
        prizeText.setText("");
        pressesText.setText(String.format(getString(R.string.string_pressesUntilPrize),
                getPressesUntilNextPrize()));
        navigation.setSelectedItemId(R.id.navigation_home);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        initService();
        initNickname();
        initIncreaseButton();

        updateCounterValue();
    }

    private void onSetNicknameNavSelected() {
        Intent i = new Intent(MainActivity.this, SetNicknameActivity.class);
        i.putExtra("nickname", nickname);
        startActivity(i);
    }

    private void onWinnersNavSelected() {
        Intent i = new Intent(MainActivity.this, WinnersActivity.class);
        i.putExtra("nickname", nickname);
        startActivity(i);
    }

    /**
     * Closes the app.
     */
    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
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
            nicknameText.setText(nickname);
        } else {
            nickname = "Anonymous";
        }
        nicknameText.setText(String.format(getString(R.string.string_username), nickname));
    }

    private void initIncreaseButton() {
        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gets counter value from Heroku, raises it by one and sends the new value back.
                updateCounterValue();
                raiseCounterValue();
            }
        });
    }

    private void updateOnScreenCounter() {
        if (counter.value > onScreenCounterValue) {
            pressesText.setText(String.format(getString(R.string.string_pressesUntilPrize),
                    getPressesUntilNextPrize()));
            onScreenCounterValue = counter.value;
        }
    }

    private void updateCounterValue() {
        Call<List<Counter>> call = service.counters();
        call.enqueue(new Callback<List<Counter>>() {
            @Override
            public void onResponse(Call<List<Counter>> c,
                                   Response<List<Counter>> response) {
                if (response.body() != null) {
                    if (response.body().size() == 0) {
                        addCounterToDatabase();
                    } else {
                        int newValue = response.body().get(0).value;
                        counter.value = newValue;
                        updateOnScreenCounter();
                        Log.d("npeli",
                                "Updated Counter with value from database: " + newValue);
                    }
                } else {
                    Log.e("npeli", "Couldn't update Counter, adding new one to database");
                    addCounterToDatabase();
                }
            }

            @Override
            public void onFailure(Call<List<Counter>> c, Throwable t) {
                t.printStackTrace();
                Log.e("npeli", "Updating Counter failed");
            }
        });
    }

    private void raiseCounterValue() {
        counter.value++;

        Call<Counter> updateCall = service.updateCounter(counter);
        updateCall.enqueue(new Callback<Counter>() {
            @Override
            public void onResponse(Call<Counter> c, Response<Counter> resp) {
                Counter newCounter = resp.body();
                if (newCounter != null) {
                    Log.d("npeli", "Counter value raised");
                } else {
                    Log.e("npeli", "Couldn't raise Counter value");
                }
            }

            @Override
            public void onFailure(Call<Counter> c, Throwable t) {
                t.printStackTrace();
                Log.e("npeli", "Counter value raise failed");
            }
        });

        if (counter.value % pressesForPrize3 == 0) {
            givePrize(3);
        } else if (counter.value % pressesForPrize2 == 0) {
            givePrize(2);
        } else if (counter.value % pressesForPrize1 == 0) {
            givePrize(1);
        }

        updateOnScreenCounter();
    }

    private void givePrize(int prizeTier) {
        this.prizeTier = prizeTier;
        String prizeName = "nothing";
        switch (prizeTier) {
            case 1: {
                prizeName = "a small prize";
                break;
            }
            case 2: {
                prizeName = "a medium prize";
                break;
            }
            case 3: {
                prizeName = "a large prize";
                break;
            }
            default: {
                Log.e("npeli", "Prize error!");
                break;
            }
        }

        Log.d("npeli", "Won " + prizeName);
        prizeText.setText(String.format(getString(R.string.string_prize), prizeName));
        addWinnerToDatabase();
    }

    private int getPressesUntilNextPrize() {
        return pressesForPrize1 - counter.value % pressesForPrize1;
    }

    private void addCounterToDatabase() {
        Call<Counter> createCall = service.createCounter(counter);
        createCall.enqueue(new Callback<Counter>() {
            @Override
            public void onResponse(Call<Counter> c, Response<Counter> resp) {
                Counter newCounter = resp.body();
                if (newCounter != null) {
                    Log.d("npeli", "Created a Counter: " + newCounter.value);
                    counter.value = newCounter.value;
                } else {
                    Log.e("npeli", "Couldn't create a Counter");
                }
            }

            @Override
            public void onFailure(Call<Counter> c, Throwable t) {
                t.printStackTrace();
                Log.e("npeli", "Creating a Counter failed");
            }
        });
    }

    private void addWinnerToDatabase() {
        Call<List<Winner>> createCall = service.winners();
        createCall.enqueue(new Callback<List<Winner>>() {
            @Override
            public void onResponse(Call<List<Winner>> c, Response<List<Winner>> resp) {
                winnerCount = resp.body().size();
                createWinner(prizeTier);
                Log.d("npeli", "Winner count received: " + winnerCount);
            }

            @Override
            public void onFailure(Call<List<Winner>> c, Throwable t) {
                t.printStackTrace();
                Log.e("npeli", "Getting Winner count failed");
            }
        });
    }

    private void createWinner(int prizeTier) {
        Winner winner = new Winner(winnerCount, nickname, prizeTier);
        Call<Winner> createCall = service.createWinner(winner);
        createCall.enqueue(new Callback<Winner>() {
            @Override
            public void onResponse(Call<Winner> c, Response<Winner> resp) {
                Winner newWinner = resp.body();
                if (newWinner != null) {
                    Log.d("npeli", "Created a Winner: " + newWinner.nickname);
                } else {
//                    Log.e("npeli", "Couldn't create a Winner");
                }
            }

            @Override
            public void onFailure(Call<Winner> c, Throwable t) {
                t.printStackTrace();
                Log.e("npeli", "Creating a Winner failed");
            }
        });
    }
}
