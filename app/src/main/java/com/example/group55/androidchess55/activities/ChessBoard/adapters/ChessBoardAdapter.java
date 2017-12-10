package com.example.group55.androidchess55.activities.ChessBoard.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.group55.androidchess55.R;
import com.example.group55.androidchess55.models.ChessPiece;
import java.util.HashMap;
import java.util.Map;

public class ChessBoardAdapter extends android.widget.BaseAdapter{

    private Context mContext;
    private ChessPiece[] board;
    private Map<String, Integer> map;

    public ChessBoardAdapter(Context c, ChessPiece[] board) {
        mContext = c;
        this.board = board;
        map = new HashMap<>();
        setImageRefs();
    }

    @Override
    public int getCount() {
        return board.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Create an ImageView to display chessboard
        ImageView imageView;

        // If it's not recycled, initialize some attributes
        if (convertView == null) {
            imageView = new ImageView(mContext);
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics metrics = new DisplayMetrics();
            if(wm != null){ wm.getDefaultDisplay().getMetrics(metrics); }else{ return null; }
            int screen_width = metrics.widthPixels;
            imageView.setLayoutParams(new GridView.LayoutParams((screen_width/8), (screen_width/8)));
        } else {
            imageView = (ImageView) convertView;
        }

        // Set image
        ChessPiece current_piece = board[position];
        if (current_piece != null) {
            imageView.setImageResource(map.get(current_piece.toString()));
        }

        // Set background color of space
        int calc_pos = (position % 8) + (position / 8);
        if (calc_pos % 2 == 0) {
            imageView.setBackgroundColor(Color.parseColor("#FFCE9E"));
        } else {
            imageView.setBackgroundColor(Color.parseColor("#D18B47"));
        }

        return imageView;
    }

    private void setImageRefs() {
        map.put("bB", R.drawable.bb);
        map.put("bN", R.drawable.bh);
        map.put("bK", R.drawable.bk);
        map.put("bP", R.drawable.bp);
        map.put("bQ", R.drawable.bq);
        map.put("bR", R.drawable.br);
        map.put("wB", R.drawable.wb);
        map.put("wN", R.drawable.wh);
        map.put("wK", R.drawable.wk);
        map.put("wP", R.drawable.wp);
        map.put("wQ", R.drawable.wq);
        map.put("wR", R.drawable.wr);
    }
}
