package org.by1337.bparser.inv.copy;

import com.google.common.base.Joiner;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.by1337.bparser.inv.ScreenUtil;
import org.by1337.bparser.text.ComponentUtil;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

public class MenuSaver {
    private final AbstractContainerScreen<?> screen;
    private final Container inventory;
    private final Map<String, InvItem> items = new HashMap<>();
    private final List<Map<Integer, String>> frames = new ArrayList<>();
    private final AlphabetNameGenerator alphabetNameGenerator = new AlphabetNameGenerator();
    private final UUID randomUUID = UUID.randomUUID();

    public MenuSaver(AbstractContainerScreen<?> screen, List<ScreenData> frames, Container inventory) {
        this.screen = screen;
        this.inventory = inventory;

        Map<InvItem, String> itemToId = new HashMap<>();

        for (ScreenData frame : frames) {
            this.frames.add(toFrameData(frame.content, itemToId));
        }
        this.frames.removeIf(Map::isEmpty);


        Map<Integer, String> matrix = new HashMap<>();

        for (Map<Integer, String> map : this.frames) {

            for (Integer i : new ArrayList<>(map.keySet())) {
                if (map.get(i).equals(matrix.get(i))) {
                    map.remove(i);
                } else {
                    matrix.put(i, map.get(i));
                }
            }
        }
        this.frames.removeIf(Map::isEmpty);
    }

    public String saveCurrentFrame() {
        items.clear();
        Map<InvItem, List<Integer>> itemToSlots = new HashMap<>();

        ScreenData data = new ScreenData(inventory);
        for (Integer i : data.content.keySet()) {
            InvItem item = data.content.get(i);
            if (item != InvItem.AIR) {
                itemToSlots.computeIfAbsent(item, k -> new ArrayList<>()).add(i);
            }
        }
        AlphabetNameGenerator generator = new AlphabetNameGenerator();
        for (InvItem item : itemToSlots.keySet()) {

            item.slots = itemToSlots.get(item);

            items.put(generator.nextName(item, items), item);
        }
        return saveNoAnimation(true);
    }

