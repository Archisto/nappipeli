package com.laurikosonen.nappipeli;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private int counter;
    private String username = "Anonymous";
    private TextView mTextMessage;
    private TextView usernameText;

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
        mTextMessage.setText(String.format(getString(R.string.string_counter), counter));
        usernameText.setText(String.format(getString(R.string.string_username), username));
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}
