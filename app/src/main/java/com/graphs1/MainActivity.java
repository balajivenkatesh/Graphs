package com.graphs1;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
//import com.graphs1.R;
//import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
	public static Activity act;
	public static Handler frame = new Handler();
	// Divide the frame by 1000 to calculate how many times per second the
	// screen will update.
	private static final int FRAME_RATE = 20; // 50 frames per second

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		act = MainActivity.this;
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		Handler h = new Handler();
		// ((Button)findViewById(R.id.the_button)).setOnClickListener(this);
		// We can't initialize the graphics immediately because the layout
		// manager
		// needs to run first, thus we call back in a sec.
		h.postDelayed(new Runnable() {
			@Override
			public void run() {
				initGfx();
			}
		}, 1000);
	}

	synchronized public void initGfx() {
		((GameBoard) findViewById(R.id.the_canvas)).resetStarField();
		// ((Button)findViewById(R.id.the_button)).setEnabled(true);
		// It's a good idea to remove any existing callbacks to keep
		// them from inadvertently stacking up.
		frame.removeCallbacks(frameUpdate);
		frame.postDelayed(frameUpdate, FRAME_RATE);
	}
	
	@Override
	synchronized public void onClick(View v) {
		initGfx();
	}

	public Runnable frameUpdate = new Runnable() {
		@Override
		synchronized public void run() {
			frame.removeCallbacks(frameUpdate);
			// make any updates to on screen objects here
			// then invoke the on draw by invalidating the canvas
			((GameBoard) findViewById(R.id.the_canvas)).invalidate();
			frame.postDelayed(frameUpdate, FRAME_RATE);
		}
	};
}