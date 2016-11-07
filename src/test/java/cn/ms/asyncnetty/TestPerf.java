package cn.ms.asyncnetty;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

public class TestPerf {

	public static void main(String[] args) throws Exception{
		final HttpClient client = HttpClient.builder().followRedirects().build();
		Perf perf = new Perf(){ 
			public TaskInThread buildTaskInThread() {
				return new TaskInThread(){
					public void initTask() throws Exception {
					}
					
					public void doTask() throws Exception {
						client.get().setURL("http://localhost:8844/").execute(new ResponseHandler<String>(String.class) {
							@Override
							protected void receive(HttpResponseStatus status, HttpHeaders headers, String response) {
								if(!new String(response.getBytes()).contains("I am OK")){
									throw new RuntimeException();
								}
							}
						});
					}
				};
			} 
		}; 
		perf.loopCount = 10000;
		perf.threadCount = 16;
		perf.logInterval = 10000;
		perf.run();
		perf.close();
	} 
	
}
