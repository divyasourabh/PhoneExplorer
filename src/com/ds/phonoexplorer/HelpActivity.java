package com.ds.phonoexplorer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.ds.phonoexplorer.utils.Utils;

public class HelpActivity extends Activity {

	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.overridePendingTransition(R.anim.in, R.anim.out);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_help_swipe);
		mContext = this;
		ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
		HelpActivityImageAdapter adapter = new HelpActivityImageAdapter(this);
		viewPager.setAdapter(adapter);

		if(Utils.mSoundStatus){
			Intent svc = new Intent(mContext, BackgroundSoundService.class);
			startService(svc);
		}
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) 
			{     

				//					Toast.makeText(mContext, "position = "  + position, Toast.LENGTH_LONG).show();
				if (position == 1){
					Utils.helpDialog(mContext);
				}
				if (position > 9){
					if(Utils.mFloatinIconStatus){
						startService(new Intent(mContext,floatingUIService.class));
					}
					if(Utils.mSoundStatus){
						Intent svc = new Intent(mContext, BackgroundSoundService.class);
						startService(svc);
					}
					helpDialog(mContext);
				}
			}
			@Override
			public void onPageScrollStateChanged(int state)
			{
			}
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){
			}
		});
	}

	public void helpDialog(final Context mContext){

		AlertDialog.Builder helpDialog = new AlertDialog.Builder(mContext);
		helpDialog.setMessage("Floating Icon always with you on HomeScreen, Just double click on Icon,And start Exploring your phone quickly.");
		helpDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Intent intent = new Intent(HelpActivity.this, SearchActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);      
				finish();
			}
		});

		helpDialog.setNeutralButton("More Help",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.myAppVideoLink)));
			}
		});

		helpDialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

			}
		});
		AlertDialog alert1 = helpDialog.create();
		alert1.show();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.help, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(Utils.mOptionMenuSoundStatus)
			Utils.optionMenuSound(mContext);

		switch (item.getItemId()) {
		case R.id.action_about_us:
			Intent intentAbout = new Intent(this, About_Us_Activity.class);
			startActivity(intentAbout);
			return true;
		case R.id.action_rate_us:
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.myAppPlayStoreLink)));
			return true;
		case R.id.action_promo_video:
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.myAppVideoLink)));
			return true;
		case R.id.action_settings:
			startActivity(new Intent(this,SettingActivity.class));
			return true;
		default:
			return false;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		Intent intentSerach = new Intent(this, SearchActivity.class);
		startActivity(intentSerach);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
}
