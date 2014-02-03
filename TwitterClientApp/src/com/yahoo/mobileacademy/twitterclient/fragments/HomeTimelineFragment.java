package com.yahoo.mobileacademy.twitterclient.fragments;

import java.util.List;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.yahoo.mobileacademy.twitterclient.apps.MyTwitterApp;
import com.yahoo.mobileacademy.twitterclient.constants.TwitterAppConstants;
import com.yahoo.mobileacademy.twitterclient.models.Tweet;
import com.yahoo.mobileacademy.twitterclient.models.User;

/**
 * Fragment to implement the home timeline 
 * 
 * @author CŽdric Lignier <cedric.lignier@free.fr>
 *
 */
public class HomeTimelineFragment extends AbstractTimelineFragment {
	
	/**
	 * Method to refresh an existing timeline
	 * by fetching content from the user home timeline
	 * 
	 * @param max_id if =/= 0, will represents the maximum tweet 
	 * id to add to the timeline
	 */
	@Override
	public void refreshTimeline(final long max_id, final long since_id) {

		MyTwitterApp.getRestClient().getHomeTimeline(
				jsonHttpResponseHandlerForTimeline(since_id), max_id, since_id);
		
	}
	
	@Override
	public void loadTimelineFromLocalDb() {
		List<Tweet> tweets = new Select().from(Tweet.class).orderBy("Tid DESC").limit(TwitterAppConstants.NB_TWEET_TO_STORE_ON_LOCAL_DB).execute();
		updateTimelineWithTweets(tweets, true);
	}
	
	/**
	 * Store the last n Tweet and store them in the local
	 * database. All previous entry of the database will be removed
	 * 
	 * @param i the number of Tweet to save in the database
	 */
	@Override
	public void updateLocalDBWithLatestItemFromListAdapter(int nbTweetToSave) {
		List<Tweet> tweets = getTweetsFromListAdapter();

		// Make sure we have at least i item in the list
		// of tweets to save
		int max = nbTweetToSave;
		if (tweets.size() < nbTweetToSave) {
			max = tweets.size();
		}
		List<Tweet> subList = tweets.subList(0, max);

		// Clean up Database
		Object r1 = new Delete().from(Tweet.class).execute();
		Object r2 = new Delete().from(User.class).execute();

		// Update Database
		for (Tweet t: subList) {
			t.getUser().save();
			t.save(); 
		}

	}


}
