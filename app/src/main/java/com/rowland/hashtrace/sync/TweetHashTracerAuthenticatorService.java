package com.rowland.hashtrace.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 *
 * The service which allows the sync adapter framework to access the
 * authenticator.
 *
 * @author Rowland
 *
 */
public class TweetHashTracerAuthenticatorService extends Service {

	// Instance field that stores the authenticator object
	private TweetHashTracerAuthenticator mAuthenticator;

	@Override
	public void onCreate() {
		// Create a new authenticator object
		mAuthenticator = new TweetHashTracerAuthenticator(this);
	}

	/*
	 * When the system binds to this Service to make the RPC call return the
	 * authenticator's IBinder.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return mAuthenticator.getIBinder();
	}

}
