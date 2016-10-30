package cn.ms.asyncnetty;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

public class Test {

	public static void main(String[] args) {
		HttpClient client = HttpClient.builder().followRedirects().build();
		client.get().setURL("https://www.baidu.com").execute(new ResponseHandler<String>(String.class) {
			@Override
			protected void receive(HttpResponseStatus status, HttpHeaders headers, String response) {
				 System.out.println ( "Here's the response: '" + response + "'" );
			}
		});
		System.out.println("=============");
	}
}
