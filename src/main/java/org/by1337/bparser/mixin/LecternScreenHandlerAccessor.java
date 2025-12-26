package org.by1337.bparser.mixin;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.LecternScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LecternScreenHandler.class)
public interface LecternScreenHandlerAccessor {
    @Accessor("inventory")
    Inventory getInventory();
}
