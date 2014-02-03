package com.yahoo.mobileacademy.twitterclient.fragments;

import com.yahoo.mobileacademy.twitterclient.apps.MyTwitterApp;

/**
 * Fragment to implement the user's Mentions timeline
 * 
 * @author CŽdric Lignier <cedric.lignier@free.fr>
 *
 */
public class MentionsTimelineFragment extends AbstractTimelineFragment {
	
	/**
	 * Method to refresh an existing timeline
	 * by fetching content from the user mentions timeline
	 * 
	 * @param max_id if =/= 0, will represents the maximum tweet 
	 * id to add to the timeline
	 */
    @Override
	public void refreshTimeline(final long max_id, final long since_id) {

		MyTwitterApp.getRestClient().getMentionsTimeline(
				jsonHttpResponseHandlerForTimeline(since_id), max_id, since_id);
		
	}

	@Override
	public void loadTimelineFromLocalDb() {
		// To be implemented :-)
	}

	@Override
	public void updateLocalDBWithLatestItemFromListAdapter(int nbItemToSave) {
		// To be implemented :-)
		
	}


}
