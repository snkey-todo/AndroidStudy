package com.hxsj.noticedemo.widget.tagview;
import java.util.Map;

/**
 * Tag Class
 */
public class Tag {

    private int id;
    private String text;
    private Map<?,?> attrs;

    public Tag(int id, String text){
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<?, ?> getAttrs() {
        return attrs;
    }

    public void setAttrs(Map<?, ?> attrs) {
        this.attrs = attrs;
    }
}