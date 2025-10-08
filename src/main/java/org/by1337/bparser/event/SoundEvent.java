package org.by1337.bparser.event;

public class SoundEvent {
    private net.minecraft.sound.SoundEvent sound;
    private float volume;
    private float pitch;

    public SoundEvent(net.minecraft.sound.SoundEvent sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public net.minecraft.sound.SoundEvent getSound() {
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
                "sound=" + sound.getId().toString() +
                ", volume=" + volume +
                ", pitch=" + pitch +
                '}';
    }
}
