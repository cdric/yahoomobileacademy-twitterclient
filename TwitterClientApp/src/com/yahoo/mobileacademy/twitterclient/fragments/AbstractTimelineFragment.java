package com.yahoo.mobileacademy.twitterclient.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yahoo.mobileacademy.twitterclient.R;
import com.yahoo.mobileacademy.twitterclient.adapters.TweetsAdapter;
import com.yahoo.mobileacademy.twitterclient.constants.TwitterAppConstants;
import com.yahoo.mobileacademy.twitterclient.helpers.TwitterAPIHelper;
import com.yahoo.mobileacademy.twitterclient.helpers.UtilityClass;
import com.yahoo.mobileacademy.twitterclient.listeners.EndlessScrollListener;
import com.yahoo.mobileacademy.twitterclient.models.Tweet;
import com.yahoo.mobileacademy.twitterclient.models.User;

/**
 * Generic class that implement a Fragment that display a ListView 
 * of tweets
 * 
 * @author CŽdric Lignier <cedric.lignier@free.fr>
 *
 */
public abstract class AbstractTimelineFragment extends Fragment {
	
	public ListView lvTweets;
	private View mView = null;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      
		if (mView == null) {
	       mView = inflater.inflate(R.layout.fragment_timeline, container, false); 
		} else {
		   // Avoid the view to be recreated upon tab switching!
		   // The following code is required to prevent a Runtime exception
		   ((ViewGroup) mView.getParent()).removeView(mView);
		}
		
        return mView;
      
    }
	
	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);
	    setUpFragment();
	    
	    // Update the view if it hasn't been already updated
	    List<Tweet> tweets = getTweetsFromListAdapter();
	    if (tweets.isEmpty()) {
	      
			// Check if the internet connection is up
		    if (!UtilityClass.isNetworkConnected(getActivity().getBaseContext())) {
		       loadTimelineFromLocalDb();		   
		    } else {
		       loadTimelineFromInternet();
		    }
	    
	    } 
		
	}

	private void setUpFragment() {		
		lvTweets = (ListView) getActivity().findViewById(R.id.lvTweets);
	}
	
	// ----------------------
	// INTENT RELATED METHODS
	// ----------------------

	protected void updateTimeline(int requestCode, int resultCode, Intent data) {
		
		// This need to move into the fragment
		
//	  if (resultCode == RESULT_OK) {
//		  
//		  // Coming back
//		  if (requestCode == TwitterAppConstants.REQUEST_COMPOSE_ACTIVITY) {
//			  
//			  // Always add the new Tweet on top of the timeline
//			  // EXTRA: Only if it does not already exist
//			  Tweet t = (Tweet) data.getExtras().getSerializable("tweet");
//			  
//			  if (!UtilityClass.isTweetInTimeline(tweetAdapter, t)) {
//				 List<Tweet> list = new ArrayList<Tweet>();
//				 list.add(t);
//				 updateTimelineWithTweets(list, true, true);	
//			  }	
//			  //addTweetToTopOfTimeline(t);
//			  
//			  // EXTRA: Load any additional Tweets that might have been added since then
//			  List<Tweet> tweets = getTweetsFromListAdapter();
//			  refreshTimeline(-1, TwitterAPIHelper.computeSinceIdFromTweets(tweets));
//		  
//		  }
//	     
//	  } 
//	  
//	  if (resultCode == RESULT_CANCELED) {
//		  
//		  // Nothing to do
//			
//	  }
	  
	} 

	/**
	 * Method to load the timeline from scratch
	 */
	public void loadTimelineFromInternet() {
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
	public List<Tweet> getTweetsFromListAdapter() {
		List<Tweet> tweets = UtilityClass.getTweetsFromAdapter((TweetsAdapter)lvTweets.getAdapter());
		return tweets;
	}

	/**
	 * Update the timeline by adding a list of tweets
	 *  - The method can add the tweet either at the top or at the bottom of the timeline
	 *  - The method will create the adapter is the timeline doesnt exist yet
	 * @param tweets the List of Tweets to add
	 * @param addAtTheTopOfTimeline if TRUE, tweets will be added at the tope of the timeline
	 */
	public void updateTimelineWithTweets(List<Tweet> tweets, boolean addAtTheTopOfTimeline) {
		
		TweetsAdapter adapter = (TweetsAdapter)lvTweets.getAdapter();
		
		if (adapter == null) {
			adapter = new TweetsAdapter(getActivity().getBaseContext(), tweets);
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

	}


	// -----------------------------
	// METHODS THAT CAN BE OVERIDDEN
	// -----------------------------

	/**
	 * Method to refresh an existing timeline
	 * to be implemented by the TwitterListViewFragment implementing this class
	 * 
	 * @param max_id if =/= 0, will represents the maximum tweet 
	 * id to add to the timeline
	 */
	public abstract void refreshTimeline(final long max_id, final long since_id);

	/**
	 * Load timeline from local DB
	 */
	public abstract void loadTimelineFromLocalDb();
	
	/**
	 * Method to be implemented in order to store
	 * latest Tweets from ListAdapater into the local database
	 * for persistence purposes
	 * 
	 * @param nbTweetToSave nb of item to save from the ListAdapter
	 */
	public abstract void updateLocalDBWithLatestItemFromListAdapter(int nbItemToSave);
	
	// ---------------------------
	// DEFAULT JsonResponseHandler
	// ---------------------------
	
	protected JsonHttpResponseHandler jsonHttpResponseHandlerForTimeline(final long since_id) {
		return new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(JSONArray jsonTweets) {

				ArrayList<Tweet> tweets = Tweet.fromJson(jsonTweets);
				
				updateTimelineWithTweets(tweets, (since_id != -1));
				updateLocalDBWithLatestItemFromListAdapter(TwitterAppConstants.NB_TWEET_TO_DISPLAY);

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
				Toast.makeText(getActivity().getBaseContext(), "Can't refresh the timeline due to the following error: " + e.toString(), Toast.LENGTH_SHORT).show();
				Log.e("ERROR", e.toString());
				e.printStackTrace();
			}

		};
	}
	
}
