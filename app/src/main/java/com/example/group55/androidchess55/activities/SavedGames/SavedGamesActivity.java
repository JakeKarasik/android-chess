package com.example.group55.androidchess55.activities.SavedGames;

import android.content.ContextWrapper;
import android.icu.util.Calendar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
        // Display board
        adapter = new SavedGamesAdapter(SavedGamesActivity.this, saved_games);
        ListView list_view = findViewById(R.id.list_view);
        list_view.setAdapter(adapter);
        loadSavedGames();

        // Set action on user input
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SavedGameInfo g = (SavedGameInfo)parent.getItemAtPosition(position);
                File file = new File(getFilesDir(), g.getFileName());
                file.delete();
            }
        });

    }

    public void loadSavedGames() {
//
        String[] save_files = new ContextWrapper(this).getFilesDir().list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".ser");
            }
        });

        if (save_files == null || save_files.length == 0) {
            return;
        }

        for (String s : save_files) {
            adapter.add(new SavedGameInfo(s));
        }
    }
}
