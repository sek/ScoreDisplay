package com.stankurdziel.scoredisplay;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView scoreL;
    private TextView scoreR;

    private int score = 0;
    private static String SCORE = "SCORE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        scoreL = (TextView) findViewById(R.id.scoreL);
        scoreR = (TextView) findViewById(R.id.scoreR);
        final Typeface font = Typeface.createFromAsset(getAssets(), "Let's go Digital Regular.ttf");
        scoreL.setTypeface(font);
        scoreR.setTypeface(font);
        scoreL.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        scoreR.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        if (savedInstanceState != null) {
            score = savedInstanceState.getInt(SCORE, 0);
            updateUi();
        }

        View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.down:
                        if (score > 0) score--;
                        break;
                    case R.id.up:
                        if (score < 99) score++;
                        break;
                }
                updateUi();
            }
        };
        View.OnLongClickListener onLongClick = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                score = 0;
                updateUi();
                return false;
            }
        };
        final View down = findViewById(R.id.down);
        down.setOnClickListener(onClick);
        down.setOnLongClickListener(onLongClick);
        final View up = findViewById(R.id.up);
        up.setOnClickListener(onClick);
        up.setOnLongClickListener(onLongClick);
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ControllerActivity.class));
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                score = intent.getIntExtra("score", 0);
                updateUi();
            }
        }, new IntentFilter("new-score"));
    }

    private void updateUi() {
        setScore(score);
    }

    private void setScore(int score) {
        String leftCharacter = "";
        if (score / 10 != 0) leftCharacter = String.valueOf(score / 10);
        scoreL.setText(leftCharacter);
        final String rightCharacter = String.valueOf(score % 10);
        scoreR.setText(rightCharacter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFlags();
    }

    private void setFlags() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // TODO in future if score is 0 for a certain amount of time (10 mins?), could let screen turn off
        // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 1.0f;
        getWindow().setAttributes(lp);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SCORE, score);
        super.onSaveInstanceState(outState);
    }
}
