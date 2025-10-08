package org.by1337.bparser.inv;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.*;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class ScreenUtil {

    @Nullable
    public static Inventory getInventory(HandledScreen<?> handler0) {
        Object handler = handler0.getScreenHandler();
        // handler.getType()
        if (handler instanceof GenericContainerScreenHandler) {
            return ((GenericContainerScreenHandler) handler).getInventory();
        } else if (handler instanceof Generic3x3ContainerScreenHandler) {
            return getField(Generic3x3ContainerScreenHandler.class, "field_7806", handler);//inventory
        }/* else if (handler instanceof AnvilScreenHandler) {
            return getField(ForgingScreenHandler.class, "input", handler);
        }*/ else if (handler instanceof BeaconScreenHandler) {
            return getField(BeaconScreenHandler.class, "field_17287", handler);//payment
        } else if (handler instanceof BlastFurnaceScreenHandler) {
            return getField(AbstractFurnaceScreenHandler.class, "field_7824", handler);//inventory
        } else if (handler instanceof BrewingStandScreenHandler) {
            return getField(BrewingStandScreenHandler.class, "field_7788", handler);
        } else if (handler instanceof CraftingScreenHandler) {
            return getField(CraftingScreenHandler.class, "field_7801", handler);//input
        } else if (handler instanceof EnchantmentScreenHandler) {
            return getField(EnchantmentScreenHandler.class, "field_7809", handler);//inventory
        } else if (handler instanceof FurnaceScreenHandler) {
            return getField(AbstractFurnaceScreenHandler.class, "field_7824", handler);
        }/*else if (handler instanceof GrindstoneScreenHandler) {
            return getField(GrindstoneScreenHandler.class, "inventory", handler);
        }*/ else if (handler instanceof HopperScreenHandler) {
            return getField(HopperScreenHandler.class, "field_7826", handler);//inventory
        } else if (handler instanceof LecternScreenHandler) {
            return getField(LecternScreenHandler.class, "field_17313", handler);//inventory
        }/*else if (handler instanceof LoomScreenHandler) {
            return getField(LoomScreenHandler.class, "inventory", handler);
        }*/ else if (handler instanceof MerchantScreenHandler) {
            return getField(MerchantScreenHandler.class, "field_7863", handler);//merchantInventory
        } else if (handler instanceof ShulkerBoxScreenHandler) {
            return getField(ShulkerBoxScreenHandler.class, "field_7867", handler);//inventory
        }/*else if (handler instanceof SmithingScreenHandler) {
            return getField(SmithingScreenHandler.class, "inventory", handler);
        }*/ else if (handler instanceof SmokerScreenHandler) {
            return getField(AbstractFurnaceScreenHandler.class, "field_7824", handler);
        } else if (handler instanceof CartographyTableScreenHandler) {
            return getField(CartographyTableScreenHandler.class, "field_17293", handler);//inventory
        }
        return null;
    }

    public static InventoryType getBukkitType(HandledScreen<?> handler0) {
        ScreenHandler handler = handler0.getScreenHandler();
        if (handler != null) {
            ScreenHandlerType<?> type = handler.getType();
            String id = Registry.SCREEN_HANDLER.getId(type).getPath();
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
