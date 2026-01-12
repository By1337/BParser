package org.by1337.bparser.inv;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.*;
import org.by1337.bparser.mixin.*;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class ScreenUtil {

    @Nullable
    public static Container getInventory(AbstractContainerScreen<?> handler0) {
        Object handler = handler0.getMenu();
        if (handler instanceof ChestMenu) {
            return ((ChestMenu) handler).getContainer();
        } else if (handler instanceof DispenserMenu) {
            return ((DispenserMenuAccessor) handler).getInventory();
        } else if (handler instanceof BeaconMenu) {
            return ((BeaconMenuAccessor) handler).getInventory();
        } else if (handler instanceof BlastFurnaceMenu) {
            return ((AbstractFurnaceMenuAccessor) handler).getInventory();
        } else if (handler instanceof BrewingStandMenu) {
            return ((BrewingStandMenuAccessor) handler).getInventory();
        } else if (handler instanceof CraftingMenu) {
            return ((AbstractCraftingMenuAccessor) handler).getInventory();
        } else if (handler instanceof EnchantmentMenu) {
            return ((EnchantmentMenuAccessor) handler).getInventory();
        } else if (handler instanceof FurnaceMenu) {
            return ((AbstractFurnaceMenuAccessor) handler).getInventory();
        } else if (handler instanceof HopperMenu) {
            return ((HopperMenuAccessor) handler).getInventory();
        } else if (handler instanceof LecternMenu) {
            return ((LecternMenuAccessor) handler).getInventory();
        } else if (handler instanceof MerchantMenu) {
            return ((MerchantMenuAccessor) handler).getInventory();
        } else if (handler instanceof ShulkerBoxMenu) {
            return ((ShulkerBoxMenuAccessor) handler).getInventory();
        } else if (handler instanceof SmokerMenu) {
            return ((AbstractFurnaceMenuAccessor) handler).getInventory();
        } else if (handler instanceof CartographyTableMenu c) {
            return c.container;
        }
        return null;
    }

    public static InventoryType getBukkitType(AbstractContainerScreen<?> handler0) {
        AbstractContainerMenu handler = handler0.getMenu();
        if (handler != null) {
            MenuType<?> type = handler.getType();
            String id = BuiltInRegistries.MENU.getKey(type).getPath();
            switch (id) {
                case "anvil": {
                    return InventoryType.ANVIL;
                }
                case "beacon": {
                    return InventoryType.BEACON;
                }
                case "blast_furnace": {
                    return InventoryType.BLAST_FURNACE;
                }
                case "brewing_stand": {
                    return InventoryType.BREWING;
                }
                case "crafting": {
                    return InventoryType.CRAFTING;
                }
                case "enchantment": {
                    return InventoryType.ENCHANTING;
                }
                case "furnace": {
                    return InventoryType.FURNACE;
                }
                case "grindstone": {
                    return InventoryType.GRINDSTONE;
                }
                case "hopper": {
                    return InventoryType.HOPPER;
                }
                case "lectern": {
                    return InventoryType.LECTERN;
                }
                case "loom": {
                    return InventoryType.LOOM;
                }
                case "merchant": {
                    return InventoryType.MERCHANT;
                }
                case "shulker_box": {
                    return InventoryType.SHULKER_BOX;
                }
                case "smithing": {
                    return InventoryType.SMITHING;
                }
                case "smoker": {
                    return InventoryType.SMOKER;
                }
                case "cartography_table": {
                    return InventoryType.CARTOGRAPHY;
                }
                case "stonecutter": {
                    return InventoryType.STONECUTTER;
                }
                case "generic_3x3": {
                    return InventoryType.DISPENSER;
                }
            }
        }
        return InventoryType.CHEST;
    }
}
