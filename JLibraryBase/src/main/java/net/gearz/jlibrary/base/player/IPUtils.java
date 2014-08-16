package net.gearz.jlibrary.base.player;

import lombok.Data;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by George on 24/12/13.
 */
public class IPUtils {

    /**
     * The default ping - timeout time
     */
    private static Float DEFAULT_PING = 9999f;

    /**
     * Returns player ping via the eventHandler passed in
     * Standard timeout of 9999 milliseconds ~ 9.999 seconds
     *
     * @param ip ~ Player to ping
     */
    public static void getPing(final InetAddress ip, final PingCallback callback) {
        new Thread(new PingGetter(ip, callback));
    }

    /**
     * A callback interface when the request to get an ips ping comes back
     */
    public interface PingCallback {
        public void onPingCallback(PingCallbackEvent e);
    }

    /**
     * A Data class containing the ping
     */
    @Data
    public static class PingCallbackEvent {

        final Float ping;

    }

    /**
     * A runnable to be run in a different thread as it stops the thread for up to 9.999 seconds to get ping
     */
    @Data
    public static class PingGetter implements Runnable {

        final InetAddress ip;

        final PingCallback callback;

        public void run() {
            float oldTime = System.currentTimeMillis();

            //give up trying to reach it at 9.999 seconds
            try {
                //noinspection ResultOfMethodCallIgnored
                ip.isReachable(DEFAULT_PING.intValue());
            } catch (IOException e) {
                // If not reachable fail silently and return the default ping
                callback.onPingCallback(new PingCallbackEvent(DEFAULT_PING));
                return;
            }

            float newTime = System.currentTimeMillis();

            //time it took in miliseconds
            float totalTime = newTime - oldTime;

            callback.onPingCallback(new PingCallbackEvent(totalTime));
        }
    }
}
