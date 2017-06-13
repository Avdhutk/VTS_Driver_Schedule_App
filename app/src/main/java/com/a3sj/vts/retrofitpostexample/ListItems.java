package com.a3sj.vts.retrofitpostexample;

/**
 * Created by Avdhut K on 11-03-2017.
 */

public class ListItems {
    String route;
    String busno;
    String time;

    ListItems(String route, String busno, String time){
        this.route=route;
        this.busno=busno;
        this.time=time;
    }

    public void setBusno(String busno) {
        this.busno = busno;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBusno() {
        return busno;
    }

    public String getRoute() {
        return route;
    }

    public String getTime() {
        return time;
    }
}
