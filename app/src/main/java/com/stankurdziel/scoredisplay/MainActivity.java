package com.stankurdziel.scoredisplay;

import android.app.*;
import android.os.*;
import android.view.View;
import android.widget.*;
import com.stankurdziel.scoredisplay.MainActivity.*;

public class MainActivity extends Activity {
	
	private TextView scoreView;
	private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		scoreView = (TextView) findViewById(R.id.score);
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
				scoreView.setText(String.valueOf(score));
				scoreView.invalidate();
			}
		};
		findViewById(R.id.down).setOnClickListener(onClick);
		findViewById(R.id.up).setOnClickListener(onClick);
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
}
