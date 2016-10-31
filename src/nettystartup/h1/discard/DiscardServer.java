// modified from io.netty.example.discard
package nettystartup.h1.discard;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public final class DiscardServer {
    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1); // accept 용 (받는 부분 - 이리가세요 저리가세요)(스레드 하나)
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // core갯수 * 2 의 스레드 (실 처리 하는 아이)
        try {
            ServerBootstrap bootStrap = new ServerBootstrap(); // 편의 클래스 (서버를 만드는 편의 클래스)
            bootStrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // 어떤 구현체로 할거냐? 네트워크 기반이다. (여기까지는 거의 비슷한 흐름)
                    .handler(new LoggingHandler(LogLevel.INFO)) // 네티 기본 핸들러, 등등 더 추가 가능, childHandler 까지 가지 않고 끊는다 (ip 기반 connection close)e
                    .childHandler(new DiscardServerHandler()); // 여기가 핵심!! 실제 커넥션이 하나하나 할당된 context에서 무슨 일을 할 것인가,
            ChannelFuture f = bootStrap.bind(8010).sync(); // 8010 번 포트로 서버를 올리겠다.
            System.err.println("Ready for 0.0.0.0:8010");
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
