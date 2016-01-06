package com.stankurdziel.scoredisplay;

import android.app.*;
import android.os.*;
import android.view.View;
import android.widget.*;
import com.stankurdziel.scoredisplay.MainActivity.*;

public class MainActivity extends Activity {
	
	private TextView scoreView;
	private int score = 0;

	private static String SCORE = "SCORE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		scoreView = (TextView) findViewById(R.id.score);
		if(savedInstanceState != null) {
			score = savedInstanceState.getInt(SCORE, 0);
			updateUi();
		}

		View.OnClickListener onClick = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()) {
					case R.id.down:
						if (score>0) score--;
					    break;
					case R.id.up:
						score++;
						break;
				}
				updateUi();
			}
		};
		findViewById(R.id.down).setOnClickListener(onClick);
		findViewById(R.id.up).setOnClickListener(onClick);
    }

	
	private void updateUi() {
		scoreView.setText(String.valueOf(score));
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
