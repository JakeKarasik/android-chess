package com.example.group55.androidchess55.activities.SavedGames;

import android.icu.util.Calendar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;

import com.example.group55.androidchess55.R;
import com.example.group55.androidchess55.activities.ChessBoard.adapters.ChessBoardAdapter;
import com.example.group55.androidchess55.activities.SavedGames.adapters.SavedGamesAdapter;
import com.example.group55.androidchess55.models.SavedGameInfo;

import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedList;

public class SavedGamesActivity extends AppCompatActivity {

    LinkedList<SavedGameInfo> saved_games;
    SavedGamesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_games);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        saved_games = new LinkedList<>();
        loadSavedGames();
        // Display board
        adapter = new SavedGamesAdapter(SavedGamesActivity.this, saved_games);
        ListView list_view = findViewById(R.id.list_view);
        list_view.setAdapter(adapter);
    }

    public void loadSavedGames() {
//        FileInputStream fis = context.openFileInput(fileName);
//        ObjectInputStream is = new ObjectInputStream(fis);
//        SimpleClass simpleClass = (SimpleClass) is.readObject();
//        is.close();
//        fis.close();
        String save_files[] = null;
        try {
            save_files = new File(".").list(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".ser");
                }
            });
        } catch(Exception e) {
            Log.d("FAILED TO GET FILE NAMES", "AHHH", e);
        }

        if (save_files == null) {
            Log.d("ERROR", "SAVE_FILES IS NULL!");
            return;
        }
        for (String s : save_files) {
            Log.d("SAVE_FILES", s);
            SavedGameInfo game = new SavedGameInfo(s, Calendar.getInstance(), null);
            saved_games.add(game);
        }
        //adapter.notifyDataSetChanged();
    }
}
