package com.yahoo.mobileacademy.twitterclient.helpers;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.yahoo.mobileacademy.twitterclient.adapters.TweetsAdapter;
import com.yahoo.mobileacademy.twitterclient.models.Tweet;

/**
 * General UtilityClass for this application
 * 
 * @author CŽdric Lignier <cedric.lignier@free.fr>
 *
 */
public class UtilityClass {
	

	/**
	 * Check if the internet conneciton is available
	 * @return Returns TRUE if internet is available, FALSE otherwise
	 */
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager CManager =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo NInfo = CManager.getActiveNetworkInfo();
		if (NInfo != null && NInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Extract the list of tweets from a TweetsAdapter
	 * @param adapter the TweetsAdapter to process
	 * @return
	 */
	public static List<Tweet> getTweetsFromAdapter(TweetsAdapter adapter) {
		
		List<Tweet> list = new ArrayList<Tweet>();
		
		if (adapter != null) {
			for (int i = 0; i<adapter.getCount(); i++) {
				Tweet tweet = adapter.getItem(i);
				list.add(tweet);
			}
		}
		
		//Log.i("INFO", "getTweetsFromAdapter: Found: " + adapter.getCount() + " tweets");
		
		return list;
	}

	/**
	 * Indicate if a given Tweet belong the 
	 * a TweetAdapter
	 * 
	 * @param adapter The TweetAdapater to parse
	 * @param t the Tweet to compare to
	 * @return TRUE if the Tweet exist, FALSE otherwise
	 */
	public static boolean isTweetInTimeline(TweetsAdapter adapter, Tweet t) {
		
		List<Tweet> tweets = getTweetsFromAdapter(adapter);
		
		for (Tweet tweet : tweets) {
			if (tweet.getBody().equals(t.getBody())
			&& tweet.getUser().getId() == t.getUser().getId()) {
				return true;
			}
			
		}
		
		return false;
		
	}

}
