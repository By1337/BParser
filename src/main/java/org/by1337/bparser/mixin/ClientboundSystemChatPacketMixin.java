package org.by1337.bparser.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.by1337.bparser.event.GameMessageS2CPacketAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientboundSystemChatPacket.class)
public abstract class ClientboundSystemChatPacketMixin implements GameMessageS2CPacketAccessor {
    @Final
    @Mutable
    @Shadow
    private Component content;

    @Override
    public void bparser$setMessage(Component message) {
        this.content = message;
    }
}
