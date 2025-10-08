package org.by1337.bparser.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.*;

public class NetworkEvent {
    public static final Event<PacketCallback<GameMessageS2CPacket>> CHAT_EVENT = EventFactory.createArrayBacked(PacketCallback.class,
            (listeners) -> (packet) -> {
                for (PacketCallback<GameMessageS2CPacket> listener : listeners) {
                    listener.on(packet);
                }
            }
    );
    public static final Event<SoundEventListener> SOUND_EVENT = EventFactory.createArrayBacked(SoundEventListener.class, (listeners) -> (packet) -> {
        for (SoundEventListener listener : listeners) {
            listener.on(packet);
        }
    });
    public static final Event<PacketCallback<ParticleS2CPacket>> PARTICLE = EventFactory.createArrayBacked(PacketCallback.class,
            (listeners) -> (packet) -> {
                for (PacketCallback<ParticleS2CPacket> listener : listeners) {
                    listener.on(packet);
                }
            }
    );
    public static final Event<PacketCallback<CooldownUpdateS2CPacket>> COOLDOWN_UPDATE = EventFactory.createArrayBacked(PacketCallback.class,
            (listeners) -> (packet) -> {
                for (PacketCallback<CooldownUpdateS2CPacket> listener : listeners) {
                    listener.on(packet);
                }
            }
    );
    public static final Event<VelocityUpdate> VELOCITY_UPDATE = EventFactory.createArrayBacked(VelocityUpdate.class, (listeners) -> (packet) -> {
        for (VelocityUpdate listener : listeners) {
            listener.on(packet);
        }
    });

    public static final Event<PacketCallback<EntityStatusEffectS2CPacket>> MOB_EFFECT = EventFactory.createArrayBacked(PacketCallback.class, (listeners) -> (packet) -> {
        for (PacketCallback<EntityStatusEffectS2CPacket> listener : listeners) {
            listener.on(packet);
        }
    });

    public static final Event<PacketCallback<TitleS2CPacket>> TITLE = EventFactory.createArrayBacked(PacketCallback.class, (listeners) -> (packet) -> {
        for (PacketCallback<TitleS2CPacket> listener : listeners) {
            listener.on(packet);
        }
    });

    public static final Event<PacketCallback<BossBarS2CPacket>> BOSS_BAR = EventFactory.createArrayBacked(PacketCallback.class, (listeners) -> (packet) -> {
        for (PacketCallback<BossBarS2CPacket> listener : listeners) {
            listener.on(packet);
        }
    });

    @FunctionalInterface
    public interface PacketCallback<T extends Packet<?>> {
        void on(T packet);
    }
}
