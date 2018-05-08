
package com.elastos.helper;

import java.util.concurrent.Semaphore;

/**
 * Created by liteng on 2018/5/6.
 */

public class Synchronizer {
    Semaphore sema;

    public Synchronizer() {
        sema = new Semaphore(0);
    }

    public void wakeup() {
        sema.release();
    }

    public void await() {
        try {
            sema.acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
