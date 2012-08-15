package com.schnee.tweetgeister.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.lang.StringEscapeUtils;

import twitter4j.Tweet;

import com.schnee.tweetgeister.data.TGTweet;
import com.schnee.tweetgeister.data.TweetCSV;

public class Csv {
    
    //Tue 16 Mar 2010 20:44:13 +0000
    static private DateFormat dfm = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss Z");
    
    //Thu Oct 28 15:03:33 +0000 2010
    static private DateFormat altDfm = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy");

    public static TreeSet<CharSequence> parse(String fn) throws Exception{
        

        TreeSet<CharSequence> is = new TreeSet<CharSequence>();

        File file = new File(fn);

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        //skip the first line
        String theLine = null;
        theLine = br.readLine();

        // String[][] data = csvParser.getAllValues();

        // line[0]=text
        // line[1]=to_user_id
        // line[2]=from_user
        // line[3]=id
        // line[4]=from_user_id
        // line[5]=iso_language
        // line[12]=created_at

        while ((theLine = br.readLine()) != null) {
            if (theLine.startsWith("\"")) {
                theLine = theLine.substring(1);
            }

            CSVParser parser = new CSVParser(new StringReader(theLine));
            String[] line = parser.getLine();
            long timecode = Long.parseLong(line[12]);
            
            try {
                Date d2 = dfm.parse(line[11]);
                System.out.println(d2);
                
                timecode = d2.getTime();
                
            } catch (ParseException e) {
                Date d2 = altDfm.parse(line[11]);
                timecode = d2.getTime();
            }
            
            Date date = new Date(timecode);

            final String fromUser = line[2];
            final int fromUserId = Integer.parseInt(line[4]);
            final long id = Long.parseLong(line[3]);
            final String isoLanguageCode = line[5];
            String text = line[0];

            text = StringEscapeUtils.unescapeHtml(text);

            int toUserId = 0;
            if (line[1].length() > 0) {
                toUserId = Integer.parseInt(line[1]);
            }
            Tweet tweet = new TweetCSV(date, fromUser, fromUserId, id, isoLanguageCode, text, toUserId);

            TGTweet tgt = new TGTweet(tweet);
        
            is.add(tgt);

        }
        
        return is;

    }
    
}
