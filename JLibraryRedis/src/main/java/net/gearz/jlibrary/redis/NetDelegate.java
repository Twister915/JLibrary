package net.gearz.jlibrary.redis;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * The NetDelegate is responsible for subscribing to the Jedis channel and dispatching
 * appropriate commands to the instance of the dispatch. It runs in it's own thread
 */
@RequiredArgsConstructor
public class NetDelegate extends JedisPubSub implements Runnable {
    /**
     * The name of the channel to subscribe to. Used to send and recieve data. Do not get these out of sync, or this application
     * will not work whatsoever.
     */
    @NonNull
    private String CHAN;

    @NonNull
    private NetCommandDispatch dispatch;

    @Override
    public void onMessage(String chan, String data) {
        if (!chan.equals(CHAN)) return;
        JSONObject obj;
        try {
            obj = new JSONObject(data);
        } catch (JSONException ex) {
            ex.printStackTrace();
            return;
        }
        this.dispatch.handleCommand(obj);
    }

    @Override
    public void onPMessage(String s, String s2, String s3) {
    }

    @Override
    public void onSubscribe(String s, int i) {
    }

    @Override
    public void onUnsubscribe(String s, int i) {
    }

    @Override
    public void onPUnsubscribe(String s, int i) {
    }

    @Override
    public void onPSubscribe(String s, int i) {
    }

    @Override
    public void run() {
        Jedis jedis = dispatch.getPool().getResource();
        jedis.subscribe(this, CHAN);
        dispatch.getPool().returnResource(jedis);
    }
}
