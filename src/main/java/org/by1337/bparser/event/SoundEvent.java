package org.by1337.bparser.event;

import net.minecraft.registry.entry.RegistryEntry;

public class SoundEvent {
    private RegistryEntry<net.minecraft.sound.SoundEvent> sound;
    private float volume;
    private float pitch;

    public SoundEvent(RegistryEntry<net.minecraft.sound.SoundEvent> sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public RegistryEntry<net.minecraft.sound.SoundEvent> getSound() {
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
                "sound=" + sound.getKey().orElse(null) +
                ", volume=" + volume +
                ", pitch=" + pitch +
                '}';
    }
}
