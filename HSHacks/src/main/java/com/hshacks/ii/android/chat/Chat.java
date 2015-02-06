package com.hshacks.ii.android.chat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chat {
    private String text;
    private String name;
    private String photo;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Chat() { }

    Chat(String message, String author, String imageUrl) {
        this.text = message;
        this.name = author;
        this.photo = imageUrl;
    }

    public String getText() {
        return text;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    // determines whether this instance is posted by Dave Fontenot or one of his disciples
    public boolean heKnows() {
        Pattern p = Pattern.compile("(?i)HEL+ *YEA");
        Matcher m = p.matcher(text);
        return m.find();
    }
}
