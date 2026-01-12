package org.by1337.bparser.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.DispenserMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DispenserMenu.class)
public interface DispenserMenuAccessor {
    @Accessor("dispenser")
    Container getInventory();
}
