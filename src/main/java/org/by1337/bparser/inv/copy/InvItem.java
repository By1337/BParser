package org.by1337.bparser.inv.copy;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.RegistryOps;
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
    public String data;

    public InvItem(ItemStack itemStack) {
        CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            extra = customData.copyTag();
        } else {
            extra = null;
        }
        this.itemStack = itemStack;
        if (!itemStack.isEmpty()) {
            data = ItemStack.CODEC.encodeStart(
                    RegistryOps.create(NbtOps.INSTANCE, Minecraft.getInstance().level.registryAccess()),
                    itemStack
            ).result().get().toString();
        } else {
            data = "air";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvItem item = (InvItem) o;
        return Objects.equals(data, item.data);
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }
}
