package org.by1337.bparser.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.HopperMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HopperMenu.class)
public interface HopperMenuAccessor {
    @Accessor("hopper")
    Container getInventory();
}
