package org.by1337.bparser.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShulkerBoxMenu.class)
public interface ShulkerBoxMenuAccessor {
    @Accessor("container")
    Container getInventory();
}
