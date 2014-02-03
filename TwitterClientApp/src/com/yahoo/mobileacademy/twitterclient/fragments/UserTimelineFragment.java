package com.yahoo.mobileacademy.twitterclient.fragments;

import android.content.Intent;

import com.yahoo.mobileacademy.twitterclient.apps.MyTwitterApp;
import com.yahoo.mobileacademy.twitterclient.constants.TwitterAppConstants;

/**
 * Class the implement user own timeline
 * 
 * @author CŽdric Lignier <cedric.lignier@free.fr>
 *
 */
public class UserTimelineFragment extends AbstractTimelineFragment {
	
	/**
	 * Method to refresh an existing timeline
	 * by fetching content from the user profile timeline
	 * 
	 * @param max_id if =/= 0, will represents the maximum tweet 
	 * id to add to the timeline
	 */
    @Override
	public void refreshTimeline(final long max_id, final long since_id) {
    	
    	// Fetch the user_id from the intent calling this Fragment
    	// though the activity that loaded this fragment
    	Intent i = getActivity().getIntent();
    	Long user_id = null;
    	if (i.getExtras() != null && i.getExtras().containsKey(TwitterAppConstants.INTENT_EXTRA_PAREM_TWITTER_USER_ID)) {
    		user_id = i.getLongExtra(TwitterAppConstants.INTENT_EXTRA_PAREM_TWITTER_USER_ID, 0);
    	} 
    	
    	// Update the timeline
		MyTwitterApp.getRestClient().getUserTimeline(
				jsonHttpResponseHandlerForTimeline(since_id), max_id, since_id, user_id);
	}

	@Override
	public void loadTimelineFromLocalDb() {
		// To be implemented
		
	}

	@Override
	public void updateLocalDBWithLatestItemFromListAdapter(int nbItemToSave) {
		// To be implemented
		
	}

}
