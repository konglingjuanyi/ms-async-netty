package cn.ms.asyncnetty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.DefaultCookie;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.mastfrog.acteur.headers.Headers;

/**
 * @author lry
 */
@SuppressWarnings("deprecation")
public class CookieStoreTest {

    @Test
    public void test() throws IOException {
        assertTrue(true);
        CookieStore store = new CookieStore();
        DefaultCookie ck1 = new DefaultCookie("foo", "bar");
        DefaultCookie ck2 = new DefaultCookie("one", "two");
        ck1.setPath("/foo");
        ck1.setDomain("foo.com");
        ck1.setMaxAge(10000);

        ck2.setPath("/foo");
        ck2.setDomain("foo.com");
        ck2.setMaxAge(10000);

        DefaultFullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        resp.headers().add(Headers.SET_COOKIE.name(), Headers.SET_COOKIE.toString(ck1));
        resp.headers().add(Headers.SET_COOKIE.name(), Headers.SET_COOKIE.toString(ck2));

        store.extract(resp.headers());
        Iterator<Cookie> iter = store.iterator();
        assertTrue(iter.hasNext());
        assertTrue(iter.hasNext());

        HttpRequest req = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/foo/bar");
        req.headers().add(Headers.HOST.name(), "foo.com");
        store.decorate(req);

        List<String> cookieHeaders = req.headers().getAll(Headers.COOKIE.name());
        assertEquals(2, cookieHeaders.size());

        List<String> find = new LinkedList<>(Arrays.asList("foo", "one"));
        for (String hdr : cookieHeaders) {
            Cookie cookie = Headers.SET_COOKIE.toValue(hdr);
            find.remove(cookie.getName());
        }
        assertTrue("Not found: " + find, find.isEmpty());

        CookieStore nue = new CookieStore(store.cookies, true, true);
        assertEquals(store, nue);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        store.store(out);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        nue = new CookieStore();
        nue.read(in);
        assertEquals(store, nue);

        DefaultCookie ck3 = new DefaultCookie("fuz", "bang");
        ck3.setMaxAge(20000);
        ck3.setPath("/moo/wuzz");
        ck3.setDomain("foo.com");
        nue.add(ck3);
        assertNotEquals(store, nue);
    }
}
