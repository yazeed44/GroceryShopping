package net.yazeed44.groceryshopping.utils;

import android.util.Log;

/**
 * Created by yazeed44 on 1/6/15.
 */
public final class ThreadUtil {

    private ThreadUtil() {
        throw new AssertionError();
    }


    public static void join(final Thread thread) {
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.e(thread.getName() + "  join", e.getMessage());
            e.printStackTrace();
        }
    }

    public static void startAndJoin(final Thread thread) {
        thread.start();
        join(thread);
        Log.d("startAndJoin", thread.getName() + "  has done it's work !");
    }


}
