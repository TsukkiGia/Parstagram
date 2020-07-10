package com.example.parstagram;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@ParseClassName("Comment")
@Parcel(analyze={Comment.class})
public class Comment extends ParseObject {
    public static final String KEY_POST = "postID";
    public static final String KEY_USER = "user";
    public static final String KEY_TEXT = "text";
    
    public ParseObject getPostRelation() {
        return getParseObject(KEY_POST);
    }

    public void setPost(ParseObject post) {
        put(KEY_POST,post);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser) {
        put(KEY_USER,parseUser);
    }

    public String getText() {
        return getString(KEY_USER);
    }

    public void setText(String string) {
        put(KEY_TEXT,string);
    }
}
