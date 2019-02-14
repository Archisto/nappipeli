package com.laurikosonen.nappipeli;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private int counter;
    private String username = "Anonymous";
    private TextView mTextMessage;
    private TextView usernameText;
    private TextView herokuText;
    private TextView prizeText;
    private TextView pressesText;
    private TextView allWinners;
    private Button herokuButton;
    private EditText usernameInput;

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
                case R.id.navigation_increase:
                    raiseCounter();
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
        herokuButton = (Button) findViewById(R.id.button_hello);
        usernameInput = (EditText) findViewById(R.id.input_username);
        herokuText = (TextView) findViewById(R.id.text_hello);
        prizeText = (TextView) findViewById(R.id.text_prize);
        pressesText = (TextView) findViewById(R.id.text_pressesUntilPrize);
        allWinners = (TextView) findViewById(R.id.text_allWinners);
        mTextMessage.setText(String.format(getString(R.string.string_counter), counter));
        usernameText.setText(String.format(getString(R.string.string_username), username));
        prizeText.setText("");
        pressesText.setText(String.format(getString(R.string.string_pressesUntilPrize),
                getPressesUntilNextPrize()));
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

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
        herokuText.setText("Empty");

        herokuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book book = new Book(usernameInput.getText().toString());
                Call<Book> createCall = serviceTest.create(book);
                createCall.enqueue(new Callback<Book>() {
                    @Override
                    public void onResponse(Call<Book> _, Response<Book> resp) {
                        Book newBook = resp.body();
                        herokuText.setText("Created Book with ISBN: " + newBook.isbn);
                    }

                    @Override
                    public void onFailure(Call<Book> _, Throwable t) {
                        t.printStackTrace();
                        herokuText.setText(t.getMessage());
                    }
                });
            }
        });

//        herokuButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Call<ResponseBody> call = serviceTest.hello();
//                call.enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(Call<ResponseBody> c,
//                                           Response<ResponseBody> response) {
//                        try {
//                            herokuText.setText(response.body().string());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            herokuText.setText(e.getMessage());
//                        } catch (NullPointerException e) {
//                            e.printStackTrace();
//                            herokuText.setText(e.getMessage());
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseBody> c, Throwable t) {
//                        t.printStackTrace();
//                        herokuText.setText(t.getMessage());
//                    }
//                });
//            }
//        });
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
        //mTextMessage.setText(String.format(getString(R.string.string_counter), counter));
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

    private void OnWinnersButtonClick() {
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
