package com.swust.queue;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class URLQueue<E> {

private BlockingQueue<E> urlQueue;
public URLQueue(){
	urlQueue = new LinkedBlockingQueue<E>();
}
public URLQueue(int capacity){
	urlQueue = new LinkedBlockingQueue<E>(capacity);
}
public E getOneURL() throws InterruptedException{
return urlQueue.take();
}
public void putOneURL(E e) throws InterruptedException{
	urlQueue.put(e);
}
public void putManyURL(List<E> es){
	urlQueue.addAll(es);
}
}
