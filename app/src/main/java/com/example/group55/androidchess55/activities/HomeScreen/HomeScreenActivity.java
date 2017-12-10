package com.example.group55.androidchess55.activities.HomeScreen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.group55.androidchess55.R;
import com.example.group55.androidchess55.activities.ChessBoard.ChessBoardActivity;

public class HomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
    }

    public HomeScreenActivity() {

    }

    public void startNewGame(View v) {
        Intent chessboard_intent = new Intent(HomeScreenActivity.this, ChessBoardActivity.class);
        //myIntent.putExtra("key", value); //Optional parameters
        startActivity(chessboard_intent);
    }


}
