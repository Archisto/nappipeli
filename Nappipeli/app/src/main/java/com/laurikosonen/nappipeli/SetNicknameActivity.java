package com.laurikosonen.nappipeli;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SetNicknameActivity extends AppCompatActivity {

    private final int nicknameMaxLength = 12;

    private String nickname;
    private Button submitButton;
    private EditText nicknameInput;
    private TextView nicknameText;
    private BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    onHomeNavSelected();
                    return true;
                case R.id.navigation_nickname:
                    return false;
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
        setContentView(R.layout.activity_set_nickname);

        submitButton = (Button) findViewById(R.id.button_submit);
        nicknameInput = (EditText) findViewById(R.id.input_username);
        nicknameText = (TextView) findViewById(R.id.text_nickname);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_nickname);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        initNickname();
        initSubmitButton();
    }

    private void onHomeNavSelected() {
        Intent i = new Intent(SetNicknameActivity.this, MainActivity.class);
        i.putExtra("nickname", nickname);
        startActivity(i);
    }

    private void onWinnersNavSelected() {
        Intent i = new Intent(SetNicknameActivity.this, WinnersActivity.class);
        i.putExtra("nickname", nickname);
        startActivity(i);
    }

    /**
     * Takes the user to the main activity.
     */
    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(SetNicknameActivity.this, MainActivity.class);
        i.putExtra("nickname", nickname);
        startActivity(i);
    }

    private void initNickname() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nickname = extras.getString("nickname");
        } else {
            nickname = "Anonymous";
        }
        nicknameText.setText(String.format(getString(R.string.string_username), nickname));
    }

    private void initSubmitButton() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNickname(v);
            }
        });
    }

    private void setNickname(View v) {
        String newNick = nicknameInput.getText().toString();
        if (newNick.length() == 0) {
            Snackbar snackbar = Snackbar.make(
                    v,
                    getString(R.string.string_nickname_empty),
                    Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else if (newNick.length() > nicknameMaxLength) {
            Snackbar snackbar = Snackbar.make(
                    v,
                    String.format(getString(R.string.string_nickname_too_long), nicknameMaxLength),
                    Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else {
            nickname = newNick;
            nicknameText.setText(String.format(getString(R.string.string_username), nickname));
        }
    }
}
