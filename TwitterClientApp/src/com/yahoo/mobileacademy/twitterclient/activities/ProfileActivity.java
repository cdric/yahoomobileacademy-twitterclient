package com.yahoo.mobileacademy.twitterclient.activities;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yahoo.mobileacademy.twitterclient.R;
import com.yahoo.mobileacademy.twitterclient.apps.MyTwitterApp;
import com.yahoo.mobileacademy.twitterclient.constants.TwitterAppConstants;
import com.yahoo.mobileacademy.twitterclient.fragments.UserTimelineFragment;
import com.yahoo.mobileacademy.twitterclient.models.User;

public class ProfileActivity extends FragmentActivity {

	ImageView ivProfileIcon;
	TextView tvUserName;
	TextView tvUserDescription;
	TextView tvNbFollowers;
	TextView tvNbFollowing;
	FrameLayout flTimeline;
	
	private Fragment fragmentTimeline;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		setupView();
		loadFragment(); 
		
		Intent i = getIntent();
		Long userId = null;
		if (i.getExtras() != null && i.getExtras().containsKey(TwitterAppConstants.INTENT_EXTRA_PAREM_TWITTER_USER_ID)) {
			userId = i.getLongExtra(TwitterAppConstants.INTENT_EXTRA_PAREM_TWITTER_USER_ID, 0);
		}
		
		MyTwitterApp.getRestClient().getUserInfo(new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(JSONObject jsonUser) {
				User userDetails = User.fromJson(jsonUser);
				
				// Update current user view
				ImageLoader.getInstance().displayImage(userDetails.getProfileImageUrl(), ivProfileIcon);
				getActionBar().setTitle("@" + userDetails.getScreenName());
				tvUserName.setText(userDetails.getName());
				tvUserDescription.setText(userDetails.getProfileDescription());
				tvNbFollowers.setText(userDetails.getFollowersCount() + " " + getResources().getString(R.string.nb_followers_suffix));
				tvNbFollowing.setText(userDetails.getFriendsCount() + " " + getResources().getString(R.string.nb_following_suffix));
			}
			
			@Override
			public void onFailure(Throwable e, JSONObject error) {
			
				// Display an error message to the user
				Toast.makeText(getBaseContext(), "Can't fetch user information because of the following error: " + e.toString(), Toast.LENGTH_SHORT).show();
				
				Log.e("ERROR", e.toString());
			}

		}, userId);
		
	}

	private void loadFragment() {
		FragmentTransaction sft = getSupportFragmentManager().beginTransaction();
		// Check if the fragment is already initialized
		if (fragmentTimeline == null) {
			// If not, instantiate and add it to the activity
			fragmentTimeline = Fragment.instantiate(this, UserTimelineFragment.class.getName());
			sft.add(R.id.fragment_content, fragmentTimeline, "main");
		} else {
			// If it exists, simply attach it in order to show it
			sft.attach(fragmentTimeline);
		}
		sft.commit();
	}
	
	private void setupView() {
		ivProfileIcon = (ImageView) findViewById(R.id.iv_profile_icon);
		tvUserName = (TextView) findViewById(R.id.tv_userName);
		tvUserDescription = (TextView) findViewById(R.id.tv_userDescription);
		tvNbFollowers = (TextView) findViewById(R.id.tv_nbFollowers);
		tvNbFollowing = (TextView) findViewById(R.id.tv_nbFollowing);
		flTimeline = (FrameLayout) findViewById(R.id.fragment_content);
	}

}
