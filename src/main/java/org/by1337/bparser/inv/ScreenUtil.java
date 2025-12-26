package org.by1337.bparser.inv;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.registry.Registries;
import net.minecraft.screen.*;
import org.by1337.bparser.mixin.*;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class ScreenUtil {

    @Nullable
    public static Inventory getInventory(HandledScreen<?> handler0) {
        Object handler = handler0.getScreenHandler();
        if (handler instanceof GenericContainerScreenHandler) {
            return ((GenericContainerScreenHandler) handler).getInventory();
        } else if (handler instanceof Generic3x3ContainerScreenHandler) {
            return ((Generic3x3ContainerScreenHandlerAccessor) handler).getInventory();
        } else if (handler instanceof BeaconScreenHandler) {
            return ((BeaconScreenHandlerAccessor) handler).getInventory();
        } else if (handler instanceof BlastFurnaceScreenHandler) {
            return ((AbstractFurnaceScreenHandlerAccessor) handler).getInventory();
        } else if (handler instanceof BrewingStandScreenHandler) {
            return ((BrewingStandScreenHandlerAccessor) handler).getInventory();
        } else if (handler instanceof CraftingScreenHandler) {
            return ((CraftingScreenHandlerAccessor) handler).getInventory();
        } else if (handler instanceof EnchantmentScreenHandler) {
            return ((EnchantmentScreenHandlerAccessor) handler).getInventory();
        } else if (handler instanceof FurnaceScreenHandler) {
            return ((AbstractFurnaceScreenHandlerAccessor) handler).getInventory();
        } else if (handler instanceof HopperScreenHandler) {
            return ((HopperScreenHandlerAccessor) handler).getInventory();
        } else if (handler instanceof LecternScreenHandler) {
            return ((LecternScreenHandlerAccessor) handler).getInventory();
        } else if (handler instanceof MerchantScreenHandler) {
            return ((MerchantScreenHandlerAccessor) handler).getInventory();
        } else if (handler instanceof ShulkerBoxScreenHandler) {
            return ((ShulkerBoxScreenHandlerAccessor) handler).getInventory();
        } else if (handler instanceof SmokerScreenHandler) {
            return ((AbstractFurnaceScreenHandlerAccessor) handler).getInventory();
        } else if (handler instanceof CartographyTableScreenHandler c) {
            return c.inventory;
        }
        return null;
    }

    public static InventoryType getBukkitType(HandledScreen<?> handler0) {
        ScreenHandler handler = handler0.getScreenHandler();
        if (handler != null) {
            ScreenHandlerType<?> type = handler.getType();
            String id = Registries.SCREEN_HANDLER.getId(type).getPath();
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

    @SuppressWarnings("unchecked")
    private static <T> T getField(Class<?> c, String field, Object from) {
        try {
            Field f = c.getDeclaredField(field);
            f.setAccessible(true);
            return (T) f.get(from);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
