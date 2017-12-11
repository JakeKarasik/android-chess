package com.example.group55.androidchess55.activities.GameReplay;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;

import com.example.group55.androidchess55.R;
import com.example.group55.androidchess55.activities.ChessBoard.adapters.ChessBoardAdapter;
import com.example.group55.androidchess55.models.ChessPiece;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.LinkedList;

public class GameReplayActivity  extends AppCompatActivity {

    LinkedList<ChessPiece[]> boards;
    ChessBoardAdapter adapter;
    GridView board_grid;
    int curr_board_num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_replay);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Saved Games");
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        // Load board given name
        getReplay(getIntent().getStringExtra("filename"));
        adapter = new ChessBoardAdapter(GameReplayActivity.this, boards.get(curr_board_num));
        board_grid = findViewById(R.id.board_grid);
        board_grid.setAdapter(adapter);
    }

    public void getReplay(String filename) {
        FileInputStream fis = null;
        try {
            fis = openFileInput(filename);
            ObjectInputStream is = new ObjectInputStream(fis);
            boards = (LinkedList<ChessPiece[]>)is.readObject();
            is.close();
            fis.close();
        } catch (Exception e) {}
    }

    public void prevMove(View v) {
        if (curr_board_num > 0) {
            curr_board_num--;
            adapter = new ChessBoardAdapter(GameReplayActivity.this, boards.get(curr_board_num));
            board_grid.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    public void nextMove(View v) {
        int size = boards.size();
        if (curr_board_num < size - 1) {
            curr_board_num++;
            adapter = new ChessBoardAdapter(GameReplayActivity.this, boards.get(curr_board_num));
            board_grid.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
}
