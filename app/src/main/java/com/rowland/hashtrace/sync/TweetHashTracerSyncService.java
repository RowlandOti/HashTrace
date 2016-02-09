package com.rowland.hashtrace.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * @author Rowland
 *
 */
public class TweetHashTracerSyncService extends Service {

	private static final Object sSyncAdapterLock = new Object();
	private static TweetHashTracerSyncAdapter sTweetHashTracerSyncAdapter = null;

	@Override
	public void onCreate() {

		synchronized (sSyncAdapterLock) {
			if (sTweetHashTracerSyncAdapter == null) {
				sTweetHashTracerSyncAdapter = new TweetHashTracerSyncAdapter(
						getApplicationContext(), true);
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return sTweetHashTracerSyncAdapter.getSyncAdapterBinder();
	}
}
