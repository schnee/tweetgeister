package com.schnee.tweetgeister.util;

import com.rosaloves.bitlyj.Url;
import static com.rosaloves.bitlyj.Bitly.*;

public class Bitly {

    private static String USER = "schnee";

    private static String API_KEY = "R_b8bc03873215d292c3143bc163bbfd45";

    public static String makeSmall(String longUrl) {

        Url url = as(USER, API_KEY).call(shorten(longUrl));

        return url.getShortUrl();
    }

}
