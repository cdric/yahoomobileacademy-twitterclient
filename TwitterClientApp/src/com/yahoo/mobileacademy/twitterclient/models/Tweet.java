package com.yahoo.mobileacademy.twitterclient.models;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Tweets")
public class Tweet extends Model implements Serializable {
	
	private static final long serialVersionUID = -3851023831373394079L;
	
	@Column(name = "body")
	private String body;
	
	@Column(name = "tId")
	private long tId;
	
	@Column(name = "favorited")
	private boolean favorited;
	
	@Column(name = "retweeted")
	private boolean retweeted;
	
	@Column(name = "user")
	private User user;

	public User getUser() {
		return user;
	}

	public String getBody() {
		return body;
	}

	public long getTweetId() {
		return tId;
	}

	public boolean isFavorited() {
		return favorited;
	}

	public boolean isRetweeted() {
		return retweeted;
	}

	public static Tweet fromJson(JSONObject jsonObject) {
		Tweet tweet = new Tweet();
		try {
			tweet.body = jsonObject.getString("text");
			tweet.tId = jsonObject.getLong("id");
			tweet.favorited = jsonObject.getBoolean("favorited");
			tweet.retweeted = jsonObject.getBoolean("retweeted");
			tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return tweet;
	}

	public static ArrayList<Tweet> fromJson(JSONArray jsonArray) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject tweetJson = null;
			try {
				tweetJson = jsonArray.getJSONObject(i);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}

			Tweet tweet = Tweet.fromJson(tweetJson);
			if (tweet != null) {
				tweets.add(tweet);
			}
		}

		return tweets;
	}
}
