package com.grim3212.assorted.mobs.gametest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Counts the game's "unsafe terrain read during worldgen" errors, which is all it says of a spawn
 * rule or a feature that reached into a chunk it may not: nothing fails, the log fills, and sooner or
 * later a worldgen thread waits on a chunk that is waiting on it and the game hangs.
 */
final class UnsafeTerrainReadWatch extends AbstractAppender {

    private static final String MARK = "unsafe terrain read during worldgen";

    private final AtomicInteger count = new AtomicInteger();
    private volatile String first;

    private UnsafeTerrainReadWatch() {
        super("assortedmobs-unsafe-terrain-read-watch", null, null, true, Property.EMPTY_ARRAY);
    }

    static UnsafeTerrainReadWatch install() {
        UnsafeTerrainReadWatch watch = new UnsafeTerrainReadWatch();
        watch.start();
        ((Logger) LogManager.getRootLogger()).addAppender(watch);
        return watch;
    }

    void remove() {
        ((Logger) LogManager.getRootLogger()).removeAppender(this);
        this.stop();
    }

    @Override
    public void append(LogEvent event) {
        String message = event.getMessage().getFormattedMessage();
        if (message != null && message.contains(MARK) && this.count.getAndIncrement() == 0) {
            this.first = message;
        }
    }

    int count() {
        return this.count.get();
    }

    String first() {
        return this.first;
    }
}
