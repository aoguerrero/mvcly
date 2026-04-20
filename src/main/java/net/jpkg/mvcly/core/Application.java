package net.jpkg.mvcly.core;

import static net.jpkg.mvcly.core.MvclyParameters.ENABLE_CACHE;
import static net.jpkg.mvcly.core.MvclyParameters.FILES_PATH;
import static net.jpkg.mvcly.core.MvclyParameters.PORT;
import static net.jpkg.mvcly.core.MvclyParameters.TEMPLATES_PATH;

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
import java.util.Map;
import net.jpkg.mvcly.ctrl.BaseController;
import net.jpkg.mvcly.ctrl.ControllersConfig;
import net.jpkg.mvcly.netty.ControllerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the bootstrap class that starts the web server, the {@code start(...)} method receives the
 * Map of controllers initialized in the parent project.
 */
public class Application {

  private static final Logger logger = LoggerFactory.getLogger(Application.class);

  public void start(ControllersConfig controllersConfig) throws InterruptedException {
    EventLoopGroup parentGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
    EventLoopGroup childGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
    try {
      ServerBootstrap serverBootstrap = new ServerBootstrap();
      serverBootstrap.group(parentGroup, childGroup).channel(NioServerSocketChannel.class)
          .childHandler(getChannelInitializer(controllersConfig.getControllers()));
      ChannelFuture channelFuture = serverBootstrap.bind(Integer.valueOf(PORT.get())).sync();

      logInfo(controllersConfig);

      channelFuture.channel().closeFuture().sync();
    } finally {
      parentGroup.shutdownGracefully();
      childGroup.shutdownGracefully();
    }
  }

  private ChannelInitializer<SocketChannel> getChannelInitializer(
      Map<String, BaseController> controllers) {
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

  private void logInfo(ControllersConfig controllersConfig) {
    logger.info("Accepted JVM parameters: '{}', '{}', '{}', '{}'", PORT.getName(),
        ENABLE_CACHE.getName(),
        TEMPLATES_PATH.getName(), FILES_PATH.getName());
    logger.info("Application listening on port: {}", PORT.get());
    logger.info("Cache enabled: {}", ENABLE_CACHE.get());
    logger.info("Templates path: {}", TEMPLATES_PATH.get());
    logger.info("Static Files path: {}", FILES_PATH.get());
    logger.info("Endpoint paths:");
    controllersConfig.getControllers().keySet().stream().forEach(logger::info);
  }
}
