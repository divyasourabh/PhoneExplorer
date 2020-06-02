package com.ds.phonoexplorer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.ds.phonoexplorer.utils.SharedDataStore;
import com.ds.phonoexplorer.utils.Utils;

public class MainActivity extends Activity {

	private Context mContext;
	private SharedDataStore mSharedDataStore;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//		startService(new Intent(MainActivity.this, floatingUIService.class));
		setContentView(R.layout.main_layout);
		
		mContext = this;
		
		mSharedDataStore = SharedDataStore.getInstance( mContext );
		
		Utils.mSoundStatus = SharedDataStore.getInstance(mContext).getSoundStatus();
		Utils.mOptionMenuSoundStatus = SharedDataStore.getInstance(mContext).getoptionMenuSoundStatus();
		Utils.mFloatinIconStatus = SharedDataStore.getInstance(mContext).getFloatingIconStatus();
		Utils.mFloatinIconRebootStatus= SharedDataStore.getInstance(mContext).getFloatingIconRebootStatus();
		Utils.mFirstTimeLaunch = SharedDataStore.getInstance(mContext).getFirstTimeStatus();

		Bundle bundle = getIntent().getExtras();
/*
		if(Utils.mFirstTimeLaunch)
		{
			mSharedDataStore.setFirstTimeStatus(false);
			mSharedDataStore.commitSettings();
			
			Intent i = new Intent(MainActivity.this, HelpActivity.class);
			this.startActivity(i);

			if(Utils.mSoundStatus){
				Intent svc = new Intent(this, BackgroundSoundService.class);
				startService(svc);
			}
			this.finish();
		}else{
			if(bundle != null && bundle.getString("LAUNCH").equals("YES")) {
				if(Utils.mFloatinIconStatus){
					startService(new Intent(this,floatingUIService.class));
					Toast.makeText(this, "Icon always with you,Just double click on it, When you want", Toast.LENGTH_LONG).show();
				}
			}*/
			Intent intent = new Intent(MainActivity.this,SearchActivity.class);
			startActivity(intent);
			finish();
		//}
	}

	@Override
	protected void onResume() {
		Bundle bundle = getIntent().getExtras();

		if(bundle != null && bundle.getString("LAUNCH").equals("YES")) {
			if(Utils.mFloatinIconStatus){
				startService(new Intent(this,floatingUIService.class));
				Toast.makeText(this, "Icon always with you,Just double click on it, When you want", Toast.LENGTH_LONG).show();
			}

		}
		super.onResume();
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
}
