package com.ds.phonoexplorer;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.ds.phonoexplorer.utils.SharedDataStore;
import com.ds.phonoexplorer.utils.Utils;
public class SettingActivity extends PreferenceActivity implements OnPreferenceChangeListener {

	private Context mContext;
//	private AdView mAdView;

	private CheckBoxPreference mSound;
	private CheckBoxPreference mOptionMenuSound;
	private CheckBoxPreference mShowFloatingIcon;
	private CheckBoxPreference mShowFloatingIconReboot;
	private CheckBoxPreference mShowHiddenFiles;

	private boolean soundStatusValue;
	private boolean optionMenuSoundStatusValue;
	private boolean floatingIconStatusValue;
	private boolean floatingIconStatusrebootValue;
	private boolean showHiddenFilesValue;
	
	private SharedDataStore mSharedDataStore;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		this.overridePendingTransition(R.anim.in, R.anim.out);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayHomeAsUpEnabled(false);
		actionbar.setHomeButtonEnabled(false);

		mSharedDataStore = SharedDataStore.getInstance( this );

		addPreferencesFromResource(R.layout.activity_setting);

		mContext = this;
		//		setContentView(R.layout.activity_setting);
		/*mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		//	        adRequest.addTestDevice("B4CB513C15C8C177AD0768170C69F779");
		//	        adRequest.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
		mAdView.loadAd(adRequest);*/

		initValues();

