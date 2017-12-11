package com.example.group55.androidchess55.activities.SavedGames.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.group55.androidchess55.models.ChessPiece;
import com.example.group55.androidchess55.models.SavedGameInfo;

import java.util.LinkedList;

public class SavedGamesAdapter extends android.widget.BaseAdapter {

    private Context mContext;
    private LinkedList<SavedGameInfo> saved_games;

    public SavedGamesAdapter(Context c, LinkedList<SavedGameInfo> g) {
        mContext = c;
        saved_games = g;
    }

    @Override
    public int getCount() {
        return saved_games.size();
    }

    @Override
    public Object getItem(int i) {
        return saved_games.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void add(SavedGameInfo item) {
        saved_games.add(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        TextView textView;

        // If it's not recycled, initialize some attributes
        if (convertView == null) {
            textView = new TextView(mContext);
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics metrics = new DisplayMetrics();
            if(wm != null){ wm.getDefaultDisplay().getMetrics(metrics); }else{ return null; }
            int screen_width = metrics.widthPixels;
            textView.setLayoutParams(new GridView.LayoutParams(screen_width, 100));
        } else {
            textView = (TextView) convertView;
        }

        // Set text
        textView.setText(saved_games.get(position).toString());

        // Set background color of box
        if (position % 2 == 0) {
            textView.setBackgroundColor(Color.parseColor("#EFF0F1"));
        } else {
            textView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        return textView;
    }
}
