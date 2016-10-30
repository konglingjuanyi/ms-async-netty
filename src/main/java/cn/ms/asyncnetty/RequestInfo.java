package cn.ms.asyncnetty;

import com.mastfrog.url.URL;
import io.netty.handler.codec.http.HttpRequest;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * @author lry
 */
final class RequestInfo {

    final URL url;
    final HttpRequest req;
    final AtomicBoolean cancelled;
    final ResponseFuture handle;
    final ResponseHandler<?> r;
    final AtomicInteger redirectCount = new AtomicInteger();
    final Duration timeout;
    final DateTime startTime;
    volatile boolean listenerAdded;
    TimerTask timer;
    final boolean dontAggregate;

    public RequestInfo(URL url, HttpRequest req, AtomicBoolean cancelled, ResponseFuture handle, ResponseHandler<?> r, Duration timeout, DateTime startTime, TimerTask timer, boolean noAggregate) {
        this.url = url;
        this.req = req;
        this.cancelled = cancelled;
        this.handle = handle;
        this.r = r;
        this.timeout = timeout;
        this.startTime = startTime;
        this.timer = timer;
        this.dontAggregate = noAggregate;
    }

    public RequestInfo(URL url, HttpRequest req, AtomicBoolean cancelled, ResponseFuture handle, ResponseHandler<?> r, Duration timeout, TimerTask timer, boolean noAggregate) {
        this(url, req, cancelled, handle, r, timeout, DateTime.now(), timer, noAggregate);
    }
    
    Duration age() {
        return new Duration(startTime, DateTime.now());
    }
    
    Duration remaining() {
        return timeout == null ? null : timeout.minus(age());
    }

    boolean isExpired() {
        if (timeout != null) {
            return DateTime.now().isAfter(startTime.plus(timeout));
        }
        return false;
    }
    
    void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public String toString() {
        return "RequestInfo{" + "url=" + url + ", req=" + req + ", cancelled="
                + cancelled + ", handle=" + handle + ", r=" + r + ", timeout=" + timeout + '}';
    }
}
