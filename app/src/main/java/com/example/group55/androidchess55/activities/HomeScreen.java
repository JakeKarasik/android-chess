package com.example.group55.androidchess55.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.group55.androidchess55.R;

public class HomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public HomeScreen() {

    }

    Intent myIntent = new Intent(getBaseContext(), ChessBoard.class);
    //myIntent.putExtra("key", value); //Optional parameters
    //startActivity(myIntent);
}
