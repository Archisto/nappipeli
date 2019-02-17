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

import retrofit2.Retrofit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private int counter;
    private String nickname;
    private TextView counterText;
    private TextView nicknameText;
    private TextView prizeText;
    private TextView pressesText;
    private Button increaseButton;
    private Button herokuButton;
    private TextView herokuText;
    private BottomNavigationView navigation;

    private GameService service;
    private BookService serviceTest;

    private static final int pressesForPrize1 = 10;
    private static final int pressesForPrize2 = 20;
    private static final int pressesForPrize3 = 50;

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

        increaseButton = (Button) findViewById(R.id.button_increase);
        counterText = (TextView) findViewById(R.id.text_counter);
        prizeText = (TextView) findViewById(R.id.text_prize);
        pressesText = (TextView) findViewById(R.id.text_pressesUntilPrize);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        nicknameText = (TextView) findViewById(R.id.text_nickname);
        counterText.setText(String.format(getString(R.string.string_counter), counter));
        herokuButton = (Button) findViewById(R.id.button_heroku);
        herokuText = (TextView) findViewById(R.id.text_heroku);
        herokuText.setText("Empty");
        prizeText.setText("");
        pressesText.setText(String.format(getString(R.string.string_pressesUntilPrize),
                getPressesUntilNextPrize()));
        navigation.setSelectedItemId(R.id.navigation_home);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        initService();
        initNickname();
        initIncreaseButton();
        initHerokuButton();
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

    private void initHerokuButton() {
        herokuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookToDatabase();
            }
        });
    }

    private void raiseCounter() {
        // TODO: Get counter value from Heroku, raise it by one and send the new value back.
        updateCounterValue();
        service.raiseCounter();
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
        addWinnerToDatabase(prizeTier);
    }

    private int getPressesUntilNextPrize() {
        return pressesForPrize1 - counter % pressesForPrize1;
    }

    private void updateCounterValue() {
        // TODO: Make this work.
        Call<Counter> call = service.getCounter();
        call.enqueue(new Callback<Counter>() {
            @Override
            public void onResponse(Call<Counter> c,
                                   Response<Counter> response) {
                if (response.body() != null) {
                    counter = response.body().value;
                }
                else {
                    Log.e("npeli", "Response body is null.");
                }
            }

            @Override
            public void onFailure(Call<Counter> c, Throwable t) {
                t.printStackTrace();
                herokuText.setText(t.getMessage());
            }
        });
    }

    private void addWinnerToDatabase(int prizeTier) {
        // TODO: Allow multiple winners with the same nickname but limit the max amount of winners.
        Winner winner = new Winner(nickname, prizeTier);
        Call<Winner> createCall = service.create(winner);
        createCall.enqueue(new Callback<Winner>() {
            @Override
            public void onResponse(Call<Winner> c, Response<Winner> resp) {
                Winner newWinner = resp.body();
                if (newWinner != null) {
                    herokuText.setText("Created Winner with nickname: " + newWinner.nickname);
                }
                else {
                    herokuText.setText("Couldn't create a Winner");
                }
            }

            @Override
            public void onFailure(Call<Winner> c, Throwable t) {
                t.printStackTrace();
                herokuText.setText(t.getMessage());
            }
        });
    }

    private void addBookToDatabase() {
        Book book = new Book(nickname);
        Call<Book> createCall = serviceTest.create(book);
        createCall.enqueue(new Callback<Book>() {
            @Override
            public void onResponse(Call<Book> c, Response<Book> resp) {
                Book newBook = resp.body();
                if (newBook != null) {
                    herokuText.setText("Created Book with ISBN: " + newBook.isbn);
                }
                else {
                    herokuText.setText("Couldn't create a Book");
                }
            }

            @Override
            public void onFailure(Call<Book> c, Throwable t) {
                t.printStackTrace();
                herokuText.setText(t.getMessage());
            }
        });
    }
}
