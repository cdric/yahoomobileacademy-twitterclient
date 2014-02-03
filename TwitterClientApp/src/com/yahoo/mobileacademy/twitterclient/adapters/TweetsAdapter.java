package com.yahoo.mobileacademy.twitterclient.adapters;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yahoo.mobileacademy.twitterclient.R;
import com.yahoo.mobileacademy.twitterclient.activities.ProfileActivity;
import com.yahoo.mobileacademy.twitterclient.constants.TwitterAppConstants;
import com.yahoo.mobileacademy.twitterclient.helpers.UtilityClass;
import com.yahoo.mobileacademy.twitterclient.models.Tweet;

public class TweetsAdapter extends ArrayAdapter<Tweet> {
	
	public TweetsAdapter(Context context, List<Tweet> objects) {
		super(context, 0, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {

			LayoutInflater inflatter = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflatter.inflate(R.layout.tweet_item, null);

		}

		final Tweet tweet = getItem(position);

		TextView homeView = (TextView) view.findViewById(R.id.tvName);
		String formattedName = "<b>" + tweet.getUser().getName()
				+ "</b> <small><font color='#777777'>@"
				+ tweet.getUser().getScreenName() + "</font></small>";
		homeView.setText(Html.fromHtml(formattedName));

		TextView bodyView = (TextView) view.findViewById(R.id.tvBody);
		bodyView.setText(Html.fromHtml(tweet.getBody()));
		
		ImageView imageView = (ImageView) view.findViewById((R.id.ivProfile));
		ImageLoader.getInstance().displayImage(
				tweet.getUser().getProfileImageUrl(), imageView);
		
		// Storing the userId inside the imageView Tag property
		imageView.setTag(tweet.getUser().getUserId());
		
		// Attaching onClick Listener to load Twitter profile when
		// the user click on the image 
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				Long userId = (Long) view.getTag();
				
				if (!UtilityClass.isNetworkConnected(view.getContext())) {
					
					Toast.makeText(view.getContext(), R.string.profile_action_impossible,
							Toast.LENGTH_SHORT).show();
					
				} else {

					Intent i = new Intent(view.getContext(), ProfileActivity.class);
					i.putExtra(TwitterAppConstants.INTENT_EXTRA_PAREM_TWITTER_USER_ID, userId);
					view.getContext().startActivity(i);
					
				}
				
			}
		});

		return view;
	}

}
