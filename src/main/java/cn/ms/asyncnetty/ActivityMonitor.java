package cn.ms.asyncnetty;

import com.mastfrog.url.URL;

/**
 * Allows network activity to be monitored in an HTTP client
 * 
 * @author lry
 */
public interface ActivityMonitor {

    void onStartRequest(URL url);

    void onEndRequest(URL url);
}
