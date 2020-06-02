package com.ds.phonoexplorer;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class floatingUIService extends Service {

	private InterstitialAd mInterstitialAd;
	private WindowManager windowManager;
	private ImageView chatHead;
	private ImageView stopHead;
	private LayoutInflater layoutInflator;
	private LinearLayout layout;
	private LinearLayout stopLayout;

	View myview;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override 
	public void onCreate() {
		super.onCreate();

		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		layoutInflator =  (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);     

		stopLayout = new LinearLayout(this);
		stopLayout.setOrientation(LinearLayout.VERTICAL);

		chatHead = new ImageView(this);
		stopHead = new ImageView(this);

		chatHead.setImageResource(R.drawable.phoneexplorer);
		stopHead.setImageResource(R.drawable.cross);
		/*		stopHead.setText(R.drawable.cross);
		stopHead.setTextSize((int)this.getResources().getDimension(R.dimen.stop_btn_text_size));
		stopHead.setTypeface(null ,Typeface.BOLD);
		stopHead.setBackgroundColor(Color.TRANSPARENT);
		stopHead.setTextColor(Color.RED);*/
		stopHead.setPadding(0, 0, 0, (int)this.getResources().getDimension(R.dimen.stop_btn_bottom_mar));

		layout.addView(chatHead);
		stopLayout.addView(stopHead);

		initAd();

		chatHead.setOnClickListener(new OnClickListener() {
			private static final long DOUBLE_CLICK_TIME_DELTA = 400;//milliseconds

			long lastClickTime = 0;
			@Override
			public void onClick(View arg0) {

				long clickTime = System.currentTimeMillis();
				if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA){
					try {
						Intent intent = new Intent(getApplicationContext(),SearchActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);	
					} catch (Exception e) {
						e.printStackTrace();
						// TODO: handle exception
					}catch (Error e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}					
				lastClickTime = clickTime;
			}
		});

		final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = 0;
		params.y = 200;

		windowManager.addView(layout, params);

		final WindowManager.LayoutParams stopParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		stopParams.gravity = Gravity.BOTTOM | Gravity.CENTER;

		windowManager.addView(stopLayout, stopParams);
		stopHead.setVisibility(View.GONE);


		try {

			chatHead.setOnTouchListener(new View.OnTouchListener() {
				private WindowManager.LayoutParams paramsF = params;
				private int initialX;
				private int initialY;
				private float initialTouchX;
				private float initialTouchY;

				private Rect rectHead,rectStop;
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int[] l = new int[2];
					stopHead.getLocationOnScreen(l);

					int x = l[0];
					int y = l[1];
					int w = stopHead.getWidth();
					int h = stopHead.getHeight();
					rectStop = new Rect(x, y, x+ w, y + h);

					chatHead.getLocationOnScreen(l);
					x = l[0];
					y = l[1];
					w = stopHead.getWidth();
					h = stopHead.getHeight();
					rectHead = new Rect(x, y, x+ w, y+h);


					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:

						// Get current time in nano seconds.

						initialX = paramsF.x;
						initialY = paramsF.y;
						initialTouchX = event.getRawX();
						initialTouchY = event.getRawY();
						break;
					case MotionEvent.ACTION_UP:
						stopHead.setVisibility(View.GONE);
						try{
							if(rectStop.intersect(rectHead)){
								stopSelf();
								sendBroadcast(new Intent("close"));
								if (mInterstitialAd.isLoaded()) {
									mInterstitialAd.show();
								}
							}
						}catch(Exception e){
							e.printStackTrace();
						}
						//						return true;
						break;

					case MotionEvent.ACTION_MOVE:
						paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
						paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
						windowManager.updateViewLayout(layout, paramsF);
						stopHead.setVisibility(View.VISIBLE);
						try{
							if(rectStop.intersect(rectHead)){
								System.out.println("==================================");
								if (mInterstitialAd.isLoaded()) {
									mInterstitialAd.show();
								}
							}
						}catch(Exception e){
							e.printStackTrace();
						}

						//						}

						break;
					}
					return false;
				}

			});
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (layout != null)
			windowManager.removeView(layout);
//		if (mInterstitialAd.isLoaded()) {
//			mInterstitialAd.show();
//		}

	}
	private void initAd(){

//		mInterstitialAd = new InterstitialAd(this);
//		mInterstitialAd.setAdUnitId(getString(R.string.Interstitial_ad_ad_unit_id));
//
//		// Create an ad request.
//		AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
//		mInterstitialAd.loadAd(adRequestBuilder.build());

//		//For testing
				mInterstitialAd = new InterstitialAd(this);
		mInterstitialAd.setAdUnitId("ca-app-pub-123456789/123456789");

		// Request for Ads

		AdRequest adRequest = new AdRequest.Builder()

		// Add a test device to show Test Ads
		.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
		.addTestDevice("6F34A77DBC1CE047184CBCBF1EF48D04")
		.build();
		mInterstitialAd.loadAd(adRequest);
	}
}
