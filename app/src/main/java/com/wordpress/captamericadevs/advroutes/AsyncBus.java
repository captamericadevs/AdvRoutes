package com.wordpress.captamericadevs.advroutes;

import com.squareup.otto.Bus;

/**
 * Created by Parker on 6/6/2016.
 */
public class AsyncBus {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }
}
