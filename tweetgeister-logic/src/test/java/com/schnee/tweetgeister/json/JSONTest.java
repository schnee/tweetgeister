package com.schnee.tweetgeister.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.schnee.tweetgeister.data.Node;
import com.schnee.tweetgeister.data.TGTweet;
import com.schnee.tweetgeister.data.Tree;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class JSONTest extends TestCase {

    public JSONTest() {
        super();
        // TODO Auto-generated constructor stub
    }

    public JSONTest(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(JSONTest.class);
    }

    public void testJSON() {

        boolean[] boolArray = new boolean[] {true, false, true};
        JSONArray jsonArray = JSONArray.fromObject(boolArray);
        System.out.println(jsonArray);

        JSONObject parent = new JSONObject();
        JSONObject child = new JSONObject();
        JSONObject c2 = new JSONObject();

        child.put("childvalue", "goo");
        c2.put("childvalue", "c2");
        parent.put("child", child);
        parent.put("c2", c2);

        //System.out.println(parent);
    }

    public void xtestJSONwithArrays() {
        TGTweet parent = new TGTweet(null, "parentUser", 1, 12345, "isoLanguageCode", "profileImageUrl", "source",
                "parentText", "toUser", 0);

        TGTweet child1 = new TGTweet(null, "child1User", 1, 67890, "isoLanguageCode", "profileImageUrl", "source",
                "child1Text", "toUser", 0);

        TGTweet child2 = new TGTweet(null, "child2User", 1, 112233, "isoLanguageCode", "profileImageUrl", "source",
                "child2Text", "toUser", 0);

        JSONObject c = new JSONObject();

        JSONArray ca = new JSONArray();

        ca.add(child1.getFromUser());
        ca.add(child1.getId());

        c.put(child1.getText(), ca);

        System.out.println(c);

    }

    public void xtestTreeToJSON() {

        Tree<CharSequence> t = feedTheTree();

        fillTree(t);
    }

    protected void fillTree(Tree<CharSequence> t) {
        Node<CharSequence> rootElement = t.getRootElement();
        JSONObject rootJSON = new JSONObject();

        JsonConfig jconfig = new JsonConfig();
        jconfig.setExcludes(new String[] {"createdAt", "id", "fromUserId", "geoLocation", "isoLanguageCode",
                "profileImageUrl", "source", "toUser", "toUserId", "numberOfChildren"});



        fillTree(rootElement, rootJSON, jconfig);
        
        System.out.println("Test tree");
        System.out.println(rootJSON.toString(3));
    }

    protected void fillTree(Node<CharSequence> rootElement, JSONObject rootJSON, JsonConfig jconfig) {
        List<Node<CharSequence>> children = rootElement.getChildren();

        for (Node<CharSequence> node : children) {

            List<Node<CharSequence>> children2 = node.getChildren();

            if (children2.size() > 0) {
                //dealing with a topic node
                CharSequence data = node.getData();
                if (data instanceof TGTweet) {
                    TGTweet topic = (TGTweet) data;
                    JSONObject topicObj = new JSONObject();
                    //pass the node and the topic Obj down to the next level
                    fillTree(node, topicObj, jconfig);
                    rootJSON.put(topic.getText(), topicObj);
                }
            } else {

                CharSequence data = node.getData();
                if (data instanceof TGTweet) {
                    TGTweet tweet = (TGTweet) data;
                    JSONObject current = JSONObject.fromObject(tweet, jconfig);

                    rootJSON.put(tweet.getId(), current);
                }
            }
        }
    }

    public void xtestJSONfromManualNodes() {
        TGTweet parent = new TGTweet(null, "parentUser", 1, 12345, "isoLanguageCode", "profileImageUrl", "source",
                "parentText", "toUser", 0);

        TGTweet child1 = new TGTweet(null, "child1User", 1, 67890, "isoLanguageCode", "profileImageUrl", "source",
                "child1Text", "toUser", 0);

        TGTweet child2 = new TGTweet(null, "child2User", 1, 112233, "isoLanguageCode", "profileImageUrl", "source",
                "child2Text", "toUser", 0);

        TGTweet childless = new TGTweet(null, "childless", 1234, 55667, null, null, null, "childless text", "toUser", 0);

        JsonConfig jconfig = new JsonConfig();
        jconfig.setExcludes(new String[] {"createdAt", "id", "fromUserId", "geoLocation", "isoLanguageCode",
                "profileImageUrl", "source", "toUser", "toUserId", "numberOfChildren"});

        JSONObject root = new JSONObject();

        JSONObject p = JSONObject.fromObject(parent.getId());
        JSONObject c1 = JSONObject.fromObject(child1);
        JSONObject c2 = JSONObject.fromObject(child2);

        Map<Long, JSONObject> theMap = new HashMap<Long, JSONObject>();

        theMap.put(child1.getId(), c1);
        theMap.put(child2.getId(), c2);

        p.putAll(theMap, jconfig);
        root.put(parent.getId(), p);
        root.put(childless.getId(), JSONObject.fromObject(childless, jconfig));
        System.out.println(root.toString(3));
    }

    public void xtestJSONfromTreeOfTGTweet() {

        TGTweet parent = new TGTweet(null, "parentUser", 1, 12345, "isoLanguageCode", "profileImageUrl", "source",
                "parentText", "toUser", 0);

        TGTweet child1 = new TGTweet(null, "child1User", 1, 67890, "isoLanguageCode", "profileImageUrl", "source",
                "child1Text", "toUser", 0);

        TGTweet child2 = new TGTweet(null, "child2User", 1, 112233, "isoLanguageCode", "profileImageUrl", "source",
                "child2Text", "toUser", 0);

        Tree<TGTweet> t = new Tree<TGTweet>();

        Node<TGTweet> p = new Node<TGTweet>(parent);

        t.setRootElement(p);

        Node<TGTweet> c1 = new Node<TGTweet>(child1);
        Node<TGTweet> c2 = new Node<TGTweet>(child2);

        p.addChild(c1);
        p.addChild(c2);

        JsonConfig jconfig = new JsonConfig();
        jconfig.setExcludes(new String[] {"createdAt", "fromUser", "fromUserId", "geoLocation", "isoLanguageCode",
                "profileImageUrl", "source", "toUser", "toUserId", "numberOfChildren"});

        jconfig.setJsonPropertyFilter(new PropertyFilter() {
            public boolean apply(Object source, String name, Object value) {
                if (name.equals("children")) {
                    if (value instanceof ArrayList) {
                        ArrayList al = (ArrayList) value;
                        if (al.isEmpty()) {
                            return true;
                        }
                    }
                }
                return false;

            }
        });

        JSONObject jt = JSONObject.fromObject(t, jconfig);

        System.out.println(jt.toString(3));

    }

    protected Tree<CharSequence> feedTheTree() {
        TGTweet parent = new TGTweet(null, "parentUser", 1, 12345, "isoLanguageCode", "profileImageUrl", "source",
                "parentText", "toUser", 0);

        TGTweet child1 = new TGTweet(null, "child1User", 1, 67890, "isoLanguageCode", "profileImageUrl", "source",
                "child1Text", "toUser", 0);

        TGTweet child2 = new TGTweet(null, "child2User", 1, 112233, "isoLanguageCode", "profileImageUrl", "source",
                "child2Text", "toUser", 0);

        TGTweet childless = new TGTweet(null, "childless", 1234, 55667, null, null, null, "childless text", "toUser", 0);

        Tree<CharSequence> t = new Tree<CharSequence>();

        Node<CharSequence> root = new Node<CharSequence>();
        t.setRootElement(root);

        Node<CharSequence> np = new Node<CharSequence>(parent);
        Node<CharSequence> nc = new Node<CharSequence>(childless);

        Node<CharSequence> nc1 = new Node<CharSequence>(child1);
        Node<CharSequence> nc2 = new Node<CharSequence>(child2);

        root.addChild(np);
        root.addChild(nc);

        np.addChild(nc1);
        np.addChild(nc2);
        return t;
    }

}
