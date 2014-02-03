package com.yahoo.mobileacademy.twitterclient.models;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Users")
public class User extends Model implements Serializable  {
	
	private static final long serialVersionUID = -6536421358029994641L;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "uId")
	private long uid;
	
	@Column(name = "screenName")
	private String screenName;
	
	@Column(name = "profileDescription")
	private String profileDescription;
	
	@Column(name = "profileBgImageUrl")
	private String profileBgImageUrl;
	
	@Column(name = "profileImageUrl")
	private String profileImageUrl;
	
	@Column(name = "numTweets")
	private int numTweets;
	
	@Column(name = "followersCount")
	private int followersCount;
	
	@Column(name = "friendsCount")
	private int friendsCount;

	public String getName() {
		return name;
	}

	public long getUserId() {
		return uid;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getProfileBackgroundImageUrl() {
		return profileBgImageUrl;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}
	
	public String getProfileDescription() {
		return profileDescription;
	}

	public int getNumTweets() {
		return numTweets;
	}

	public int getFollowersCount() {
		return followersCount;
	}

	public int getFriendsCount() {
		return friendsCount;
	}

	public static User fromJson(JSONObject json) {
		User u = new User();
		try {
			u.name = json.getString("name");
			u.uid = json.getLong("id");
			u.screenName = json.getString("screen_name");
			u.profileDescription = json.getString("description");
			u.profileBgImageUrl = json
					.getString("profile_background_image_url");
			u.profileImageUrl = json.getString("profile_image_url");
			u.numTweets = json.getInt("statuses_count");
			u.followersCount = json.getInt("followers_count");
			u.friendsCount = json.getInt("friends_count");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return u;
	}

}
