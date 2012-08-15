package com.schnee.tweetgeister.util;

import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    static Pattern twitPattern = Pattern.compile("(\\^[A-Za-z0-9]+)");

    public static Set<String> extractUsers(String queryString) {

        Matcher matcher = twitPattern.matcher(queryString);

        Set<String> users = new TreeSet<String>();

        while (matcher.find()) {
            String user = matcher.group();
            user = user.substring(1, user.length());
            users.add(user);
        }

        return users;
    }

}
