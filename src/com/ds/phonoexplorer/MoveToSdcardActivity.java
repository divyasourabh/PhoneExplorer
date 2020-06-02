package com.ds.phonoexplorer;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.ds.phonoexplorer.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MoveToSdcardActivity extends Activity {
	
	private AdView mAdView;
	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		ActionBar actionbar = getActionBar();
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setDisplayShowTitleEnabled(false);
		actionbar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		actionbar.setHomeButtonEnabled(false);
		setContentView(R.layout.activity_movetosdcard);
		mContext = this;

		mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		//	        adRequest.addTestDevice("B4CB513C15C8C177AD0768170C69F779");
		//	        adRequest.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
		mAdView.loadAd(adRequest);

		findViewById(R.id.pop).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	@Override
	public void onPause() {
		if (mAdView != null) {
			mAdView.pause();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onRestart();
		if (mAdView != null) {
			mAdView.resume();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mAdView != null) {
			mAdView.destroy();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.uninstall, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(Utils.mOptionMenuSoundStatus)
			Utils.optionMenuSound(mContext);
		
		switch (item.getItemId()) {
		case R.id.action_about_us:
			Intent intentAbout = new Intent(this, About_Us_Activity.class);
			startActivity(intentAbout);
			return true;
/*		case R.id.action_help:
			Intent intenthelp = new Intent(this, HelpActivity.class);
			startActivity(intenthelp);
			return true;*/
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
}
