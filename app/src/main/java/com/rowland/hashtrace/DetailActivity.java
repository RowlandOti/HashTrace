package com.rowland.hashtrace;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * @author Rowland
 *
 */
public class DetailActivity extends ActionBarActivity {

	public static final String DATE_KEY = "tweet_text_date";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_details);
	}


}
