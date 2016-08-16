package com.zjp.schedule.client;

import com.zjp.schedule.core.Callback;
import com.zjp.schedule.entity.Response;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * ©¥©¥©¥©¥©¥©¥ÄÏÎÞ°¢ÃÖÍÓ·ð©¥©¥©¥©¥©¥©¥
 * ¡¡¡¡¡¡©³©·¡¡¡¡¡¡©³©·
 * ¡¡¡¡©³©¿©ß©¥©¥©¥©¿©ß©·
 * ¡¡¡¡©§¡¡¡¡¡¡¡¡¡¡¡¡¡¡©§
 * ¡¡¡¡©§¡¡¡¡¡¡©¥¡¡¡¡¡¡©§
 * ¡¡¡¡©§¡¡©×©¿¡¡©»©×¡¡©§
 * ¡¡¡¡©§¡¡¡¡¡¡¡¡¡¡¡¡¡¡©§
 * ¡¡¡¡©§¡¡¡¡¡¡©ß¡¡¡¡¡¡©§
 * ¡¡¡¡©§¡¡¡¡¡¡¡¡¡¡¡¡¡¡©§
 * ¡¡¡¡©»©¥©·¡¡¡¡¡¡©³©¥©¿
 * ¡¡¡¡¡¡¡¡©§¡¡¡¡¡¡©§stay hungry stay foolish
 * ¡¡¡¡¡¡¡¡©§¡¡¡¡¡¡©§Code is far away from bug with the animal protecting
 * ¡¡¡¡¡¡¡¡©§¡¡¡¡¡¡©»©¥©¥©¥©·
 * ¡¡¡¡¡¡¡¡©§¡¡¡¡¡¡¡¡¡¡¡¡¡¡©Ç©·
 * ¡¡¡¡¡¡¡¡©§¡¡¡¡¡¡¡¡¡¡¡¡¡¡©³©¿
 * ¡¡¡¡¡¡¡¡©»©·©·©³©¥©×©·©³©¿
 * ¡¡¡¡¡¡¡¡¡¡©§©Ï©Ï¡¡©§©Ï©Ï
 * ¡¡¡¡¡¡¡¡¡¡©»©ß©¿¡¡©»©ß©¿
 * ©¥©¥©¥©¥©¥©¥ÃÈÃÈßÕ©¥©¥©¥©¥©¥©¥
 * Module Desc:com.zjp.schedule.client
 * User: zjprevenge
 * Date: 2016/8/11
 * Time: 17:06
 */

public class RpcFuture<V> implements Future<V> {

    private volatile Response response;
    private volatile Exception exception;
    private volatile boolean done;
    private volatile int waiters;
    private volatile Callback callback;

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException {
        long timeoutMillis = unit.toMillis(timeout);
        long endTime = System.currentTimeMillis() + timeoutMillis;
        synchronized (this) {
            if (done) {
                return done;
            }
            if (timeoutMillis <= 0) {
                return done;
            }
            waiters++;
            try {
                while (!done) {
                    wait(timeoutMillis);
                    if (endTime < System.currentTimeMillis() && !done) {
                        exception = new TimeoutException("time out");
                        break;
                    }
                }
            } finally {
                waiters--;
            }
        }
        return done;
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public Exception getException() {
        return exception;
    }

    public Response getResponse() {
        return response;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    public boolean isCancelled() {
        return false;
    }

    public boolean isDone() {
        return done;
    }

    public V get() throws InterruptedException, ExecutionException {
        try {
            return get(Integer.MAX_VALUE, TimeUnit.DAYS);
        } catch (TimeoutException e) {
            throw new InterruptedException(e.getMessage());
        }
    }

    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        await(timeout, unit);
        try {
            Response response = getResponse();
            return (V) response.getResult();
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }
}
