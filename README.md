Twitter Client
==============

*Yahoo, Introduction to Android - Assignment Week 3: Twitter Client*

Assignment #1

User stories implemented
------------------------
 - User can sign in using OAuth login flow
 - User can view last 25 tweets from their home timeline
 - User can compose a new tweet
 - User can click a “Compose” icon in the Action Bar on the top right
 - User will have a Compose view opened
 - User can enter a message and hit a button to Post
 - User should be taken back to home timeline with new tweet visible
 - User can open the twitter app offline and see recent tweets
 - Tweets are persisted into sqlite and displayed from the local DB
 - User can load more tweets once they reach the bottom

Additional work done
--------------------
 - Better code encapsulation than in previous assignments
 - When a tweet is composed and the timeline is shown again, refresh the timeline to include any new recent tweets
 - Add the Twitter handle of the user in the action bar of the Timeline activity
 - Update behavior of the app by not performing network related operation when network connectivity is not available to better usability

Assignment #2

User stories implemented
------------------------
 - Includes all user stories from Assignment #1 (see above)
 - User can switch between Timeline and Mention views using tabs.
 - User can view their home timeline tweets.
 - User can view the recent mentions of their username.
 - User can click icon on Action Bar to view their profile
 - User can see picture, tagline, # of followers, # of following, and tweets on their profile.
 - User can click on the profile image in a tweet to see that user's profile.

Additional work done
--------------------
 - Creation of color.xml resources to centralize management of color within the application
 - Create a custom twitter icon for the app
 - Preserve the content of the "timeline" fragment upon tab switching by prevent the fragment to get recreated
 - When composing a Tweet, the home timeline will get updated with the tweet that has been added, even if it's not in focus 