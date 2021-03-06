package cn.ms.asyncnetty;

import com.mastfrog.url.HostAndPort;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import javax.net.ssl.SSLEngine;

/**
 *
 * @author lry
 */
final class Initializer extends ChannelInitializer<Channel> {

    private final HostAndPort hostPort;

    private final ChannelInboundHandlerAdapter handler;
    private final SslContext context;
    private final boolean ssl;
    private final int maxChunkSize;
    private final int maxInitialLineLength;
    private final boolean compress;

    public Initializer(HostAndPort hostPort, ChannelInboundHandlerAdapter handler, SslContext context, boolean ssl, int maxChunkSize, int maxInitialLineLength, int maxHeadersSize, boolean compress) {
        this.hostPort = hostPort;
        this.handler = handler;
        this.context = context;
        this.ssl = ssl;
        this.maxChunkSize = maxChunkSize;
        this.maxInitialLineLength = maxInitialLineLength;
        this.compress = compress;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (ssl) {
            SslContext clientContext = context == null ? SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build() : context;
            pipeline.addLast("ssl", new ExceptionForwardingSslHandler(clientContext.newEngine(ByteBufAllocator.DEFAULT, hostPort.host(), hostPort.port())));
        }
        pipeline.addLast("http-codec", new HttpClientCodec(maxInitialLineLength, maxChunkSize, maxChunkSize));
        if (compress) {
            pipeline.addLast("decompressor", new HttpContentDecompressor());
        }
        pipeline.addLast("handler", handler);
    }

    // Ensure exceptions during handshaking get propagated
    private static class ExceptionForwardingSslHandler extends SslHandler {

        public ExceptionForwardingSslHandler(SSLEngine engine) {
            super(engine);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            RequestInfo info = ctx.channel().attr(HttpClient.KEY).get();
            if (info != null) {
                info.handle.event(new State.Error(cause));
            }
//            super.exceptionCaught(ctx, cause);
            cause.printStackTrace(System.err);
        }
    }
}
