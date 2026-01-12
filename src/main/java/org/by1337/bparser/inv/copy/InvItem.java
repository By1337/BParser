package org.by1337.bparser.inv.copy;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InvItem {
    public static final InvItem AIR = new InvItem(ItemStack.EMPTY);
    public final CompoundTag extra;
    public final ItemStack itemStack;
    public List<Integer> slots = new ArrayList<>();

    public InvItem(ItemStack itemStack) {
        CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
        if (customData != null){
            extra = customData.copyTag();
        } else {
            extra = null;
        }
        this.itemStack = itemStack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvItem item = (InvItem) o;
        return itemStack.getComponentsPatch().entrySet().equals(item.itemStack.getComponentsPatch().entrySet());
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemStack.getComponentsPatch().entrySet());
    }
}
