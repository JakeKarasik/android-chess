package com.example.group55.androidchess55.activities.SavedGames;

import android.content.ContextWrapper;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.group55.androidchess55.R;
import com.example.group55.androidchess55.activities.GameReplay.GameReplayActivity;
import com.example.group55.androidchess55.activities.SavedGames.adapters.SavedGamesAdapter;
import com.example.group55.androidchess55.models.SavedGameInfo;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class SavedGamesActivity extends AppCompatActivity {

    LinkedList<SavedGameInfo> saved_games;
    SavedGamesAdapter adapter;
    ListView list_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_games);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Home Screen");
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        saved_games = new LinkedList<>();
        // Display board
        adapter = new SavedGamesAdapter(SavedGamesActivity.this, saved_games);
        list_view = findViewById(R.id.list_view);
        list_view.setAdapter(adapter);
        loadSavedGames();

        // Set action on user input
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SavedGameInfo g = (SavedGameInfo)parent.getItemAtPosition(position);
                //File file = new File(getFilesDir(), g.getFileName());
                //file.delete();

                Intent intent = new Intent(SavedGamesActivity.this, GameReplayActivity.class);
                intent.putExtra("filename", g.getFileName());
                startActivity(intent);
            }
        });

    }

    public void loadSavedGames() {
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

    public void sortByTitle(View v) {
        Collections.sort(saved_games, new Comparator<SavedGameInfo>() {
            public int compare(SavedGameInfo o1, SavedGameInfo o2) {
                return o1.getTitle().compareToIgnoreCase(o2.getTitle());
            }
        });
        adapter.notifyDataSetChanged();
    }

    public void sortByDate(View v) {
        Collections.sort(saved_games, new Comparator<SavedGameInfo>() {
            public int compare(SavedGameInfo o1, SavedGameInfo o2) {
                return o1.getDT().compareTo(o2.getDT());
            }
        });
        adapter.notifyDataSetChanged();
    }
}
