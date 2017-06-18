package com.fredo.introtorxjava;

/**
 * Created by Fredo on 6/18/17.
 */
public class DiscoResponse {
    public Embedded _embedded;


    public class Embedded {
        public Events[] events;
    }

    public class Events {
        String name;
    }
}
