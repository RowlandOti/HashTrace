# HashTrace
This is my final project for the Udacity - Developing Android Apps: Android Fundamentals . 

HasTrace is an android application that enables users to track down tweets with specific hashtags, 
from specific users and specific time span in a particular order. Users can query for the tweets 
with a particular #Hashtag by providing a value in the settings from which the search query is 
built and sent to the Twitter API for retrieval.

Other cool features of the application include:‐

*  Ability to favorite tweets – these are special tweets which will be stored in users SQLite 
database for future reference and retrieval on demand by the user.
*    Tweet notification – users can set preference to be notified when
the specific tweets with specific hashtags, and or from specific users at a postdated time span are 
detected. This will sync automatically if allowed.
*    Cool pull to refresh feature ‐ With sync service in the
background as seen in the screen shot.
*   AccountAuthentication and AccountSyncService ‐ for syncing in the background service.
*    Preference Settings – for custom user preferences
*    Cool user interface.
*    Graphs of tweet data – custom graph is available for the user of application to analytically 
view the tweet behavior over a particular time span. For example if the user sets
  *  No. of tweets of Hashtag vs. Date
  *  No. of tweets of Hashtag vs. No. of users engaged with
Hashtag etc.

Preview: 

![Alt text](https://github.com/RowlandOti/HashTrace/blob/master/documentation/mockup/gif/hashtrace.gif?raw=true "Hashtrace Preview")
