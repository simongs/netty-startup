package nettystartup.h2.http;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;

import java.io.IOException;
import java.io.RandomAccessFile;

public class HttpStaticFileHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private String path;
    private String filename;

    public HttpStaticFileHandler(String path, String filename) {
        super(false); // set auto-release to false
        this.path = path;
        this.filename = filename;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        // TODO: [실습2-1] sendStaticFile메소드를 써서 구현합니다. "/" 요청이 아닌 경우에는 어떻게 할까요?
        // 이 Handler는 SimpleChannelInboundHandler<I>를 확장했지만 "auto-release: false"임에 주의합니다.
        // 상황에 따라 "필요시"에는 retain()을 부르도록 합니다.

        if (!("/".equals(req.uri()))) {
            ctx.fireChannelRead(req);
            return;
        }

        sendStaticFile(ctx, req);
    }

    private void sendStaticFile(ChannelHandlerContext ctx, FullHttpRequest req) throws IOException {
        try {
            RandomAccessFile raf = new RandomAccessFile(filename, "r");
            long fileLength = raf.length();

            HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            HttpUtil.setContentLength(res, fileLength);
            res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=utf-8");
            if (HttpUtil.isKeepAlive(req)) {
                res.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
            ctx.write(res); // 응답 헤더 전송
            ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength));
            ChannelFuture f = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            if (!HttpUtil.isKeepAlive(req)) {
                f.addListener(ChannelFutureListener.CLOSE);
            }
        } finally {
            req.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
