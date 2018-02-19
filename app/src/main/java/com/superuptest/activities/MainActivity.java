package com.superuptest.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.superuptest.R;


/**
 * MainActivity simple screen with start game button.
 * */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btStartGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btStartGame = findViewById(R.id.bt_start_game);
        btStartGame.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btStartGame.getId()){

            overridePendingTransition(0,0);
            startActivity(new Intent(this,GameActivity.class));
        }
    }
}
