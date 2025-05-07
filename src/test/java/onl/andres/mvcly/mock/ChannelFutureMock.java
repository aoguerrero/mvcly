package onl.andres.mvcly.mock;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.local.LocalChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class ChannelFutureMock implements ChannelFuture {

	@Override
	public boolean isSuccess() {
		return true;
	}

	@Override
	public boolean isCancellable() {
		return false;
	}

	@Override
	public Throwable cause() {
		return new Exception();
	}

	@Override
	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		return true;
	}

	@Override
	public boolean await(long timeoutMillis) throws InterruptedException {
		return true;
	}

	@Override
	public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
		return true;
	}

	@Override
	public boolean awaitUninterruptibly(long timeoutMillis) {
		return true;
	}

	@Override
	public Void getNow() {
		return null;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return true;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return false;
	}

	@Override
	public Void get() throws InterruptedException, ExecutionException {
		return null;
	}

	@Override
	public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return null;
	}

	@Override
	public Channel channel() {
		return new LocalChannel();
	}

	@Override
	public ChannelFuture addListener(GenericFutureListener<? extends Future<? super Void>> listener) {
		return new ChannelFutureMock();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ChannelFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
		return new ChannelFutureMock();
	}

	@Override
	public ChannelFuture removeListener(GenericFutureListener<? extends Future<? super Void>> listener) {
		return new ChannelFutureMock();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ChannelFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
		return new ChannelFutureMock();
	}

	@Override
	public ChannelFuture sync() throws InterruptedException {
		return new ChannelFutureMock();
	}

	@Override
	public ChannelFuture syncUninterruptibly() {
		return new ChannelFutureMock();
	}

	@Override
	public ChannelFuture await() throws InterruptedException {
		return new ChannelFutureMock();
	}

	@Override
	public ChannelFuture awaitUninterruptibly() {
		return new ChannelFutureMock();
	}

	@Override
	public boolean isVoid() {
		return false;
	}

}
