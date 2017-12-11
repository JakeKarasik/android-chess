package com.example.group55.androidchess55.activities.SavedGames;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.group55.androidchess55.R;

import java.io.File;
import java.io.FilenameFilter;

public class SavedGamesActivity extends AppCompatActivity {

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
    }

    public boolean loadSavedGames() {
//        FileInputStream fis = context.openFileInput(fileName);
//        ObjectInputStream is = new ObjectInputStream(fis);
//        SimpleClass simpleClass = (SimpleClass) is.readObject();
//        is.close();
//        fis.close();

        String save_files[] = new File("").list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".ser");
            }
        });

        return true;
    }
}
