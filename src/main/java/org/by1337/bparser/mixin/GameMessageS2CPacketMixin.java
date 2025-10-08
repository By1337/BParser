package org.by1337.bparser.mixin;

import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import org.by1337.bparser.event.GameMessageS2CPacketAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameMessageS2CPacket.class)
public abstract class GameMessageS2CPacketMixin implements GameMessageS2CPacketAccessor {
    @Shadow
    private Text message;

    @Override
    public void bparser$setMessage(Text message) {
        this.message = message;
    }
}
