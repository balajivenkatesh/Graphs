package com.graphs1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import static com.graphs1.MainActivity.act;
import static com.graphs1.MainActivity.frame;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class GameBoard extends View implements OnTouchListener, OnClickListener {
	private Paint p, q;
	private List<Point> starField = null, targetField = null;
	private int starAlpha = 80;
	private int starFade = 2;
	private int targSize = 80;
	List<Integer> prevTargSize = new ArrayList<Integer>();
	private int targFade = 1;
	private int width, height;
	Point prevPoint;
	boolean s = true;
	int score=0, lastScore=0, highScore=0;
	List<Point> points = new ArrayList<Point>();
	List<Integer> alpha = new ArrayList<Integer>();
	private static final int NUM_OF_STARS = 25, NUM_OF_TARGS = 1;
	int CURR_TARG=1;
	Dialog dialog=null;

	synchronized public void resetStarField() {
		starField = null;
	}

	public GameBoard(Context context, AttributeSet aSet) {
		super(context, aSet);
		// it's best not to create any new objects in the on draw
		// initialize them as class variables here
		p = new Paint();
		q = new Paint();
		dialog = new Dialog(act.getBaseContext());
		setFocusableInTouchMode(true);
		this.setOnTouchListener(this);
	}

	private void initializeStars(int maxX, int maxY) {
		starField = new ArrayList<Point>();
		for (int i = 0; i < NUM_OF_STARS; i++) {
			Random r = new Random();
			int x = r.nextInt(maxX - 5 + 1) + 5;
			int y = r.nextInt(maxY - 5 + 1) + 5;
			starField.add(new Point(x, y));
		}
	}

	private void initializeTarget(int maxX, int maxY) {
		if (targetField == null)
			targetField = new ArrayList<Point>();

		targSize = 80;
		for (int i = 0; i < NUM_OF_TARGS; i++) {
			Random r = new Random();
			int x = r.nextInt(maxX - 5 + 1) + 5;
			int y = r.nextInt(maxY - 5 + 1) + 5;
			targetField.add(new Point(x, y));
			
		}
	}

	private boolean checkColl(Point point) {
		int x1 = point.x - targetField.get(targetField.size() - 1).x;
		int y1 = point.y - targetField.get(targetField.size() - 1).y;
		if ((x1 * x1 + y1 * y1) < targSize * targSize * 1.21) {
			prevTargSize.add(targSize);
			lastScore=targSize*targFade;
			score+=lastScore;
			CURR_TARG--;
			initializeTarget(width, height);
		}
		return false;
	}

	public boolean onTouch(View view, MotionEvent event) {
		// Log.d("hello",event.getAction()+"");
		if (event.getAction() == MotionEvent.ACTION_UP) {
			alpha.clear();
			points.clear();
			prevTargSize.clear();
			targetField.clear();
			lastScore=0;
			if(score>highScore)
				highScore=score;
			score=0;
			
			s = false;
//			frame.removeCallbacks(((MainActivity) act).frameUpdate);
//			enter(dialog);
			initializeTarget(width, height);
			// Log.d("hello", "no touch");
			return super.onTouchEvent(event);
		}
		Point point = new Point();
		point.x = (int) event.getX();
		point.y = (int) event.getY();
		Log.d("hello","at "+event.getX()+" , "+event.getY());
		checkColl(point);

		points.add(point);
		alpha.add(255);
		// ((GameBoard)findViewById(R.id.the_canvas)).invalidate();
		// s=true;
		return true;
	}

	public void enter(Dialog dialog){
    	dialog.setContentView(R.layout.dialog);
    	dialog.setTitle("Enter Values");
    	dialog.setCancelable(true);
    	dialog.show();
    	View b1=dialog.findViewById(R.id.button01);
		b1.setOnClickListener(this);
		View b2=dialog.findViewById(R.id.button02);
		b2.setOnClickListener(this);
    }	
	
	public void onClick(View v){	
		
		switch(v.getId()){
		case R.id.button01:
			dialog.cancel();
			break;
		case R.id.button02:
			dialog.dismiss();
			break;
		}
	}
	
	@Override
	synchronized public void onDraw(Canvas canvas) {
		// create a black canvas
		// p.setColor(Color.BLACK);
		// p.setAlpha(255);
		// p.setStrokeWidth(1);
		// canvas.drawRect(0, 0, getWidth(), getHeight(), p);
		// initialize the starfield if needed
		width = canvas.getWidth();
		height = canvas.getHeight();
		if (starField == null) {
			initializeStars(canvas.getWidth(), canvas.getHeight());
		}
		if (targetField == null) {
			initializeTarget(canvas.getWidth(), canvas.getHeight());
		}

		// draw the stars
		p.setColor(Color.CYAN);
		p.setAlpha(starAlpha += starFade);
		q.setColor(Color.WHITE);
		q.setStrokeWidth(5);

		targSize -= targFade;

		int i = 0;
		for (int p : prevTargSize) {
			prevTargSize.set(i, prevTargSize.get(i) - 1);
			i++;
		}

		// fade them in and out
		if (starAlpha >= 252 || starAlpha <= 20)
			starFade = starFade * -1;
		if (targSize < 1) {
			lastScore=0;
			if(score>highScore)
				highScore=score;
			score=0;
			alpha.clear();
			points.clear();
			targetField.clear();
			prevTargSize.clear();
			initializeTarget(canvas.getWidth(), canvas.getHeight());

		}

		p.setStrokeWidth(5);
		p.setAlpha(255 - starAlpha);
		
		for (i = 0; i < NUM_OF_STARS; i += 2) {
			canvas.drawPoint(starField.get(i).x, starField.get(i).y, p);
		}
		p.setAlpha(starAlpha);
		for (i = 1; i < NUM_OF_STARS; i += 2) {
			canvas.drawPoint(starField.get(i).x, starField.get(i).y, p);
		}

		i = 0;

		if (!points.isEmpty()) {
			prevPoint = points.get(0);
			for (Point point : points) {
				int a = alpha.get(i);
				// int a=255;
				if (a > 20) {
					q.setAlpha(a);
					alpha.set(i, a - 20);
					canvas.drawLine(prevPoint.x, prevPoint.y, point.x, point.y, q);
				}
				prevPoint = point;
				// else
				// {
				// alpha.remove(i);
				// // points.remove(i);
				// points.remove(point);
				// }
				i++;
			}
		}

		for (Point point : points) {
			canvas.drawPoint(point.x, point.y, p);
		}
		
		if (!targetField.isEmpty()) {
			p.setAlpha(255);
			int s = targetField.size() - 1;
			Iterator<Point> point = targetField.iterator();
			i = 0;
			p.setColor(Color.GREEN);
			while (point.hasNext() ) {
				prevPoint = point.next();
				if (i != s) {
					canvas.drawCircle(prevPoint.x, prevPoint.y,
							prevTargSize.get(i) / 8, p);
				} else {
					p.setColor(Color.RED);
					canvas.drawCircle(prevPoint.x, prevPoint.y, targSize / 4, p);
				}
				i++;
			}
			p.setColor(Color.CYAN);
			if(lastScore!=0){
				prevPoint=targetField.get(s-1);
				canvas.drawText(""+lastScore, prevPoint.x+20, prevPoint.y, p);
			}
//			if(score!=0){
			p.setTextSize(50);
				canvas.drawText(""+score, 20, 50, p);
				canvas.drawText(""+highScore, width-150, 50, p);
//			}
		}
	}
}
