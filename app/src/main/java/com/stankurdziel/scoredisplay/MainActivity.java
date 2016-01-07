package com.stankurdziel.scoredisplay;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
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
        findViewById(R.id.down).setOnClickListener(onClick);
        findViewById(R.id.up).setOnClickListener(onClick);
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
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SCORE, score);
        super.onSaveInstanceState(outState);
    }
}
