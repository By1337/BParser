package org.by1337.bparser.event;


import net.minecraft.core.Holder;

public class SoundEvent {
    private Holder<net.minecraft.sounds.SoundEvent> sound;
    private float volume;
    private float pitch;

    public SoundEvent(Holder<net.minecraft.sounds.SoundEvent> sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public Holder<net.minecraft.sounds.SoundEvent> getSound() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    @Override
    public String toString() {
        return "SoundEvent{" +
                "sound=" + sound +
                ", volume=" + volume +
                ", pitch=" + pitch +
                '}';
    }
}
