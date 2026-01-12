package org.by1337.bparser.event;

import net.minecraft.network.chat.Component;

public interface GameMessageS2CPacketAccessor {
    void bparser$setMessage(Component message);

    default void setMessage(Component message) {
        bparser$setMessage(message);
    }
}
