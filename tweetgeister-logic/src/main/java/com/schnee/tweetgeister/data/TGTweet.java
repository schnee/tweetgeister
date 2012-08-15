package com.schnee.tweetgeister.data;

import java.util.Date;

import twitter4j.Annotations;
import twitter4j.GeoLocation;
import twitter4j.Tweet;

public class TGTweet implements Tweet, CharSequence {

    /**
     * Default
     */
    private static final long serialVersionUID = 5864879923109447926L;

    private Date createdAt;

    private String fromUser;

    private int fromUserId;

    private long id;

    private GeoLocation geoLocation;

    private String isoLanguageCode;

    private String profileImageUrl;

    private String source;

    private String text;

    private String toUser;

    private int toUserId;

    private String synthCharSequence;
    
    private int age;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public TGTweet(Tweet tweet) {
        this.createdAt = tweet.getCreatedAt();
        this.fromUser = tweet.getFromUser();
        this.fromUserId = tweet.getFromUserId();
        this.id = tweet.getId();
        this.isoLanguageCode = tweet.getIsoLanguageCode();
        this.profileImageUrl = tweet.getProfileImageUrl();
        this.source = tweet.getSource();
        this.text = tweet.getText();
        this.toUser = tweet.getToUser();
        this.toUserId = tweet.getToUserId();
        this.geoLocation = tweet.getGeoLocation();
        genSynthCharSequence();
    }

    public TGTweet(Date createdAt, String fromUser, int fromUserId, long id, String isoLanguageCode, String profileImageUrl,
            String source, String text, String toUser, int toUserId) {
        super();
        this.createdAt = createdAt;
        this.fromUser = fromUser;
        this.fromUserId = fromUserId;
        this.id = id;
        this.isoLanguageCode = isoLanguageCode;
        this.profileImageUrl = profileImageUrl;
        this.source = source;
        this.text = text;
        this.toUser = toUser;
        this.toUserId = toUserId;
        this.geoLocation = null;
        genSynthCharSequence();
    }

    private void genSynthCharSequence() {
        StringBuilder sb = new StringBuilder(text);
        sb.append(" ");
        //        sb.append(Long.toString(id));
        //        sb.append(" ");
        sb.append(fromUserId);
        synthCharSequence = sb.toString();
    }

    public int length() {
        return synthCharSequence.length();
    }

    public char charAt(int index) {
        return synthCharSequence.charAt(index);
    }

    public CharSequence subSequence(int start, int end) {
        return synthCharSequence.subSequence(start, end);
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getFromUser() {
        return fromUser;
    }

    public int getFromUserId() {
        return fromUserId;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public long getId() {
        return id;
    }

    public String getIsoLanguageCode() {
        return isoLanguageCode;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getSource() {
        return source;
    }

    public String getText() {
        return text;
    }

    public String getToUser() {
        return toUser;
    }

    public int getToUserId() {
        return toUserId;
    }

    public int compareTo(TGTweet o) {

        int retval = 0;
        if (id != o.getId()) { //if Twitter says they have the same ID...
            retval = createdAt.compareTo(o.getCreatedAt());
            if (retval == 0) { //dates are equal
                //look at the text
                retval = text.compareTo(o.getText());
                if (retval == 0) {//texts are equal
                    //look at the userId
                    retval = fromUser.compareTo(o.getFromUser());
                }
            }
        }
        return retval;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
        result = prime * result + fromUserId;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TGTweet other = (TGTweet) obj;
        if (createdAt == null) {
            if (other.createdAt != null)
                return false;
        } else if (!createdAt.equals(other.createdAt))
            return false;
        if (fromUserId != other.fromUserId)
            return false;
        if (id != other.id)
            return false;
        if (text == null) {
            if (other.text != null)
                return false;
        } else if (!text.equals(other.text))
            return false;
        return true;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return text;
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

    @Override
    public int compareTo(Tweet o) {
        int retval = 0;
        if (id != o.getId()) { //if Twitter says they have the same ID...
            retval = createdAt.compareTo(o.getCreatedAt());
            if (retval == 0) { //dates are equal
                //look at the text
                retval = text.compareTo(o.getText());
                if (retval == 0) {//texts are equal
                    //look at the userId
                    retval = fromUser.compareTo(o.getFromUser());
                }
            }
        }
        return retval;
    }
}
