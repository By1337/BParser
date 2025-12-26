package org.by1337.bparser.inv.copy;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InvItem {
    public static final InvItem AIR = new InvItem(new NbtCompound(), new ItemStack(net.minecraft.item.Items.AIR));
    public final NbtCompound trimNBT;
    public final NbtCompound fullNBT;
    public final ItemStack itemStack;
    private final String data;
    public List<Integer> slots = new ArrayList<>();

    public InvItem(NbtCompound nbt0, ItemStack itemStack) {
        this.trimNBT = nbt0.copy();
        this.fullNBT = nbt0.copy();
        if (trimNBT.contains("tag", NbtElement.COMPOUND_TYPE)) {
            NbtCompound tag = trimNBT.getCompound("tag");
            tag.remove("PublicBukkitValues");
            tag.remove("AttributeModifiers");
            tag.remove("BlockEntityTag");
        }
        this.data = trimNBT.toString();
        this.itemStack = itemStack;
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
        return Objects.hash(data);
    }
}
