package com.example.group55.androidchess55.activities.ChessBoard;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;

import com.example.group55.androidchess55.R;
import com.example.group55.androidchess55.models.*;

import java.util.ArrayList;

public class ChessBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess_board);

        //Setup toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Cancel Game");
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        //Create board
        ArrayList<ChessPiece> board = new ArrayList<ChessPiece>(64);
        board.add(new Pawn('b'));


        ArrayAdapter<ChessPiece> adapter= new ArrayAdapter<ChessPiece>(ChessBoardActivity.this, android.R.layout.simple_list_item_1,board);
        //findViewById(R.id.board_grid).setAdapter(adapter);
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
