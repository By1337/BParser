package org.by1337.bparser.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.BeaconMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BeaconMenu.class)
public interface BeaconMenuAccessor {
    @Accessor("beacon")
    Container getInventory();
}
