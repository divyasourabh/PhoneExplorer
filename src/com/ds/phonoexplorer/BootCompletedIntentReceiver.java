package com.ds.phonoexplorer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ds.phonoexplorer.utils.SharedDataStore;

public class BootCompletedIntentReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			Intent pushIntent = new Intent(context, floatingUIService.class);

			Log.d("PhoneExplorer","BootCompletedIntentReceiver getFloatingIconRebootStatus value  =  " + SharedDataStore.getInstance(context).getFloatingIconRebootStatus() + "");

			if(SharedDataStore.getInstance(context).getFloatingIconRebootStatus()  && SharedDataStore.getInstance(context).getFloatingIconStatus()){
				context.startService(pushIntent);
			}

		}
	}
}
