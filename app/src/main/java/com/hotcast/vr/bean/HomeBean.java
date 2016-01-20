package com.hotcast.vr.bean;

import java.util.List;

/**
 * Created by lostnote on 15/12/4.
 */
public class HomeBean {
    List<HomeRoll> home_roll;
    List<HomeSubject> home_subject;

    public List<HomeSubject> getHome_subject() {
        return home_subject;
    }

    public void setHome_subject(List<HomeSubject> home_subject) {
        this.home_subject = home_subject;
    }

    public List<HomeRoll> getHome_roll() {
        return home_roll;
    }

    public void setHome_roll(List<HomeRoll> home_roll) {
        this.home_roll = home_roll;
    }

    @Override
    public String toString() {
        return "HomeBean{" +
                "home_roll=" + home_roll +
                ", home_subject=" + home_subject +
                '}';
    }
}
