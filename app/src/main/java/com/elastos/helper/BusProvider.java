package com.elastos.helper;

import com.squareup.otto.Bus;

/**
 * @author rczhang on 2018/05/10.
 */
public class BusProvider {
    private static final Bus sBus = new MainThreadBus();

    public static Bus getInstance() {
        return sBus;
    }

    private BusProvider() {

    }
}
