package com.harry.storq.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.harry.storq.R;
import com.harry.storq.utils.ParseConstants;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class Gesture2Activity extends Activity {

	  	protected TextView text;
	  	protected TextView text1;
	  	protected TextView text2;
	  	protected TextView text3;
	  	
		protected String storq;
		protected Intent chaining;
		protected boolean forwarded;

		protected int counter;
	  	protected GestureDetector gestureDetector;
		protected SwipeRefreshLayout mSwipeRefreshLayout;
		
		
		protected List<ParseObject> mMessages;
		protected Map<String, ParseObject> MapMessages;
		protected ArrayList<String> parseObjectId;
		
		protected RelativeLayout relativLayout;
	  
	  @SuppressWarnings("deprecation")
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_gesture2);
			text = (TextView) findViewById(R.id.textView);
			text1 = (TextView) findViewById(R.id.textView1);
			text2= (TextView) findViewById(R.id.textView2);
			text3 = (TextView) findViewById(R.id.textView3);

			text1.setText("");
			text2.setText("");
			text3.setText("");
			
			//setting up the wallpaper.
			relativLayout = (RelativeLayout) findViewById(R.id.pager);
			if(ParseUser.getCurrentUser().getString("wallpaper") != null) {
				relativLayout.setBackgroundColor(Color.parseColor(ParseUser.getCurrentUser().getString("wallpaper")));		
			} else {
				relativLayout.setBackgroundColor(Color.parseColor("#ffffff"));		
			}
			
			
			Typeface tf = Typeface.createFromAsset(getAssets(),
	                "fonts/GOTHICB.TTF");
			text.setTypeface(tf);
			
			gestureDetector = new GestureDetector(
	                  new SwipeGestureDetector());
			
		mMessages = new ArrayList<ParseObject>();
		parseObjectId = new ArrayList<String>();
		
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
		query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
		
		MapMessages = new HashMap<String, ParseObject>();
		try {
			mMessages = query.find();
			for(ParseObject m : mMessages) {
				MapMessages.put(m.getObjectId(), m);
				parseObjectId.add(m.getObjectId());
			}
			counter = parseObjectId.size();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		  
		  showStorq();

	  
	  }

	public void showStorq() {
		if (counter > 0) { 
			  	ParseObject dummy = MapMessages.get(parseObjectId.get(counter-1));
			  	String storq = dummy.getString("storq");
			  	String con = dummy.getString("contributors");
				String gender = ParseUser.getCurrentUser().getString("gender");
				if(gender.equals("female")) {
					gender = "f";
				} else if (gender.equals("male")) {
					gender = "m";
				} else {
					gender = "na";
				}
				
			  	 String Location = getIntent().getStringExtra("Location");
			     chaining = new Intent(Gesture2Activity.this, SendStorqActivity.class);
			     chaining.putExtra("storqm", storq);
			     chaining.putExtra("storqt", false);
			     chaining.putExtra("forward", true);
			     if(Location == null) Location = ParseUser.getCurrentUser().getString("location"); 	     
				 String age = ParseUser.getCurrentUser().getString("age");
			     con +=gender.toLowerCase() + ", " + Location.toLowerCase() + " (" + age + ")~";; 
				 deleteSender(dummy);
			     chaining.putExtra("contributors", con);			     
			   
					text.setText(storq);
					String []names = (String[]) ParseConstants.parseString(con);
					
					if(names.length > 2) {
						text1.setText(names[0]);
						text2.setText(names[1]);
						text3.setText(names[2]);
					} else if (names.length > 1) {
						text1.setText(names[0]);
						text2.setText(names[1]);
					} else {
						text1.setText(names[0]);
					}	
					counter--;
		  } else {
			  finish();
		  }
	}
	  
	  
	  @Override
	  public boolean onTouchEvent(MotionEvent event) {
		  if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
		      Log.d("TouchTest", "Touch up");
	    	  showStorq();
		  }
	    if (gestureDetector.onTouchEvent(event)) {
	      return true;
	    }
	    return super.onTouchEvent(event);
	  }

	  private void onLeftSwipe() {
		  showStorq();
	  }

	  private void onRightSwipe() {
		  showStorq(); 
	  }
	  
	  private void onUpSwipe() {
		  startActivity(chaining);
		  finish();
	  }

	  
	  private void onDownSwipe() {
		  
	  }


	private void deleteSender(ParseObject dummy) {
		List<String> ids = dummy.getList(ParseConstants.KEY_RECIPIENT_IDS);
			
			if (ids.size() == 1) {
				// last recipient - delete the whole thing!
				dummy.deleteInBackground();
			}
			else {
				// remove the recipient and save
				ids.remove(ParseUser.getCurrentUser().getObjectId());
				
				ArrayList<String> idsToRemove = new ArrayList<String>();
				idsToRemove.add(ParseUser.getCurrentUser().getObjectId());
				
				dummy.removeAll(ParseConstants.KEY_RECIPIENT_IDS, idsToRemove);
				dummy.saveInBackground();
			}
	}
	  

	  

	  // Private class for gestures
	  private class SwipeGestureDetector
	          extends SimpleOnGestureListener {
	    // Swipe properties, you can change it to make the swipe
	    // longer or shorter and speed
	    private static final int SWIPE_MIN_DISTANCE = 120;
	    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	    @Override
	    public boolean onFling(MotionEvent e1, MotionEvent e2,
	                         float velocityX, float velocityY) {
	      try {
	        float diff = e1.getX() - e2.getX();
	        float diffY = e1.getY() - e2.getY();
	        
	        
	        // Left swipe
	        if (diff > SWIPE_MIN_DISTANCE
	        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	        	Gesture2Activity.this.onLeftSwipe();

	        // Right swipe
	        } else if (-diff > SWIPE_MIN_DISTANCE
	        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	        	Gesture2Activity.this.onRightSwipe();
	        }
	        
	        if (diffY > SWIPE_MIN_DISTANCE
	        && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
	        	Gesture2Activity.this.onUpSwipe();

	        } else if (-diffY > SWIPE_MIN_DISTANCE
	        && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
	        	Gesture2Activity.this.onDownSwipe();
	        }
	        
	        
	      } catch (Exception e) {
	        Log.e("Gesture Activity", "Error on gestures");
	      }
	      return false;
	    }
	  }
	

	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gesture, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
