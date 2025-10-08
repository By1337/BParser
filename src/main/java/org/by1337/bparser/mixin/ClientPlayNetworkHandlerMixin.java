package org.by1337.bparser.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.*;
import org.by1337.bparser.event.NetworkEvent;
import org.by1337.bparser.event.SoundEvent;
import org.by1337.bparser.event.VelocityUpdate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Inject(at = @At("HEAD"), method = "onGameMessage(Lnet/minecraft/network/packet/s2c/play/GameMessageS2CPacket;)V")
    private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        NetworkEvent.CHAT_EVENT.invoker().on(packet);
    }

    @Inject(at = @At("HEAD"), method = "onPlaySound(Lnet/minecraft/network/packet/s2c/play/PlaySoundS2CPacket;)V")
    private void onGameMessage(PlaySoundS2CPacket packet, CallbackInfo ci) {
        NetworkEvent.SOUND_EVENT.invoker().on(new SoundEvent(
                packet.getSound(),
                packet.getVolume(),
                packet.getVolume()
        ));
    }

    @Inject(at = @At("HEAD"), method = "onParticle(Lnet/minecraft/network/packet/s2c/play/ParticleS2CPacket;)V")
    private void onParticle(ParticleS2CPacket packet, CallbackInfo ci) {
        NetworkEvent.PARTICLE.invoker().on(packet);
    }

    @Inject(at = @At("HEAD"), method = "onCooldownUpdate(Lnet/minecraft/network/packet/s2c/play/CooldownUpdateS2CPacket;)V")
    private void onCooldownUpdate(CooldownUpdateS2CPacket packet, CallbackInfo ci) {
        NetworkEvent.COOLDOWN_UPDATE.invoker().on(packet);
    }

    @Inject(at = @At("HEAD"), method = "onVelocityUpdate(Lnet/minecraft/network/packet/s2c/play/EntityVelocityUpdateS2CPacket;)V")
    private void onVelocityUpdate(EntityVelocityUpdateS2CPacket packet, CallbackInfo ci) {
        VelocityUpdate.Data data = new VelocityUpdate.Data(
                packet.getId(),
                (double) packet.getVelocityX() / 8000.0, (double) packet.getVelocityY() / 8000.0, (double) packet.getVelocityZ() / 8000.0
        );
        NetworkEvent.VELOCITY_UPDATE.invoker().on(data);
    }

    @Inject(at = @At("HEAD"), method = "onEntityPotionEffect(Lnet/minecraft/network/packet/s2c/play/EntityStatusEffectS2CPacket;)V")
    private void onEntityPotionEffect(EntityStatusEffectS2CPacket packet, CallbackInfo ci) {
        NetworkEvent.MOB_EFFECT.invoker().on(packet);
    }

    @Inject(at = @At("HEAD"), method = "onTitle(Lnet/minecraft/network/packet/s2c/play/TitleS2CPacket;)V")
    private void onTitle(TitleS2CPacket packet, CallbackInfo ci) {
        NetworkEvent.TITLE.invoker().on(packet);
    }

    @Inject(at = @At("HEAD"), method = "onBossBar(Lnet/minecraft/network/packet/s2c/play/BossBarS2CPacket;)V")
    private void onBossBar(BossBarS2CPacket packet, CallbackInfo ci) {
        NetworkEvent.BOSS_BAR.invoker().on(packet);
    }
}
