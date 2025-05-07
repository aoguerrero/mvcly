package onl.andres.mvcly.mock;

import java.net.SocketAddress;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;

public class ContextMock implements ChannelHandlerContext {
	
	private byte[] content;
	private String locationHeader;
	private HttpResponseStatus status;

	@Override
	public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
		return new ChannelFutureMock();
	}

	@Override
	public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
		return new ChannelFutureMock();
	}

	@Override
	public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
		return new ChannelFutureMock();
	}

	@Override
	public ChannelFuture disconnect(ChannelPromise promise) {
		return new ChannelFutureMock();
	}

	@Override
	public ChannelFuture close(ChannelPromise promise) {
		return new ChannelFutureMock();
	}

	@Override
	public ChannelFuture deregister(ChannelPromise promise) {
		return new ChannelFutureMock();
	}

	@Override
	public ChannelFuture write(Object msg, ChannelPromise promise) {
		if(msg instanceof FullHttpResponse response) {
			content = response.content().array();
			locationHeader = response.headers().get("Location");
			status = response.status();
		}
		return new ChannelFutureMock();
	}

	@Override
	public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
		return new ChannelFutureMock();
	}

	@Override
	public ChannelPromise newPromise() {
		return null;
	}

	@Override
	public ChannelProgressivePromise newProgressivePromise() {
		return null;
	}

	@Override
	public ChannelFuture newSucceededFuture() {
		return new ChannelFutureMock();
	}

	@Override
	public ChannelFuture newFailedFuture(Throwable cause) {
		return new ChannelFutureMock();
	}

	@Override
	public ChannelPromise voidPromise() {
		return null;
	}

	@Override
	public Channel channel() {
		return null;
	}

	@Override
	public EventExecutor executor() {
		return null;
	}

	@Override
	public String name() {
		return null;
	}

	@Override
	public ChannelHandler handler() {
		return null;
	}

	@Override
	public boolean isRemoved() {
		return false;
	}

	@Override
	public ChannelHandlerContext fireChannelRegistered() {
		return null;
	}

	@Override
	public ChannelHandlerContext fireChannelUnregistered() {
		return null;
	}

	@Override
	public ChannelHandlerContext fireChannelActive() {
		return null;
	}

	@Override
	public ChannelHandlerContext fireChannelInactive() {
		return null;
	}

	@Override
	public ChannelHandlerContext fireExceptionCaught(Throwable cause) {
		return null;
	}

	@Override
	public ChannelHandlerContext fireUserEventTriggered(Object evt) {
		return null;
	}

	@Override
	public ChannelHandlerContext fireChannelRead(Object msg) {
		return null;
	}

	@Override
	public ChannelHandlerContext fireChannelReadComplete() {
		return null;
	}

	@Override
	public ChannelHandlerContext fireChannelWritabilityChanged() {
		return null;
	}

	@Override
	public ChannelHandlerContext read() {
		return null;
	}

	@Override
	public ChannelHandlerContext flush() {
		return null;
	}

	@Override
	public ChannelPipeline pipeline() {
		return null;
	}

	@Override
	public ByteBufAllocator alloc() {
		return null;
	}

	@Override
	public <T> Attribute<T> attr(AttributeKey<T> key) {
		return null;
	}

	@Override
	public <T> boolean hasAttr(AttributeKey<T> key) {
		return false;
	}

	public byte[] getContent() {
		return content;
	}

	public String getLocationHeader() {
		return locationHeader;
	}

	public HttpResponseStatus getStatus() {
		return status;
	}
	
}
