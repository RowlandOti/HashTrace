package com.rowland.hashtrace.models;

import java.util.Date;

/**
 * Tweet object, a class of all Tweets
 *
 * Holds a Tweets atrributes and provides corresponding getters
 *
 * @author Rowland
 *
 */
public class Tweet {


	private long tweet_id;

	private String tweet_text;
	private Date tweet_text_date;
	private String user_name;
	private String user_image_url;
	private String user_cover_url;
	private String user_location;

	private String user_description;
	private int tweet_retweet_count;
	private int tweet_favourite_count;
	private int tweet_mentions_count;

	public Tweet(long tweetId, String tweetText, Date tweetTextDate, int tweet_retweet_count, int tweet_favourite_count, int tweet_mentions_count, String userName, String userImageUrl, String userLocation, String user_description, String user_cover_url)
	{
		this.tweet_id = tweetId;
		this.tweet_text = tweetText;
		this.tweet_text_date = tweetTextDate;
		this.tweet_retweet_count = tweet_retweet_count;
		this.tweet_favourite_count = tweet_favourite_count;
		this.tweet_mentions_count = tweet_mentions_count;
		this.user_name = userName;
		this.user_image_url = userImageUrl;
		this.user_cover_url = user_cover_url;
		this.user_location = userLocation;
		this.user_description = user_description;
	}

	public long getTweetId()
	{
		return tweet_id;
	}

	public String getTweetText()
	{
		return tweet_text;
	}

	public Date getTweetTextDate()
	{
		return tweet_text_date;
	}

	public int getTweetRetweetCount()
	{
		return tweet_retweet_count;
	}

	public int getTweetFavouriteCount()
	{
		return tweet_favourite_count;
	}

	public int getTweetMentionsCount()
	{
		return tweet_mentions_count;
	}

	public String getUserName()
	{
		return user_name;
	}

	public String getUserImageUrl()
	{
		return user_image_url;
	}

	public String getUserCoverUrl()
	{
		return user_cover_url;
	}

	public String getUserLocation()
	{
		return user_location;
	}

	public String getUserDescription() { return user_description; }
}