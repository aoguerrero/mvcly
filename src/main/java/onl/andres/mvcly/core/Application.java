package onl.andres.mvcly.core;

import java.util.Map;

import onl.andres.mvcly.netty.ControllerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import onl.andres.mvcly.ctrl.BaseController;

import static onl.andres.mvcly.core.MvclyParameters.*;

public class Application {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	public void start(Map<String, BaseController> controllers) throws InterruptedException {
		EventLoopGroup parentGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
		EventLoopGroup childGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
		try {
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(parentGroup, childGroup).channel(NioServerSocketChannel.class)
					.childHandler(getChannelInitializer(controllers));
			ChannelFuture channelFuture = serverBootstrap.bind(Integer.valueOf(PORT.get())).sync();

			logInfo(controllers);

			channelFuture.channel().closeFuture().sync();
		} finally {
			parentGroup.shutdownGracefully();
			childGroup.shutdownGracefully();
		}
	}

	private ChannelInitializer<SocketChannel> getChannelInitializer(Map<String, BaseController> controllers) {
		return new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel socketChannel) throws Exception {
				ChannelPipeline channelPipeline = socketChannel.pipeline();
				channelPipeline.addLast(new HttpRequestDecoder());
				channelPipeline.addLast(new HttpResponseEncoder());
				channelPipeline.addLast(new ControllerHandler(controllers));
			}
		};
	}

	private void logInfo(Map<String, BaseController> controllers) {
		logger.info("Accepted JVM parameters: '{}', '{}', '{}', '{}'", PORT.getName(), ENABLE_CACHE.getName(),
                TEMPLATES_PATH.getName(), FILES_PATH.getName());
		logger.info("Application listening on port: {}", PORT.get());
		logger.info("Cache enabled: {}", ENABLE_CACHE.get());
        logger.info("Templates path: {}", TEMPLATES_PATH.get());
        logger.info("Static Files path: {}", FILES_PATH.get());
		logger.info("Endpoint paths:");
		controllers.keySet().stream().forEach(logger::info);
	}
}
