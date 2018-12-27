package com.stankurdziel.scoredisplay;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends Activity {

    private TextView scoreL;
    private TextView scoreR;

    private int score = 0;
    private static String SCORE = "SCORE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        String deviceId = FirebaseInstanceId.getInstance().getId();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference scoreReference = database.getReference("scores/" + deviceId);
        scoreReference.setValue(0);

        Log.d("ScoreDisplay", "FirebaseInstanceId: " + deviceId);

        scoreL = findViewById(R.id.scoreL);
        scoreR = findViewById(R.id.scoreR);
        final Typeface font = Typeface.createFromAsset(getAssets(), "Let's go Digital Regular.ttf");
        scoreL.setTypeface(font);
        scoreR.setTypeface(font);
        scoreL.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        scoreR.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        if (savedInstanceState != null) {
            setScore(savedInstanceState.getInt(SCORE, 0));
        }

        View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.down:
                        incrementScore();
                        break;
                    case R.id.up:
                        decrementScore();
                        break;
                }
            }
        };
        View.OnLongClickListener onLongClick = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setScore(0);
                return true;
            }
        };
        final View down = findViewById(R.id.down);
        down.setOnClickListener(onClick);
        down.setOnLongClickListener(onLongClick);
        final View up = findViewById(R.id.up);
        up.setOnClickListener(onClick);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
    }

    private void subscribeToServerSideDbChanges(DatabaseReference scoreReference) {
        // TODO trigger this when user selects to show barcode
        // TODO would be good to abstract subscribe and publish score
        scoreReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setScore(dataSnapshot.getValue(Integer.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ScoreDisplay", "onCancelled(): " + databaseError.getMessage());
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SCORE, score);
        super.onSaveInstanceState(outState);
    }

    private void decrementScore() {
        if (score < 99) setScore(score + 1);
    }

    private void incrementScore() {
        if (score > 0) setScore(score - 1);
    }

    private void setScore(int i) {
        score = i;
        updateUi();
    }

    private void updateUi() {
        displayScore(score);
    }

    private void displayScore(int score) {
        String leftCharacter = "";
        if (score / 10 != 0) leftCharacter = String.valueOf(score / 10);
        scoreL.setText(leftCharacter);
        final String rightCharacter = String.valueOf(score % 10);
        scoreR.setText(rightCharacter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setFullscreenFullBrightness();
    }

    private void setFullscreenFullBrightness() {
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
}
