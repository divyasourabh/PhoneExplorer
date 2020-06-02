package com.ds.phonoexplorer;

import com.ds.phonoexplorer.utils.Utils;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class BackgroundSoundService extends Service {

	MediaPlayer player;
	public IBinder onBind(Intent arg0) {

		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		player = MediaPlayer.create(this, R.raw.intro);
		//        player.setLooping(true); // Set looping
		player.setVolume(100,100);

	}
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(Utils.mSoundStatus || Utils.mOptionMenuSoundStatus){
			player.start();
			return 1;
		}else{
			return 0;
		}
	}

	public void onStart(Intent intent, int startId) {
		// TO DO
	}
	public IBinder onUnBind(Intent arg0) {
		// TO DO Auto-generated method
		return null;
	}

	public void onStop() {

	}
	public void onPause() {

	}
	@Override
	public void onDestroy() {
		player.stop();
		player.release();
	}

	@Override
	public void onLowMemory() {

	}
}
