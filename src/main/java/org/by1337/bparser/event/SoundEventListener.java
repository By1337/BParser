package org.by1337.bparser.event;


@FunctionalInterface
public interface SoundEventListener {
    void on(SoundEvent event);
}
