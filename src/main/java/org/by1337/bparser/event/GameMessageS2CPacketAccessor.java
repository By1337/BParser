package org.by1337.bparser.event;

import net.minecraft.text.Text;

public interface GameMessageS2CPacketAccessor {
    void bparser$setMessage(Text message);

    default void setMessage(Text message) {
        bparser$setMessage(message);
    }
}
