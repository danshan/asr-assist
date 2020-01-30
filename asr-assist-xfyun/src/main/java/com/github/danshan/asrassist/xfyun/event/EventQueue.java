package com.github.danshan.asrassist.xfyun.event;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class EventQueue<E> implements BlockingQueue<E> {
    private static final int DEFAULT_MAX_SIZE = 1024;
    private final BlockingQueue<E> queue;
    private final int max_size;

    public EventQueue() {
        this(1024);
    }

    public EventQueue(int max_size) {
        this.max_size = max_size;
        this.queue = new ArrayBlockingQueue(this.max_size);
    }

    public E remove() {
        return this.queue.remove();
    }

    public E poll() {
        return this.queue.poll();
    }

    public E element() {
        return this.queue.element();
    }

    public E peek() {
        return this.queue.peek();
    }

    public int size() {
        return this.queue.size();
    }

    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    public Iterator<E> iterator() {
        return this.queue.iterator();
    }

    public Object[] toArray() {
        return this.queue.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return this.queue.toArray(a);
    }

    public boolean containsAll(Collection<?> c) {
        return this.queue.containsAll(c);
    }

    public boolean addAll(Collection<? extends E> c) {
        return this.queue.addAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return this.queue.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return this.queue.retainAll(c);
    }

    public void clear() {
        this.queue.clear();
    }

    public boolean add(E e) {
        return this.queue.add(e);
    }

    public boolean offer(E e) {
        return this.queue.offer(e);
    }

    public void put(E e) throws InterruptedException {
        this.queue.put(e);
    }

    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return this.queue.offer(e, timeout, unit);
    }

    public E take() throws InterruptedException {
        E x = this.queue.take();
        return x;
    }

    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        return this.queue.poll(timeout, unit);
    }

    public int remainingCapacity() {
        return this.queue.remainingCapacity();
    }

    public boolean remove(Object o) {
        return this.queue.remove(o);
    }

    public boolean contains(Object o) {
        return this.queue.contains(o);
    }

    public int drainTo(Collection<? super E> c) {
        return this.queue.drainTo(c);
    }

    public int drainTo(Collection<? super E> c, int maxElements) {
        return this.queue.drainTo(c, maxElements);
    }
}
