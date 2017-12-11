package com.example.group55.androidchess55.models;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;

import java.util.ArrayList;


public class SavedGameInfo {
    private String title;
    private Calendar date;
    private ArrayList<ChessPiece[]> recording;

    public SavedGameInfo(String t, Calendar d, ArrayList<ChessPiece[]> r) {
        title = t;
        date = d;
        recording = r;
    }

    public String toString() {
        return title + "\n" + new SimpleDateFormat("dd.MM.yyyy").format(date.getTime());
    }

    public ArrayList<ChessPiece[]> getRecording() {
        return recording;
    }
}
