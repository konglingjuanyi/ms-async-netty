package cn.ms.asyncnetty;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

public class Test {

	public static void main(String[] args) {
		HttpClient client = HttpClient.builder().followRedirects().build();
		client.get().setURL("http://localhost:8844/").execute(new ResponseHandler<String>(String.class) {
			@Override
			protected void receive(HttpResponseStatus status, HttpHeaders headers, String response) {
				 System.out.println(new String(response.getBytes()));
			}
		});
	}
}