		mSound.setOnPreferenceChangeListener( this );
		mOptionMenuSound.setOnPreferenceChangeListener( this );
		mShowFloatingIcon.setOnPreferenceChangeListener( this );
		mShowFloatingIconReboot.setOnPreferenceChangeListener( this );
		mShowHiddenFiles.setOnPreferenceChangeListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
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
		default:
			return false;
		}
	}


	@SuppressWarnings("deprecation")
	private void initValues() {

		soundStatusValue = SharedDataStore.getInstance(mContext).getSoundStatus();
		mSound = (CheckBoxPreference) findPreference("sound");
		mSound.setChecked(soundStatusValue);
		Log.d("TEST", soundStatusValue+"  msound status");
		mSound.setOnPreferenceClickListener( new OnPreferenceClickListener(){

			@Override
			public boolean onPreferenceClick(Preference arg0) {
				// TODO Auto-generated method stub

				if(Utils.mOptionMenuSoundStatus)
					Utils.optionMenuSound(mContext);

				SharedPreferences pref = getSharedPreferences(Utils.prefName, Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit();
				editor.putBoolean("sound", mSound.isChecked());
				Log.d("TEST", mSound.isChecked()+"  in msound click");
				editor.commit();
				return false;
			}
		});

		optionMenuSoundStatusValue = SharedDataStore.getInstance(mContext).getoptionMenuSoundStatus();
		mOptionMenuSound = (CheckBoxPreference) findPreference("option_menu_sound");
		mOptionMenuSound.setChecked(optionMenuSoundStatusValue);
		mOptionMenuSound.setOnPreferenceClickListener( new OnPreferenceClickListener(){

			@Override
			public boolean onPreferenceClick(Preference arg0) {
				// TODO Auto-generated method stub
				if(Utils.mOptionMenuSoundStatus)
					Utils.optionMenuSound(mContext);

				SharedPreferences pref = getSharedPreferences(Utils.prefName, Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit();
				editor.putBoolean("option_menu_sound", mOptionMenuSound.isChecked());
				Log.d("TEST", mOptionMenuSound.isChecked()+"  in mOptionMenuSound click");
				editor.commit();
				return false;
			}
		});

		floatingIconStatusValue = SharedDataStore.getInstance(mContext).getFloatingIconStatus();
		mShowFloatingIcon = (CheckBoxPreference) findPreference("show_floating_icon");
		mShowFloatingIcon.setChecked(floatingIconStatusValue);
		mShowFloatingIcon.setOnPreferenceClickListener( new OnPreferenceClickListener(){

			@Override
			public boolean onPreferenceClick(Preference arg0) {
				// TODO Auto-generated method stub
				if(Utils.mOptionMenuSoundStatus)
					Utils.optionMenuSound(mContext);

				SharedPreferences pref = getSharedPreferences(Utils.prefName, Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit();
				editor.putBoolean("show_floating_icon", mShowFloatingIcon.isChecked());
				editor.commit();
				return false;
			}
		});

		floatingIconStatusrebootValue = SharedDataStore.getInstance(mContext).getFloatingIconRebootStatus();
		mShowFloatingIconReboot = (CheckBoxPreference) findPreference("show_floating_icon_reboot");
		mShowFloatingIconReboot.setChecked(floatingIconStatusrebootValue);
		mShowFloatingIconReboot.setOnPreferenceClickListener( new OnPreferenceClickListener(){

			@Override
			public boolean onPreferenceClick(Preference arg0) {
				// TODO Auto-generated method stub
				if(Utils.mOptionMenuSoundStatus)
					Utils.optionMenuSound(mContext);

				SharedPreferences pref = getSharedPreferences(Utils.prefName, Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit();
				editor.putBoolean("show_floating_icon_reboot", mShowFloatingIconReboot.isChecked());
				editor.commit();
				return false;
			}
		});
		showHiddenFilesValue = SharedDataStore.getInstance(mContext).getShowHiddenStatus();
		mShowHiddenFiles = (CheckBoxPreference) findPreference("show_hidden");
		mShowHiddenFiles.setChecked(showHiddenFilesValue);
		mShowHiddenFiles.setOnPreferenceClickListener( new OnPreferenceClickListener(){

			@Override
			public boolean onPreferenceClick(Preference arg0) {
				// TODO Auto-generated method stub
				if(Utils.mOptionMenuSoundStatus)
					Utils.optionMenuSound(mContext);

				SharedPreferences pref = getSharedPreferences(Utils.prefName, Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit();
				editor.putBoolean("show_hidden", mShowHiddenFiles.isChecked());
				editor.commit();
				return false;
			}
		});

	}

	@Override

	protected void onSaveInstanceState(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(arg0);
		SharedDataStore sharedDataStore = SharedDataStore.getInstance(getBaseContext());
		sharedDataStore.setSoundStatus(mSound.isChecked());
		sharedDataStore.setoptionMenuSoundStatus(mOptionMenuSound.isChecked());
		sharedDataStore.setFloatingIconStatus(mShowFloatingIcon.isChecked());
		sharedDataStore.setFloatingIconRebootStatus(mShowFloatingIconReboot.isChecked());
		sharedDataStore.setShowHiddenStatus(mShowHiddenFiles.isChecked());
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
/*		if (mAdView != null) {
			mAdView.resume();
		}*/
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
/*		if (mAdView != null) {
			mAdView.pause();
		}*/
		super.onPause();
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();

		SharedPreferences pref = getSharedPreferences(Utils.prefName, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean("sound", mSound.isChecked());
		editor.putBoolean("option_menu_sound", mOptionMenuSound.isChecked());
		editor.putBoolean("show_floating_icon", mShowFloatingIcon.isChecked());
		editor.putBoolean("show_floating_icon_reboot", mShowFloatingIconReboot.isChecked());
		editor.putBoolean("show_hidden", mShowHiddenFiles.isChecked());
		editor.commit();
		/*

		if (soundStatusValue != mSound.isChecked() || optionMenuSoundStatusValue != mOptionMenuSound.isChecked()
				|| floatingIconStatusValue != mShowFloatingIcon.isChecked()) {


			SharedDataStore sharedDataStore = SharedDataStore.getInstance(getBaseContext());

			sharedDataStore.setSoundStatus(mSound.isChecked());

			sharedDataStore.setSoundStatus(mOptionMenuSound.isChecked());

			sharedDataStore.setFloatingIconStatus(mShowFloatingIcon.isChecked());



		} else {
			setResult(Activity.RESULT_CANCELED);
		}


		 */}

	@Override
	protected void onDestroy() {
		super.onDestroy();
/*		if (mAdView != null) {
			mAdView.destroy();
		}*/
		mSharedDataStore.commitSettings();
	}


	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		if ( preference.equals( mSound ) ) {

			mSharedDataStore.setSoundStatus((Boolean) newValue );
			Log.d("TEST", mSound.isChecked()+"  in onPreferenceChange msound click " + newValue);
			return true;

		} else if ( preference.equals( mOptionMenuSound ) ) {

			mSharedDataStore.setoptionMenuSoundStatus((Boolean) newValue );
			return true;
			
		} else if ( preference.equals( mShowFloatingIcon ) ) {

			mSharedDataStore.setFloatingIconStatus((Boolean) newValue );
			return true;
			
		}else if(preference.equals(mShowFloatingIconReboot)){
			
			mSharedDataStore.setFloatingIconRebootStatus((Boolean) newValue );
			return true;
		}else if(preference.equals(mShowHiddenFiles)){
			
			mSharedDataStore.setShowHiddenStatus((Boolean) newValue );
			return true;
		}

		return false;
	}
}
