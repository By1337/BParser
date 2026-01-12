package org.by1337.bparser.inv.copy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ScreenData {
    public final Map<Integer, InvItem> content;

    public ScreenData(Container inventory) {
        content = new HashMap<>();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack == null || itemStack.isEmpty()) {
                content.put(slot, InvItem.AIR);
            } else {
                content.put(slot, new InvItem(itemStack));
            }
        }
    }

    public ScreenData() {
        content = new HashMap<>();
    }

    public void set(int slot, InvItem item) {
        content.put(slot, item);
    }

    @Nullable
    public InvItem get(int slot) {
        return content.get(slot);
    }

    public void merge(ScreenData other) {
        for (Integer i : other.content.keySet()) {
            content.put(i, other.content.get(i));
        }
    }

    public Map<Integer, InvItem> getDiff(ScreenData other) {
        Map<Integer, InvItem> diff = new HashMap<>();

        for (Map.Entry<Integer, InvItem> entry : content.entrySet()) {
            Integer key = entry.getKey();
            InvItem value = entry.getValue();

            if (!other.content.containsKey(key)) {
                diff.put(key, InvItem.AIR);
            } else {
                InvItem otherValue = other.content.get(key);
                if (!value.equals(otherValue)) {
                    diff.put(key, otherValue);
                }
            }
        }

        for (Map.Entry<Integer, InvItem> entry : other.content.entrySet()) {
            Integer key = entry.getKey();
            if (!content.containsKey(key)) {
                diff.put(key, entry.getValue());
            }
        }

        return diff;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScreenData that = (ScreenData) o;
        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }
}
