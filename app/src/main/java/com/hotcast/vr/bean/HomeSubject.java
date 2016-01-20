package com.hotcast.vr.bean;

import java.util.List;

/**
 * Created by lostnote on 15/11/29.
 */
public class HomeSubject {
    Head head;
    List<HomeRoll> body;
    public Head getHead() {
        return head;
    }

    public void setHead(Head head) {
        this.head = head;
    }

    public List<HomeRoll> getBody() {
        return body;
    }

    public void setBody(List<HomeRoll> body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "HomeSubject{" +
                "head=" + head +
                ", body=" + body +
                '}';
    }
}