    public String saveNoAnimation(boolean setSlots) {
        StringBuilder sb = new StringBuilder();
        sb.append(Component.translatable("config.bparser.copyright").getString());

        sb.append("\nid: bparser:").append(randomUUID).append("\n");
        sb.append("provider: default\n");

        sb.append("type: ").append(ScreenUtil.getBukkitType(screen)).append("\n");
        sb.append("size: ").append(inventory.getContainerSize()).append("\n");


        sb.append("title: ")
                .append(quoteAndEscape(ComponentUtil.convert(screen.getTitle()))).append("\n");

        sb.append("items:\n");
        for (String id : items.keySet()) {
            InvItem item = items.get(id);
            if (item.itemStack.isEmpty()) {
                continue;
            }
            sb.append("  ").append(id).append(":\n");
            if (setSlots && !item.slots.isEmpty()) {
                if (item.slots.size() == 1) {
                    sb.append("\tslot: ").append(item.slots.get(0)).append("\n");
                } else {
                    sb.append("\tslot: ").append(Joiner.on(",").join(item.slots)).append("\n");
                }
            }
            ItemStack itemStack = item.itemStack;
            of(itemStack, DataComponents.MAX_STACK_SIZE, i -> {
                if (itemStack.getItem().getDefaultMaxStackSize() == i) return;
                sb.append("\tmax_stack_size: ").append(i).append("\n");
            });
            of(itemStack, DataComponents.DAMAGE, i -> {
                sb.append("\tdamage: ").append(i).append("\n");
            });
            of(itemStack, DataComponents.UNBREAKABLE, i -> {
                sb.append("\tunbreakable: true");
            });
            any(itemStack, DataComponents.CUSTOM_NAME, DataComponents.ITEM_NAME, c -> {
                sb.append("   #name: ").append(quoteAndEscape(ComponentUtil.convert(c, true))).append("\n");
                sb.append("\tname: ").append(quoteAndEscape(ComponentUtil.convert(c))).append("\n");
            });
            of(itemStack, DataComponents.LORE, lore -> {
                sb.append("\tlore:");
                toList("\t  ", lore.lines(), sb, ComponentUtil::convert);
            });
            of(itemStack, DataComponents.CUSTOM_MODEL_DATA, model -> {
                sb.append("\tmodel_data:\n");
                sb.append("\t  floats:");
                toList("\t\t", model.floats(), sb, b -> b);
                sb.append("\t  flags:");
                toList("\t\t", model.flags(), sb, b -> b);
                sb.append("\t  colors:");
                toList("\t\t", model.colors(), sb, MenuSaver::toHexEscaped);
                sb.append("\t  strings:");
                toList("\t\t", model.strings(), sb, MenuSaver::quoteAndEscape);
            });
            AtomicBoolean hasColor = new AtomicBoolean();
            of(itemStack, DataComponents.DYED_COLOR, color -> {
                hasColor.set(true);
                sb.append("\tcolor: ").append(toHexEscaped(color.rgb()));
            });
            of(itemStack, DataComponents.MAP_COLOR, color -> {
                if (hasColor.get()) sb.append("\t#map_color ");
                sb.append("\tcolor: ").append(toHexEscaped(color.rgb()));
            });
            // это ARGB хз куда его
            // of(itemStack, DataComponents.BASE_COLOR, color -> {
            //     if (hasColor.get()) sb.append("\t#base_color ");
            //     sb.append("\tcolor: ").append(toHexEscaped(color.()));
            // });
            of(itemStack, DataComponents.POTION_CONTENTS, content -> {
                var c = content.customColor();
                if (c.isPresent()) {
                    if (hasColor.get()) sb.append("\t#potion-customColor ");
                    //todo это тоже ARGB
                    sb.append("\tcolor: ").append(toHexEscaped(c.get()));
                }
                //potion_effects:
                //  glowing: 10 10
                //  slow_falling: <duration> <amplifier>
                //color: '#rrggbb' # для зелий и кожаной брони
                var effects = content.customEffects();
                if (!effects.isEmpty()) {
                    sb.append("\tpotion_effects:\n");
                    for (MobEffectInstance effect : effects) {
                        var ef = BuiltInRegistries.MOB_EFFECT.getKey(effect.getEffect().value()).getPath();
                        sb.append("\t  ").append(ef).append(": ").append(effect.getDuration()).append(" ").append(effect.getAmplifier()).append("\n");
                    }
                }
            });
            of(itemStack, DataComponents.ENCHANTMENTS, enchantments -> {
                if (enchantments.isEmpty()) return;
                //enchantments:
                //  protection: 1
                int idx = sb.length();
                sb.append("\tenchantments:\n");
                int size = 0;
                for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
                    var key = entry.getKey().unwrapKey();
                    if (key.isPresent()) {
                        sb.append("\t  ").append(key.get().location().getPath()).append(": ").append(entry.getIntValue()).append("\n");
                        size++;
                    }
                }
                if (size == 0) {
                    sb.setLength(idx);
                }
            });
            of(itemStack, DataComponents.TOOLTIP_DISPLAY, tooltip -> {
                if (tooltip.hideTooltip()) {
                    sb.append("\thide_tooltip: true\n");
                }
                for (DataComponentType<?> component : tooltip.hiddenComponents()) {
                    var v = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(component).getPath();
                    sb.append("\t#hide -> ").append(v).append("\n");
                }
            });
            AtomicBoolean hasMaterial = new AtomicBoolean();
            of(itemStack, DataComponents.PROFILE, profile -> {
                if (profile.properties().containsKey("textures")) {
                    var data = profile.properties().get("textures").iterator().next();
                    sb.append("\tmaterial: ").append(quoteAndEscape("basehead-" + data.value())).append("\n");
                    hasMaterial.set(true);
                }
            });
            if (item.itemStack.getCount() != 1) {
                sb.append("\tamount: ").append(item.itemStack.getCount()).append("\n");
            }
            if (!hasMaterial.get()) {
                String material = BuiltInRegistries.ITEM.getKey(item.itemStack.getItem()).getPath();
                sb.append("\tmaterial: ").append(material).append("\n");
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    private static <T> void toList(String space, Iterable<T> t, StringBuilder sb, Function<T, Object> serializer) {
        var iterator = t.iterator();
        int size = 0;
        while (iterator.hasNext()) {
            T val = iterator.next();
            size++;
            if (size == 1) {
                if (!iterator.hasNext()) {
                    sb.append(" ").append(serializer.apply(val)).append("\n");
                    break;
                } else {
                    sb.append("\n");
                }
            }
            sb.append(space).append("- ").append(serializer.apply(val)).append("\n");
        }
        if (size == 0) {
            sb.append(" []\n");
        }
    }

    private static <T> void of(ItemStack itemStack, DataComponentType<T> type, Consumer<T> applier) {
        T t = itemStack.get(type);
        if (t != null) {
            applier.accept(t);
        }
    }

    private static <T> void any(ItemStack itemStack, DataComponentType<T> t, DataComponentType<T> t1, Consumer<T> applier) {
        T v = itemStack.get(t);
        if (v != null) {
            applier.accept(v);
        } else {
            v = itemStack.get(t1);
            if (v != null) {
                applier.accept(v);
            }
        }
    }

    private static String toHexEscaped(int rgb) {
        return "\"" + toHex(rgb) + "\"";
    }

    private static String toHex(int rgb) {
        int BIT_MASK = 0xff;
        return String.format(
                "#%02X%02X%02X",
                rgb >> 16 & BIT_MASK,
                rgb >> 8 & BIT_MASK,
                rgb >> 0 & BIT_MASK
        );
    }

    public String save() {
        StringBuilder sb = new StringBuilder(saveNoAnimation(false));
        Set<String> airs = new HashSet<>();
        for (String id : items.keySet()) {
            if (items.get(id).itemStack.isEmpty()) {
                airs.add(id);
            }
        }

        Map<Integer, String> nonAirSlots = new HashMap<>();
        sb.append("animation:\n");
        int tick = 0;
        StringBuilder frameBuilder = new StringBuilder();
        for (Map<Integer, String> frame : frames) {
            frameBuilder.append("  - tick: ").append(tick++).append("\n");
            frameBuilder.append("    opcodes:\n");

            Map<String, List<Integer>> inverse = new HashMap<>();
            frame.forEach((k, v) -> inverse.computeIfAbsent(v, ignore -> new ArrayList<>()).add(k));

            boolean isEmpty = true;

            for (String item : inverse.keySet()) {
                List<Integer> slots = inverse.get(item);
                if (airs.contains(item)) {
                    slots.removeIf(s -> !nonAirSlots.containsKey(s));
                    if (slots.isEmpty()) continue;
                    frameBuilder.append("      - remove: ");
                    slots.forEach(nonAirSlots::remove);
                    isEmpty = false;
                } else {
                    frameBuilder.append("      - set: ").append(item).append(" ");
                    slots.forEach(s -> nonAirSlots.put(s, item));
                    isEmpty = false;
                }
                frameBuilder.append(Joiner.on(",").join(slots)).append("\n");
            }
            if (!isEmpty) {
                sb.append(frameBuilder);
            } else {
                tick--;
            }
            frameBuilder.setLength(0);
        }
        return sb.toString();
    }

    public UUID getRandomUUID() {
        return randomUUID;
    }

    public enum ItemFlag {
        HIDE_ENCHANTS,
        HIDE_ATTRIBUTES,
        HIDE_UNBREAKABLE,
        HIDE_DESTROYS,
        HIDE_PLACED_ON,
        HIDE_POTION_EFFECTS,
        HIDE_DYE;
    }

    public static String quoteAndEscape(String raw) {
        StringBuilder result = new StringBuilder(" ");
        int quoteChar = 0;
        for (int i = 0; i < raw.length(); ++i) {
            char currentChar = raw.charAt(i);
            switch (currentChar) {
                case '\\':
                    result.append("\\\\");
                    break;
                case '\n':
                    result.append("\\n");
                    break;
                case '\t':
                    result.append("\\t");
                    break;
                case '\b':
                    result.append("\\b");
                    break;
                case '\r':
                    result.append("\\r");
                    break;
                case '\f':
                    result.append("\\f");
                    break;
                case '\"':
                case '\'':
                    if (quoteChar == 0) {
                        quoteChar = currentChar == '\"' ? '\'' : '\"';
                    }
                    if (quoteChar == currentChar) {
                        result.append('\\');
                    }
                    result.append(currentChar);
                    break;
                default:
                    result.append(currentChar);
            }
        }
        if (quoteChar == 0) {
            quoteChar = '\"';
        }
        result.setCharAt(0, (char) quoteChar);
        result.append((char) quoteChar);
        return result.toString();
    }

    private Map<Integer, String> toFrameData(Map<Integer, InvItem> content, Map<InvItem, String> itemToId) {
        Map<Integer, String> frameData = new HashMap<>();
        for (Integer slot : content.keySet()) {
            InvItem item = content.get(slot);
            String itemId = itemToId.get(item);
            if (itemId == null) {
                itemId = alphabetNameGenerator.nextName(null, null);
                items.put(itemId, item);
                itemToId.put(item, itemId);
            }
            frameData.put(slot, itemId);
        }
        return frameData;
    }


    private static class AlphabetNameGenerator {
        private static final List<String> alphabet;

        static {
            alphabet = new ArrayList<>();
            alphabet.add("a");
            alphabet.add("b");
            alphabet.add("c");
            alphabet.add("d");
            alphabet.add("e");
            alphabet.add("f");
            alphabet.add("g");
            alphabet.add("h");
            alphabet.add("i");
            alphabet.add("j");
            alphabet.add("k");
            alphabet.add("l");
            alphabet.add("m");
            alphabet.add("n");
            alphabet.add("o");
            alphabet.add("p");
            alphabet.add("q");
            alphabet.add("r");
            alphabet.add("s");
            alphabet.add("t");
            alphabet.add("u");
            alphabet.add("v");
            alphabet.add("w");
            alphabet.add("x");
            alphabet.add("y");
            alphabet.add("z");
        }

        private static final Object lock = new Object();
        private final char[] symbols = Joiner.on("").join(alphabet).toCharArray();
        private long currentPosition = 0;

        public String nextName(@Nullable InvItem item, @Nullable Map<String, InvItem> map) {
            if (item != null && map != null && item.extra != null) {
                String name = extractName(item.extra);
                if (name != null) {
                    String s = name;
                    while (map.containsKey(name)) {
                        name = s + "-" + nextName(null, null);
                    }
                    return name;
                }
            }
            synchronized (lock) {
                StringBuilder combination = new StringBuilder();
                long position = currentPosition;
                for (int i = 0; i < symbols.length; i++) {
                    int charIndex = (int) ((position + i) % symbols.length);
                    combination.append(symbols[charIndex]);
                    position /= symbols.length;
                    if (position <= 0) break;
                }
                currentPosition++;
                return combination.toString();
            }
        }

        private static final List<NbtPath> COMPOUNDS = Util.make(() -> {
            List<NbtPath> list = new ArrayList<>();
            list.add(new NbtPath("kringeItems", "type", "hw-kringeItems-"));
            list.add(new NbtPath("pyrotechnic-item", "name", "hw-pyrotechnic-"));
            list.add(new NbtPath("PublicBukkitValues", "minecraft:ftid", "ft-ftid-"));
            list.add(new NbtPath("PublicBukkitValues", "eggkeeper:isfragment", "hw-eggkeeper-"));
            list.add(new NbtPath("PublicBukkitValues", "advancedserverselecter:server", "hw-server-"));
            list.add(new NbtPath("PublicBukkitValues", "advancedserverselecter:server-type", "hw-server-"));
            list.add(new NbtPath("@", "SpecialEgg_Pattern", "hw-SpecialEgg-"));
            return list;
        });

        private @Nullable String extractName(CompoundTag fullNBT) {
            for (NbtPath path : COMPOUNDS) {
                CompoundTag compound;
                if (path.compound.equals("@")) {
                    compound = fullNBT;
                } else {
                    var opt = fullNBT.getCompound(path.compound);
                    if (opt.isPresent()) {
                        compound = opt.get();
                    } else {
                        continue;
                    }
                }
                var opt = compound.getString(path.value);
                if (opt.isPresent())
                    return path.prefix + opt.get();
            }
            return null;
        }

        private static class NbtPath {
            private final String compound;
            private final String value;
            private final String prefix;

            public NbtPath(String compound, String value, String prefix) {
                this.compound = compound;
                this.value = value;
                this.prefix = prefix;
            }
        }
    }
}
