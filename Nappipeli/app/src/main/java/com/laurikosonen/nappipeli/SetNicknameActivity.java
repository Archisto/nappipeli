package com.laurikosonen.nappipeli;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SetNicknameActivity extends AppCompatActivity {

    private String nickname = "Anonymous";
    private Button submitButton;
    private EditText nicknameInput;
    private TextView nicknameText;
    private Button herokuButton;
    private TextView herokuText;
    private BottomNavigationView navigation;

    private BookService serviceTest;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_increase:
                    onMainScreenButtonClick();
                    return true;
                case R.id.navigation_username:
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
        setContentView(R.layout.activity_set_nickname);

        submitButton = (Button) findViewById(R.id.button_submit);
        nicknameInput = (EditText) findViewById(R.id.input_username);
        herokuButton = (Button) findViewById(R.id.button_heroku);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        herokuText = (TextView) findViewById(R.id.text_hello);
        herokuText.setText("Empty");
        nicknameText = (TextView) findViewById(R.id.text_nickname);
        nicknameText.setText(String.format(getString(R.string.string_username), nickname));
        navigation.setSelectedItemId(R.id.navigation_username);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl("https://ratpack-demo-db.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        serviceTest = retrofit2.create(BookService.class);

        initSubmitButton();
        initHerokuButton();
    }

    private void onMainScreenButtonClick() {
        startActivity(new Intent(SetNicknameActivity.this, MainActivity.class));
    }

    private void onSetNicknameButtonClick() {
        // Does nothing
    }

    private void onWinnersButtonClick() {
        startActivity(new Intent(SetNicknameActivity.this, WinnersActivity.class));
    }

    private void initSubmitButton() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNickname();
            }
        });
    }

    private void setNickname() {
        nickname += "+";
        nicknameText.setText(String.format(getString(R.string.string_username), nickname));
    }

    private void initHerokuButton() {
        herokuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book book = new Book(nicknameInput.getText().toString());
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
    }
}
