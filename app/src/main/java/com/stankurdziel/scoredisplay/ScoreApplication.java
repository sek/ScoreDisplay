package com.stankurdziel.scoredisplay;

import android.app.Application;
import android.content.Context;

public class ScoreApplication extends Application {
    private int score;

    public static ScoreApplication getInstance(Context ctx) {
        return (ScoreApplication) ctx.getApplicationContext();
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
