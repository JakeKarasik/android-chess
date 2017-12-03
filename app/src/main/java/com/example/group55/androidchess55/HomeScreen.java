package com.example.group55.androidchess55;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class HomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    Intent myIntent = new Intent(getBaseContext(), ChessBoard.class);
    //myIntent.putExtra("key", value); //Optional parameters
    startActivity(myIntent);
}
