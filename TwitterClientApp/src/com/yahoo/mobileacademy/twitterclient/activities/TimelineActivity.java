package com.yahoo.mobileacademy.twitterclient.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.yahoo.mobileacademy.twitterclient.R;
import com.yahoo.mobileacademy.twitterclient.adapters.TweetsAdapter;
import com.yahoo.mobileacademy.twitterclient.apps.MyTwitterApp;
import com.yahoo.mobileacademy.twitterclient.constants.TwitterAppConstants;
import com.yahoo.mobileacademy.twitterclient.fragments.AbstractTimelineFragment;
import com.yahoo.mobileacademy.twitterclient.fragments.HomeTimelineFragment;
import com.yahoo.mobileacademy.twitterclient.fragments.MentionsTimelineFragment;
import com.yahoo.mobileacademy.twitterclient.helpers.TwitterAPIHelper;
import com.yahoo.mobileacademy.twitterclient.helpers.UtilityClass;
import com.yahoo.mobileacademy.twitterclient.listeners.FragmentTabListener;
import com.yahoo.mobileacademy.twitterclient.models.Tweet;
import com.yahoo.mobileacademy.twitterclient.models.User;

public class TimelineActivity extends FragmentActivity {

	private Tab tabHome, tabMentions;
	private String tabHomeTag, tabMentionsTag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home); 
		
		setUpActivity();
	}

	private void setUpActivity() {
		
		// EXTRA: Update Action Bar to inlcude userName twitter handle
		MyTwitterApp.getRestClient().getUserInfo(new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(JSONObject jsonUser) {
				User userDetails = User.fromJson(jsonUser);
				
				//Setup actionBar title
				getActionBar().setTitle("@" + userDetails.getScreenName());
			}
			
			@Override
			public void onFailure(Throwable e, JSONObject error) {
			
				// Display an error message to the user
				Toast.makeText(getBaseContext(), "Can't fetch user information due to the following error: " + e.toString(), Toast.LENGTH_SHORT).show();
				Log.e("ERROR", e.toString());
				e.printStackTrace();
			}

		}, null);
		
		//Set up Tab navigation in Action Bar
		ActionBar actionBar = getActionBar(); 
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS); 
		tabHomeTag = getResources().getString(R.string.tabHome);
		tabHome = actionBar.newTab().setText(R.string.tabHome).setTabListener(
				new FragmentTabListener(this, tabHomeTag, HomeTimelineFragment.class)).setIcon(R.drawable.ic_action_home).setTag(tabHomeTag);
		actionBar.addTab(tabHome);
		tabMentionsTag = getResources().getString(R.string.tabMentions);
		tabMentions = actionBar.newTab().setText(R.string.tabMentions).setTabListener(
				new FragmentTabListener(this, tabMentionsTag, MentionsTimelineFragment.class)).setIcon(R.drawable.ic_action_mentions).setTag(tabMentionsTag);
		actionBar.addTab(tabMentions);
		
	}


	


	
	
	
	// ------------------
	// ACTION BAR ACTIONS
	// ------------------

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.timeline, menu);
		return true;
	}

	/**
	 * Method invoked when user click on the "refresh" icon from the action bar
	 * @param mi the MenuItem
	 */
	public void onRefreshAction(MenuItem mi) {
		
		if (!UtilityClass.isNetworkConnected(getBaseContext())) {
			
			Toast.makeText(getBaseContext(), R.string.timeline_refresh_action_impossible,
					Toast.LENGTH_SHORT).show();
			
		} else {
		
			Toast.makeText(getBaseContext(), R.string.timeline_refresh,
					Toast.LENGTH_SHORT).show();
			
			// Need to identify which fragment to refresh			
			AbstractTimelineFragment fragment = null;
			if (getActionBar().getSelectedTab().getTag().equals(tabHomeTag) ) {
				// HOME TAB
				fragment = (AbstractTimelineFragment) getSupportFragmentManager().findFragmentByTag(tabHomeTag);
			}
			
			if (getActionBar().getSelectedTab().getTag().equals(tabMentionsTag) ) {
				// MENTION TAB				
				fragment = (AbstractTimelineFragment) getSupportFragmentManager().findFragmentByTag(tabMentionsTag);
			}
			
					
			// If the adapter is already setup for this view
			// Let's clear it before refreshing the view
			ListView lvTweets = fragment.lvTweets;
			if (lvTweets.getAdapter() != null) {
				TweetsAdapter adapter = (TweetsAdapter) lvTweets.getAdapter();
				adapter.clear();
			}
			
			fragment.loadTimelineFromInternet();
			
		}

	}

	/**
	 * Method invoked when user click on the "compose" icon from the action bar
	 * @param mi the MenuItem
	 */
	public void onComposeAction(MenuItem mi) {
		
		if (!UtilityClass.isNetworkConnected(getBaseContext())) {
			
			Toast.makeText(getBaseContext(), R.string.compose_action_impossible,
					Toast.LENGTH_SHORT).show();
			
		} else {

			Intent i = new Intent(getApplicationContext(), ComposeActivity.class);
			startActivityForResult(i, TwitterAppConstants.REQUEST_COMPOSE_ACTIVITY);
			
		}

	}
	
	/**
	 * Method invoked when user click on the "compose" icon from the action bar
	 * @param mi the MenuItem
	 */
	public void onProfileAction(MenuItem mi) {
		
		if (!UtilityClass.isNetworkConnected(getBaseContext())) {
			
			Toast.makeText(getBaseContext(), R.string.profile_action_impossible,
					Toast.LENGTH_SHORT).show();
			
		} else {

			Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
			startActivity(i);
			
		}

	}
	
	// ----------------------
	// INTENT RELATED METHODS
	// ----------------------
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		// This need to move into the fragment
		
	  if (resultCode == RESULT_OK) {
		  
		  // Coming back from Compose activity
		  if (requestCode == TwitterAppConstants.REQUEST_COMPOSE_ACTIVITY) {
			  
			  // Only refresh the home view
			  // Refresh even if the view is not the current one
			  AbstractTimelineFragment fragment = null;
			  fragment = (AbstractTimelineFragment) getSupportFragmentManager().findFragmentByTag(tabHomeTag);
			  
			  // Always add the new Tweet on top of the timeline
			  // EXTRA: Only if it does not already exist
			  Tweet t = (Tweet) data.getExtras().getSerializable("tweet");

			  TweetsAdapter adapter = (TweetsAdapter) fragment.lvTweets.getAdapter();
			  if (!UtilityClass.isTweetInTimeline(adapter, t)) {
				 List<Tweet> list = new ArrayList<Tweet>();
				 list.add(t);
				 fragment.updateTimelineWithTweets(list, true);
				 fragment.updateLocalDBWithLatestItemFromListAdapter(TwitterAppConstants.NB_TWEET_TO_DISPLAY);
			  }	
			  //addTweetToTopOfTimeline(t);
			  
			  // EXTRA: Load any additional Tweets that might have been added since then
			  List<Tweet> tweets = fragment.getTweetsFromListAdapter();
			  fragment.refreshTimeline(-1, TwitterAPIHelper.computeSinceIdFromTweets(tweets));
		  
		  }
	     
	  } 
	  
	  if (resultCode == RESULT_CANCELED) {
		  
		  // Nothing to do
			
	  }
	  
	} 
	
	

}
