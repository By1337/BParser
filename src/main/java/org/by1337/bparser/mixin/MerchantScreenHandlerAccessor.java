package org.by1337.bparser.mixin;

import net.minecraft.village.MerchantInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(net.minecraft.screen.MerchantScreenHandler.class)
public interface MerchantScreenHandlerAccessor {
    @Accessor("merchantInventory")
    MerchantInventory getInventory();
}
