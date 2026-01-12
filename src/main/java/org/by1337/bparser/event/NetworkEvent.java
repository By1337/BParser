package org.by1337.bparser.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;

public class NetworkEvent {
    public static final Event<PacketCallback<ClientboundSystemChatPacket>> CHAT_EVENT = EventFactory.createArrayBacked(PacketCallback.class,
            (listeners) -> (packet) -> {
                for (PacketCallback<ClientboundSystemChatPacket> listener : listeners) {
                    listener.on(packet);
                }
            }
    );
    public static final Event<SoundEventListener> SOUND_EVENT = EventFactory.createArrayBacked(SoundEventListener.class, (listeners) -> (packet) -> {
        for (SoundEventListener listener : listeners) {
            listener.on(packet);
        }
    });
    public static final Event<PacketCallback<ClientboundLevelParticlesPacket>> PARTICLE = EventFactory.createArrayBacked(PacketCallback.class,
            (listeners) -> (packet) -> {
                for (PacketCallback<ClientboundLevelParticlesPacket> listener : listeners) {
                    listener.on(packet);
                }
            }
    );
    public static final Event<PacketCallback<ClientboundCooldownPacket>> COOLDOWN_UPDATE = EventFactory.createArrayBacked(PacketCallback.class,
            (listeners) -> (packet) -> {
                for (PacketCallback<ClientboundCooldownPacket> listener : listeners) {
                    listener.on(packet);
                }
            }
    );
    public static final Event<VelocityUpdate> VELOCITY_UPDATE = EventFactory.createArrayBacked(VelocityUpdate.class, (listeners) -> (packet) -> {
        for (VelocityUpdate listener : listeners) {
            listener.on(packet);
        }
    });

    public static final Event<PacketCallback<ClientboundUpdateMobEffectPacket>> MOB_EFFECT = EventFactory.createArrayBacked(PacketCallback.class, (listeners) -> (packet) -> {
        for (PacketCallback<ClientboundUpdateMobEffectPacket> listener : listeners) {
            listener.on(packet);
        }
    });

    public static final Event<PacketCallback<ClientboundSetTitleTextPacket>> TITLE = EventFactory.createArrayBacked(PacketCallback.class, (listeners) -> (packet) -> {
        for (PacketCallback<ClientboundSetTitleTextPacket> listener : listeners) {
            listener.on(packet);
        }
    });

    public static final Event<PacketCallback<ClientboundBossEventPacket>> BOSS_BAR = EventFactory.createArrayBacked(PacketCallback.class, (listeners) -> (packet) -> {
        for (PacketCallback<ClientboundBossEventPacket> listener : listeners) {
            listener.on(packet);
        }
    });

    @FunctionalInterface
    public interface PacketCallback<T extends Packet<?>> {
        void on(T packet);
    }
}
