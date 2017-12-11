package com.example.group55.androidchess55.models;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;

import java.text.ParseException;


public class SavedGameInfo {
    private String title;
    private Calendar date;
    private String file_name;

    public SavedGameInfo(String file_name) {
        this.file_name = file_name;
        String[] data = file_name.split("~");
        title = data[0];
        date = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        try {
            date.setTime(sdf.parse(data[1].substring(4)));//
        } catch (ParseException e) {

        }
    }

    public String toString() {
        return title + "\n" + new SimpleDateFormat("dd.MM.yyyy").format(date.getTime());
    }

    public String getFileName() {
        return file_name;
    }
}
