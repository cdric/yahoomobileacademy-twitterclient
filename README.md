Twitter Client
==============

*Yahoo, Introduction to Android - Assignment Week 3: Twitter Client*

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
 - Provide ability to add new tweets either at the top or the bottom of an existing timeline