package com.yahoo.mobileacademy.twitterclient.helpers;

import java.util.List;

import android.util.Log;

import com.yahoo.mobileacademy.twitterclient.models.Tweet;

/**
 * Class to help managing class to the Twitter API 1.1
 * 
 * @author CŽdric Lignier <cedric.lignier@free.fr>
 *
 */
public class TwitterAPIHelper {

	/**
	 * Return the max_id from a list of tweet that should be used
	 * to determine the next set of tweets to return from the user
	 * timeine.
	 * 
	 * For more info: 
	 * https://dev.twitter.com/docs/working-with-timelines
	 * 
	 * @param tweets List of Tweets
	 * @return the value if it has been found, -1 otherwise
	 */
	public static long computeMaxIdFromTweets(List<Tweet> tweets) {

		long max_id = -1; // null value
		
		if (!tweets.isEmpty()) {
			max_id = tweets.get(0).getTweetId();
			for (Tweet t : tweets) {
				if (t.getTweetId() < max_id) {
					max_id = t.getTweetId();
				}
			}
		}
		
		return max_id;
	}
	
	/**
	 * Return the since_id from a list of tweet that should be used
	 * to determine the next set of tweets to return from the user
	 * timeine.
	 * 
	 * For more info: 
	 * https://dev.twitter.com/docs/working-with-timelines
	 * 
	 * @param tweets List of Tweets
	 * @return the value if it has been found, -1 otherwise
	 */
	public static long computeSinceIdFromTweets(List<Tweet> tweets) {

		long since_id = -1; // null value
		
		if (!tweets.isEmpty()) {
			for (Tweet t : tweets) {
				if (t.getTweetId() > since_id) {
					since_id = t.getTweetId();
				}
			}
		} 

		return since_id;
	}
	
}
