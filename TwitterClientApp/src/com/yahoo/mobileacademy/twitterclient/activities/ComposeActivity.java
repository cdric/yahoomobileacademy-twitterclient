package com.yahoo.mobileacademy.twitterclient.activities;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yahoo.mobileacademy.twitterclient.R;
import com.yahoo.mobileacademy.twitterclient.apps.MyTwitterApp;
import com.yahoo.mobileacademy.twitterclient.models.Tweet;
import com.yahoo.mobileacademy.twitterclient.models.User;

public class ComposeActivity extends Activity {

	EditText etTweet;
	ImageView ivProfileIcon;
	TextView tvUser;
	TextView tvWordCountLeft;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose);

		setupView();

		// Update button bar background color
		View view = findViewById(R.id.rl_buttonBar);
		view.setBackgroundColor(0xAAAAAAAA);

		MyTwitterApp.getRestClient().getUserInfo(new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(JSONObject jsonUser) {
				User userDetails = User.fromJson(jsonUser);
				
				// Update current user view
				ImageLoader.getInstance().displayImage(userDetails.getProfileImageUrl(), ivProfileIcon);
				tvUser.setText("@" + userDetails.getScreenName());
			}
			
			@Override
			public void onFailure(Throwable e, JSONObject error) {
			
				// Display an error message to the user
				Toast.makeText(getBaseContext(), "Can't fetch user information because of the following error: " + e.toString(), Toast.LENGTH_SHORT).show();
				
				Log.e("ERROR", e.toString());
			}

		}, null);
		
		// Register textChangesListener on the etTweet EditText View
		// To update the current word count
		etTweet.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {

				int charLeft = 140 - s.length();
				if (charLeft < 20) {
					tvWordCountLeft.setTextColor(Color.RED);
				} else {
					tvWordCountLeft.setTextColor(Color.GRAY);
				}

				tvWordCountLeft.setText(String.valueOf(charLeft)
						+ " character(s) left");

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
	}

	private void setupView() {
		etTweet = (EditText) findViewById(R.id.etTweet);
		ivProfileIcon = (ImageView) findViewById(R.id.iv_profile_icon);
		tvUser = (TextView) findViewById(R.id.tv_user);
		tvWordCountLeft = (TextView) findViewById(R.id.tv_count_wordleft);
	}

	/**
	 * When user click the "Cancel" button
	 * 
	 * @param v
	 */
	public void onCancelAction(View v) {

		setResult(RESULT_CANCELED);
		finish();

	}

	/**
	 * When user click the "Tweet" button
	 *  - Post the tweet
	 *  - Add the Tweet to the Intent
	 * 
	 * @param v
	 */
	public void onTweetAction(View v) {

		MyTwitterApp.getRestClient().postTweet(etTweet.getText().toString(), new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(JSONObject jsonTweet) {
				
				Tweet tweet = Tweet.fromJson(jsonTweet);
				Intent i = getIntent();
				i.putExtra("tweet", tweet);
				setResult(RESULT_OK, i);
				finish();
				
			}
			
			@Override
			public void onFailure(Throwable e, JSONObject error) {
			
				// Display an error message to the user
				Toast.makeText(getBaseContext(), "Can't post your tweet because of the following error: " + e.toString(), Toast.LENGTH_SHORT).show();
				
				Log.e("ERROR", e.toString());
			}
			
		});
			
	}

}
