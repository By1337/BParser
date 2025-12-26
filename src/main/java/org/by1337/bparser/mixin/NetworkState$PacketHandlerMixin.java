package org.by1337.bparser.mixin;

import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.*;
import org.by1337.bparser.event.NetworkEvent;
import org.by1337.bparser.event.SoundEvent;
import org.by1337.bparser.event.VelocityUpdate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NetworkState.PacketHandler.class)
public class NetworkState$PacketHandlerMixin {

    @Inject(method = "createPacket", at = @At("RETURN"))
    private void onCreatePacket(int id, PacketByteBuf buf, CallbackInfoReturnable<Packet<?>> cir) {
        Packet<?> packet = cir.getReturnValue();
        if (packet != null) {
            bparser$call(packet);
        }
    }

    @Unique
    private void bparser$call(Object o) {
        if (o instanceof GameMessageS2CPacket packet) {
            NetworkEvent.CHAT_EVENT.invoker().on(packet);
        } else if (o instanceof PlaySoundS2CPacket packet) {
            NetworkEvent.SOUND_EVENT.invoker().on(new SoundEvent(
                    packet.getSound(),
                    packet.getVolume(),
                    packet.getVolume()
            ));
        } else if (o instanceof ParticleS2CPacket packet) {
            NetworkEvent.PARTICLE.invoker().on(packet);
        } else if (o instanceof CooldownUpdateS2CPacket packet) {
            NetworkEvent.COOLDOWN_UPDATE.invoker().on(packet);
        } else if (o instanceof EntityVelocityUpdateS2CPacket packet) {
            VelocityUpdate.Data data = new VelocityUpdate.Data(
                    packet.getId(),
                    (double) packet.getVelocityX() / 8000.0, (double) packet.getVelocityY() / 8000.0, (double) packet.getVelocityZ() / 8000.0
            );
            NetworkEvent.VELOCITY_UPDATE.invoker().on(data);

        } else if (o instanceof EntityStatusEffectS2CPacket packet) {
            NetworkEvent.MOB_EFFECT.invoker().on(packet);
        } else if (o instanceof TitleS2CPacket packet) {
            NetworkEvent.TITLE.invoker().on(packet);
        } else if (o instanceof BossBarS2CPacket packet) {
            NetworkEvent.BOSS_BAR.invoker().on(packet);
        }
    }
}
