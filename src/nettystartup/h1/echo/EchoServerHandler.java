// modified from io.netty.example.echo
package nettystartup.h1.echo;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
class EchoServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // TODO: [실습1-2] 받은대로 응답하는 코드를 한 줄 작성합니다. release는 필요하지 않습니다.
        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        // Read에서 wrte를 쭈욱하다가 ReadComplete 에서 flush()를 한다.
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // handler 안에서 예외 상황이 발생하면 예외를 처리하는 케이스이다
        cause.printStackTrace();
        ctx.close();
    }
}
