package com.example.group55.androidchess55;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ChessBoard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess_board);
    }

//    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
//    {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
//        {
//            String a = String.valueOf(position);
//            Toast.makeText(getApplicationContext(), a, Toast.LENGTH_SHORT).show();
//        }
//    });
}
