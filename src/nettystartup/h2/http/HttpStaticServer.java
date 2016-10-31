package nettystartup.h2.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import nettystartup.h2.NettyStartupUtil;

public class HttpStaticServer {
	static String index = System.getProperty("user.dir") + "/res/h2/index.html";

	public static void main(String[] args) throws Exception {
		NettyStartupUtil.runServer(8020, new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) {
				ChannelPipeline p = ch.pipeline();
				p.addLast(new HttpServerCodec()); // 패킷을 HTTP 스펙에 맞게끔 잘라주는 코덱
				p.addLast(new HttpObjectAggregator(65536)); // 모아주는 버퍼
				p.addLast(new HttpStaticFileHandler("/", index)); // "/" 에 대해서 index를 넘겨준다
				p.addLast(new HttpNotFoundHandler()); // "/" 에 대해서 index를 넘겨준다
				// TODO: [실습2-2] HttpNotFoundHandler를 써서 404응답을 처리합니다.

			}
		});
	}
}
