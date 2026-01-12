package org.by1337.bparser.mixin;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.PacketDecoder;
import net.minecraft.network.ProtocolSwapHandler;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import org.by1337.bparser.event.NetworkEvent;
import org.by1337.bparser.event.SoundEvent;
import org.by1337.bparser.event.VelocityUpdate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PacketDecoder.class)
public abstract class PacketDecoderMixin {

    @Redirect(
            method = "decode",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/ProtocolSwapHandler;handleInboundTerminalPacket(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V"
            )
    )
    private void bparser$redirectInboundTerminalPacket(
            ChannelHandlerContext ctx,
            Packet<?> packet
    ) {
        ProtocolSwapHandler.handleInboundTerminalPacket(ctx, packet);

        bparser$call(packet);
    }


    @Unique
    private static void bparser$call(Object o) {
        if (o instanceof ClientboundSystemChatPacket packet) {
            NetworkEvent.CHAT_EVENT.invoker().on(packet);
        } else if (o instanceof ClientboundSoundPacket packet) {
            NetworkEvent.SOUND_EVENT.invoker().on(new SoundEvent(
                    packet.getSound(),
                    packet.getVolume(),
                    packet.getPitch()
            ));
        } else if (o instanceof ClientboundLevelParticlesPacket packet) {
            NetworkEvent.PARTICLE.invoker().on(packet);
        } else if (o instanceof ClientboundCooldownPacket packet) {
            NetworkEvent.COOLDOWN_UPDATE.invoker().on(packet);
        } else if (o instanceof ClientboundSetEntityMotionPacket packet) {
            VelocityUpdate.Data data = new VelocityUpdate.Data(
                    packet.getId(),
                    packet.getXa(), packet.getYa(), packet.getZa()
            );
            NetworkEvent.VELOCITY_UPDATE.invoker().on(data);

        } else if (o instanceof ClientboundUpdateMobEffectPacket packet) {
            NetworkEvent.MOB_EFFECT.invoker().on(packet);
        } else if (o instanceof ClientboundSetTitleTextPacket packet) {
            NetworkEvent.TITLE.invoker().on(packet);
        } else if (o instanceof ClientboundBossEventPacket packet) {
            NetworkEvent.BOSS_BAR.invoker().on(packet);
        }
    }
}
