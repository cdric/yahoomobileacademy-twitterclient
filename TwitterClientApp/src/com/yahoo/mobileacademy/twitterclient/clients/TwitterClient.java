package com.yahoo.mobileacademy.twitterclient.clients;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change
																				// this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change
																			// this,
																			// base
																			// API
																			// URL
	public static final String REST_CONSUMER_KEY = "eOYaE8yYTjzUCEtuVeZg"; // Change
																			// this
	public static final String REST_CONSUMER_SECRET = "RATIeDUCOWDXwz1tvzsZ9eRCbjIZMzrETCcxxHjDSw"; // Change
																									// this
	public static final String REST_CALLBACK_URL = "oauth://mytwitterapp"; // Change
																			// this
																			// (here
																			// and
																			// in
																			// manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	/**
	 * Fetch the most recent Tweets and retweets posted by the authenticating user and the users
	 * they follow
	 * 
	 * @param handler
	 * @param max_id value for the "max_id" API parameter // -1 mean not setting the param
	 * @param since_id value for the "since_id" API parameter // -1 mean not setting the param
	 */
	public void getHomeTimeline(AsyncHttpResponseHandler handler, long max_id, long since_id) {
		String url = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", "25");
		getTimelineData(url, handler, max_id, since_id, params);
	}
	
	/**
	 * Fetch the most recent mentions (tweets containing the users' twitter handle)
	 * 
	 * @param handler
	 * @param max_id value for the "max_id" API parameter // -1 mean not setting the param
	 * @param since_id value for the "since_id" API parameter // -1 mean not setting the param
	 */
	public void getMentionsTimeline(AsyncHttpResponseHandler handler, long max_id, long since_id) {
		String url = getApiUrl("statuses/mentions_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", "25");
		getTimelineData(url, handler, max_id, since_id, params);
	}
	
	/**
	 * Fetch the most recent Tweets posted by the user
	 * 
	 * @param handler
	 * @param max_id value for the "max_id" API parameter // -1 mean not setting the param
	 * @param since_id value for the "since_id" API parameter // -1 mean not setting the param
	 */
	public void getUserTimeline(AsyncHttpResponseHandler handler, long max_id, long since_id, Long userId) {
		String url = getApiUrl("statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", "25");
		if (userId != null) {
			params.put("user_id", userId.toString());
		}
		
		getTimelineData(url, handler, max_id, since_id, params);
	}
	
	/**
	 * Fetch 25 timeline item for a given timeline (home, mentions, user)
	 * 
	 * @param url the API endpoint to fetch timeline informations from 
	 * @param handler 
	 * @param max_id value for the "max_id" API parameter // -1 mean not setting the param
	 * @param since_id value for the "since_id" API parameter // -1 mean not setting the param
	 */
	private void getTimelineData(String url, AsyncHttpResponseHandler handler, long max_id, long since_id, RequestParams params) {
		
		if (max_id != -1) {
			params.put("max_id", String.valueOf(max_id - 1));
		}
		if (since_id != -1) {
			params.put("since_id", String.valueOf(since_id));
		}
		
		Log.i("INFO", "getHomeTimeline: max_id=" + max_id + " / since_id=" + since_id );
		
		client.get(url, params, handler);
		
	}

	/**
	 * Fetch Tweet from user timeline
	 * 
	 * @param handler
	 * @param tweet
	 *            The status update to post
	 */
	public void postTweet(String statusUpdate, AsyncHttpResponseHandler handler) {
		String url = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", statusUpdate);
		client.post(url, params, handler); 
	}

	/**
	 * Fetch user information
	 * 
	 * @param handler 
	 * @param userId the id of the user to lookup information for. If userId is NULL
	 * we will return information for the authenticated user
	 * @parem screenName the screen name of the user to lookup information for. If screenName 
	 * is NULL, we will return information for the authenticated user
	 */
	public void getUserInfo(AsyncHttpResponseHandler handler, Long userId) {
		if (userId == null) {
			String url = getApiUrl("account/verify_credentials.json");
			client.get(url, null, handler);
		} else {
			String url = getApiUrl("users/show.json");
			RequestParams params = new RequestParams();
			params.put("user_id", userId.toString());
			//params.put("screen_name", userId.toString());
			client.get(url, params, handler);
		}
	}
}