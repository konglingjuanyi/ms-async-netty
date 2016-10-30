package cn.ms.asyncnetty;

import com.mastfrog.util.thread.Receiver;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.joda.time.Duration;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * @author lry
 */
public class ConnectionRefusedTest {

    @Test
    public void testTimeout() throws Throwable {
        final CountDownLatch latch = new CountDownLatch(1);
        HttpClient client = HttpClient.builder().setTimeout(Duration.standardSeconds(3)).build();
        final AtomicBoolean notified = new AtomicBoolean();
        client.get().setTimeout(new Duration(2)).setURL("http://10.0.0.0/abcd")
                .onEvent(new Receiver<State<?>>() {

                    @Override
                    public void receive(State<?> object) {
                        System.out.println("1STATE " + object);
                    }
                }).execute(new ResponseHandler<Object>(Object.class) {

            @Override
            protected void receive(Object obj) {
                System.out.println("RECEIVE " + obj);
                notified.set(true);
                latch.countDown();
            }

            @Override
            protected void onError(Throwable err) {
                System.out.println("onError");
                err.printStackTrace();
                notified.set(true);
                latch.countDown();
            }

        }).await(10, TimeUnit.SECONDS);
        Thread.sleep(3000);
        assertTrue(notified.get());
    }

    @Test
    public void test() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        HttpClient client = HttpClient.builder().setTimeout(Duration.standardSeconds(3)).build();
        final AtomicBoolean notified = new AtomicBoolean();
        client.get().setURL("http://192.168.1.254:10001/abcd")
                .onEvent(new Receiver<State<?>>() {

                    @Override
                    public void receive(State<?> object) {
                        System.out.println("2STATE " + object);
                    }
                })
                .execute(new ResponseHandler<Object>(Object.class) {

                    @Override
                    protected void onError(Throwable err) {
                        err.printStackTrace();
                        notified.set(true);
                        latch.countDown();
                    }

                    @Override
                    protected void onErrorResponse(HttpResponseStatus status, HttpHeaders headers, String content) {
                        System.out.println("onErrorResponse " + status + " - " + content);
                    }

                    @Override
                    protected void receive(Object obj) {
                        System.out.println("RECEIVE " + obj);
                    }

                    @Override
                    protected void receive(HttpResponseStatus status, HttpHeaders headers, Object obj) {
                        System.out.println("Receive " + status + " " + obj);
                    }

                    @Override
                    protected void receive(HttpResponseStatus status, Object obj) {
                        super.receive(status, obj); //To change body of generated methods, choose Tools | Templates.
                    }
                }).await(1, TimeUnit.SECONDS);
        latch.await(20, TimeUnit.SECONDS);
        if (!notified.get()) {
            Thread.sleep(5000);
        }
        assertTrue(notified.get());
    }
}
