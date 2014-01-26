package com.yahoo.mobileacademy.twitterclient.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yahoo.mobileacademy.twitterclient.R;
import com.yahoo.mobileacademy.twitterclient.adapters.TweetsAdapter;
import com.yahoo.mobileacademy.twitterclient.apps.MyTwitterApp;
import com.yahoo.mobileacademy.twitterclient.constants.TwitterAppConstants;
import com.yahoo.mobileacademy.twitterclient.helpers.TwitterAPIHelper;
import com.yahoo.mobileacademy.twitterclient.helpers.UtilityClass;
import com.yahoo.mobileacademy.twitterclient.listeners.EndlessScrollListener;
import com.yahoo.mobileacademy.twitterclient.models.Tweet;
import com.yahoo.mobileacademy.twitterclient.models.User;

public class TimelineActivity extends Activity {

	ListView lvTweets;
	int since_id; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline); 
		
		setUpView();
		loadTimelineFromLocalDb();
		loadTimelineFromInternet();
		
	}

	private void setUpView() {
		lvTweets = (ListView) findViewById(R.id.lvTweets);
		
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

		});
	}
	
	/**
	 * Load timeline from local DB
	 */
	private void loadTimelineFromLocalDb() {
		
		List<Tweet> tweets = new Select().from(Tweet.class).orderBy("Tid DESC").limit(TwitterAppConstants.NB_TWEET_TO_STORE_ON_LOCAL_DB).execute();
		updateTimelineWithTweets(tweets, true, false);
		
	}
	
	/**
	 * Method to load the timeline from scratch
	 */
	private void loadTimelineFromInternet() {
		List<Tweet> tweets = getTweetsFromListAdapter();
		refreshTimeline(TwitterAPIHelper.computeMaxIdFromTweets(tweets), -1);
	}

	
	// -----------------------------
	// UTILITY METHOD FOR THIS CLASS
	// -----------------------------
	
	/**
	 * Return the list of Tweet from the list view adapter
	 * @return
	 */
	private List<Tweet> getTweetsFromListAdapter() {
		TweetsAdapter adapter = (TweetsAdapter) lvTweets.getAdapter();
		List<Tweet> tweets = UtilityClass.getTweetsFromAdapter(adapter);
		return tweets;
	}
	
	/**
	 * Update the timeline by adding a list of tweets
	 *  - The method can add the tweet either at the top or at the bottom of the timeline
	 *  - The method will create the adapter is the timeline doesnt exist yet
	 * @param tweets the List of Tweets to add
	 * @param addAtTheTopOfTimeline if TRUE, tweets will be added at the tope of the timeline
	 */
	private void updateTimelineWithTweets(List<Tweet> tweets, boolean addAtTheTopOfTimeline, boolean saveToDb) {
		
        TweetsAdapter adapter = (TweetsAdapter) lvTweets.getAdapter();
		
		if (adapter == null) {
			adapter = new TweetsAdapter(getBaseContext(), tweets);
			lvTweets.setAdapter(adapter);
		} else {
		
			// Look if the timeline already contain the tweet that we 
		    // would like to add to this timeline
			if (addAtTheTopOfTimeline) {
				for (Tweet t: tweets) {
				   adapter.insert(t, 0);
				}
			} else {
				adapter.addAll(tweets);
			}
			
		}
		
		if (saveToDb) {
			// Persist last 25 tweets
			updateLocalDBWithLatestTweets(TwitterAppConstants.NB_TWEET_TO_DISPLAY);
		}
		
	}
	
	/**
	 * Retrieve the last n tweets and store them in the local
	 * database. All previous entry of the database will be removed
	 * 
	 * @param i the number of Tweet to save in the database
	 */
	private void updateLocalDBWithLatestTweets(int i) {
		List<Tweet> tweets = getTweetsFromListAdapter();
		
		// Make sure we have at least i item in the list
		// of tweets to save
		int max = i;
		if (tweets.size() < i) {
			max = tweets.size();
		}
		List<Tweet> subList = tweets.subList(0, max);
		
		// Clean up Database
		Object r1 = new Delete().from(Tweet.class).execute();
		Object r2 = new Delete().from(User.class).execute();
		
		//Toast.makeText(getBaseContext(), "Saving to DB: " + subList.size() + " items", Toast.LENGTH_SHORT).show();
		
		// Update Database
		for (Tweet t: subList) {
			t.getUser().save();
			t.save(); 
		}
		
	}

	/**
	 * Method to refresh an existing timeline
	 * 
	 * @param max_id if =/= 0, will represents the maximum tweet 
	 * id to add to the timeline
	 */
	private void refreshTimeline(final long max_id, final long since_id) {
		MyTwitterApp.getRestClient().getHomeTimeline(
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(JSONArray jsonTweets) {
						
						ArrayList<Tweet> tweets = Tweet.fromJson(jsonTweets);
						updateTimelineWithTweets(tweets, (since_id != -1), true);
						
						// Implement endless scrolling
						lvTweets.setOnScrollListener(new EndlessScrollListener() {
							@Override
							public void onLoadMore(int page, int totalItemsCount) {
								
								List<Tweet> tweets = getTweetsFromListAdapter();
								
								refreshTimeline(TwitterAPIHelper
										.computeMaxIdFromTweets(tweets), -1); 
							}
							
						});
					}
					
					@Override
					public void onFailure(Throwable e, JSONObject error) {
					
						// Display an error message to the user
						Toast.makeText(getBaseContext(), "Can't refresh the timeline due to the following error: " + e.toString(), Toast.LENGTH_SHORT).show();
						Log.e("ERROR", e.toString());
						e.printStackTrace();
					}

				}, max_id, since_id);
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
		
		Toast.makeText(getBaseContext(), "Refreshng timeline...",
				Toast.LENGTH_SHORT).show();
		
		// If the adapter is already setup for this view
		// Let's clear it before refreshing the view
		if (lvTweets.getAdapter() != null) {
			TweetsAdapter adapter = (TweetsAdapter) lvTweets.getAdapter();
			adapter.clear();
		}
		
		loadTimelineFromInternet();

	}

	/**
	 * Method invoked when user click on the "compose" icon from the action bar
	 * @param mi the MenuItem
	 */
	public void onComposeAction(MenuItem mi) {

		Intent i = new Intent(getApplicationContext(), ComposeActivity.class);
		startActivityForResult(i, TwitterAppConstants.REQUEST_COMPOSE_ACTIVITY);

	}
	
	// ----------------------
	// INTENT RELATED METHODS
	// ----------------------
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
	  if (resultCode == RESULT_OK) {
		  
		  // Coming back
		  if (requestCode == TwitterAppConstants.REQUEST_COMPOSE_ACTIVITY) {
			  
			  // Always add the new Tweet on top of the timeline
			  // EXTRA: Only if it does not already exist
			  Tweet t = (Tweet) data.getExtras().getSerializable("tweet");

			  TweetsAdapter adapter = (TweetsAdapter) lvTweets.getAdapter();
			  if (!UtilityClass.isTweetInTimeline(adapter, t)) {
				 List<Tweet> list = new ArrayList<Tweet>();
				 list.add(t);
				 updateTimelineWithTweets(list, true, true);	
			  }	
			  //addTweetToTopOfTimeline(t);
			  
			  // EXTRA: Load any additional Tweets that might have been added since then
			  List<Tweet> tweets = getTweetsFromListAdapter();
			  refreshTimeline(-1, TwitterAPIHelper.computeSinceIdFromTweets(tweets));
		  
		  }
	     
	  } 
	  
	  if (resultCode == RESULT_CANCELED) {
		  
		  // Nothing to do
			
	  }
	  
	} 

}
