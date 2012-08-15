package com.schnee.tweetgeister.data;

import java.util.Date;

import twitter4j.Annotations;
import twitter4j.GeoLocation;
import twitter4j.Tweet;

public class TweetCSV implements Tweet {
	
	private Date createdAt;
	private String fromUser;
	private int fromUserId;
	private long id;
	//private GeoLocation geoLocation;
	private String isoLanguageCode;
	private String profileImageUrl;
	private String source;
	private String text;
	private String toUser;
	private int toUserId;
	
	
	
	public TweetCSV(Date createdAt, String fromUser, int fromUserId, long id,
			String isoLanguageCode, String text, int toUserId) {
		super();
		this.createdAt = createdAt;
		this.fromUser = fromUser;
		this.fromUserId = fromUserId;
		this.id = id;
		this.isoLanguageCode = isoLanguageCode;
		this.text = text;
		this.toUserId = toUserId;
	}

	public Date getCreatedAt() {
		// TODO Auto-generated method stub
		return createdAt;
	}

	public String getFromUser() {
		// TODO Auto-generated method stub
		return fromUser;
	}

	public int getFromUserId() {
		// TODO Auto-generated method stub
		return fromUserId;
	}

	public GeoLocation getGeoLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getId() {
		// TODO Auto-generated method stub
		return id;
	}

	public String getIsoLanguageCode() {
		// TODO Auto-generated method stub
		return isoLanguageCode;
	}

	public String getProfileImageUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSource() {
		// TODO Auto-generated method stub
		return source;
	}

	public String getText() {
		// TODO Auto-generated method stub
		return text;
	}

	public String getToUser() {
		// TODO Auto-generated method stub
		return toUser;
	}

	public int getToUserId() {
		// TODO Auto-generated method stub
		return 0;
	}

    @Override
    public int compareTo(Tweet o) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Annotations getAnnotations() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getLocation() {
        // TODO Auto-generated method stub
        return null;
    }

}
