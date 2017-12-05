package com.example.group55.androidchess55.activities.ChessBoard;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.example.group55.androidchess55.R;
import com.example.group55.androidchess55.activities.ChessBoard.adapters.ChessBoardAdapter;
import com.example.group55.androidchess55.models.*;

import java.util.ArrayList;

public class ChessBoardActivity extends AppCompatActivity {

    static ChessPiece[][] board;

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
       board  = new ChessPiece[8][8];
       initBoard();

        BaseAdapter adapter = new ChessBoardAdapter(ChessBoardActivity.this, board);
        GridView board_grid = (GridView)findViewById(R.id.board_grid);
        board_grid.setAdapter(adapter);
    }

    /**
     * Converts a given string coordinate to its position on the board.
     *
     * @param pos Coordinate in string form. Ex: a1
     * @return <code>[row, col]</code> of given pos.
     */
    public static int[] strPositionToXY(String pos) {
        //Create array for final pos
        int[] final_pos = new int[2];

        //Convert letter/number to separate ints
        final_pos[0] = 8 - Character.getNumericValue(pos.charAt(1));
        final_pos[1] = pos.charAt(0) - 'a';

        return final_pos;
    }

    /**
     * Places given piece on board at given position and sets the pieces pos field.
     *
     * @param pos Position to place piece.
     * @param piece ChessPiece to be placed.
     */
    public static void placePiece(int[] pos, ChessPiece piece) {
        board[pos[0]][pos[1]] = piece;
        piece.setPos(pos);
    }
    public static void initBoard() {

        // Place black pieces
        placePiece(strPositionToXY("a7"), new Pawn('b'));
        placePiece(strPositionToXY("b7"), new Pawn('b'));
        placePiece(strPositionToXY("c7"), new Pawn('b'));
        placePiece(strPositionToXY("d7"), new Pawn('b'));
        placePiece(strPositionToXY("e7"), new Pawn('b'));
        placePiece(strPositionToXY("f7"), new Pawn('b'));
        placePiece(strPositionToXY("g7"), new Pawn('b'));
        placePiece(strPositionToXY("h7"), new Pawn('b'));
        placePiece(strPositionToXY("e8"), new King('b'));
        placePiece(strPositionToXY("d8"), new Queen('b'));
        placePiece(strPositionToXY("c8"), new Bishop('b'));
        placePiece(strPositionToXY("f8"), new Bishop('b'));
        placePiece(strPositionToXY("b8"), new Knight('b'));
        placePiece(strPositionToXY("g8"), new Knight('b'));
        placePiece(strPositionToXY("a8"), new Rook('b'));
        placePiece(strPositionToXY("h8"), new Rook('b'));

        // Place white pieces
        placePiece(strPositionToXY("a2"), new Pawn('w'));
        placePiece(strPositionToXY("b2"), new Pawn('w'));
        placePiece(strPositionToXY("c2"), new Pawn('w'));
        placePiece(strPositionToXY("d2"), new Pawn('w'));
        placePiece(strPositionToXY("e2"), new Pawn('w'));
        placePiece(strPositionToXY("f2"), new Pawn('w'));
        placePiece(strPositionToXY("g2"), new Pawn('w'));
        placePiece(strPositionToXY("h2"), new Pawn('w'));
        placePiece(strPositionToXY("e1"), new King('w'));
        placePiece(strPositionToXY("d1"), new Queen('w'));
        placePiece(strPositionToXY("c1"), new Bishop('w'));
        placePiece(strPositionToXY("f1"), new Bishop('w'));
        placePiece(strPositionToXY("b1"), new Knight('w'));
        placePiece(strPositionToXY("g1"), new Knight('w'));
        placePiece(strPositionToXY("a1"), new Rook('w'));
        placePiece(strPositionToXY("h1"), new Rook('w'));

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
