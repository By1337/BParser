package org.by1337.bparser.schem;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SchemSaver {
    private final SchemSelector.Region region;
    private final Path schemFolder = FabricLoader.getInstance().getGameDir().resolve("mods/schems");
    private final Minecraft mc = Minecraft.getInstance();
    private static final int MAX_SIZE = Short.MAX_VALUE - Short.MIN_VALUE;

    public SchemSaver(SchemSelector.Region region) {
        this.region = region;
    }

    public void save(String name) {
        LocalPlayer player = Objects.requireNonNull(mc.player);
        var pos = player.getOnPos();
        Vec3i origin = new Vec3i(pos.getX(), pos.getY(), pos.getZ());
        Vec3i min = new Vec3i(region.minX, region.minY, region.minZ);
        Vec3i offset = new BlockPos(min.getX() - origin.getX(), min.getY() - origin.getY(), min.getZ() - origin.getZ());

        int width = Math.abs(region.maxX - region.minX);
        int height = Math.abs(region.maxY - region.minY);
        int length = Math.abs(region.maxZ - region.minZ);

        if (width > MAX_SIZE) {
            throw new IllegalArgumentException("Width of region too large for a .schematic");
        }
        if (height > MAX_SIZE) {
            throw new IllegalArgumentException("Height of region too large for a .schematic");
        }
        if (length > MAX_SIZE) {
            throw new IllegalArgumentException("Length of region too large for a .schematic");
        }

        CompoundTag compound = new CompoundTag();
        compound.putInt("DataVersion", SharedConstants.getCurrentVersion().dataVersion().version());
        compound.putInt("Version", 3);

        compound.putIntArray("Offset", new int[]{offset.getX(), offset.getY(), offset.getZ()});

        compound.putShort("Width", (short) width);
        compound.putShort("Height", (short) height);
        compound.putShort("Length", (short) length);

        compound.put("Metadata", warp(
                "CreatedByMod", StringTag.valueOf("BParser"),
                "ModAuthor", StringTag.valueOf("By1337"),
                "Name", StringTag.valueOf(name),
                "WorldEdit", warp(
                        "Origin", new IntArrayTag(new int[]{origin.getX(), origin.getY(), origin.getZ()})
                ),
                "Date", LongTag.valueOf(System.currentTimeMillis())
        ));


        ListTag tileEntities = new ListTag();

        int paletteMax = 0;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(width * height * length);
        Map<String, Integer> palette = new HashMap<>();

        for (int y = 0; y < height; y++) {
            int y0 = min.getY() + y;
            for (int z = 0; z < length; z++) {
                int z0 = min.getZ() + z;
                for (int x = 0; x < width; x++) {
                    int x0 = min.getX() + x;
                    BlockPos point = new BlockPos(x0, y0, z0);
                    BlockState state = mc.level.getBlockState(point);

                    BlockEntity blockEntity = mc.level.getBlockEntity(point);
                    if (blockEntity != null) {
                        CompoundTag data = blockEntity.saveWithFullMetadata(mc.level.registryAccess());
                        data.remove("id"); // Remove 'id' if it exists. We want 'Id'
                        // Positions are kept in NBT, we don't want that.
                        data.remove("x");
                        data.remove("y");
                        data.remove("z");

                        CompoundTag result = new CompoundTag();
                        var identifier = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType());
                        result.putString("Id", identifier.toString());
                        result.putIntArray("Pos", new int[]{x, y, z});
                        result.put("Data", data);
                        tileEntities.add(result);
                    }

                    String blockKey = state.toString().replace("Block{", "").replace("}", "");
                    int blockId;
                    if (palette.containsKey(blockKey)) {
                        blockId = palette.get(blockKey);
                    } else {
                        blockId = paletteMax;
                        palette.put(blockKey, blockId);
                        paletteMax++;
                    }

                    while ((blockId & -128) != 0) {
                        buffer.write(blockId & 127 | 128);
                        blockId >>>= 7;
                    }
                    buffer.write(blockId);
                }
            }
        }
        CompoundTag paletteTag = new CompoundTag();
        palette.forEach(paletteTag::putInt);

        compound.put("Blocks", warp(
                "Palette", paletteTag,
                "Data", new ByteArrayTag(buffer.toByteArray()),
                "BlockEntities", tileEntities
        ));

        try {
            schemFolder.toFile().mkdirs();
            NbtIo.writeCompressed(warp("Schematic", compound), new File(schemFolder.toFile(), name).toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static CompoundTag warp(String key, Tag tag) {
        CompoundTag c = new CompoundTag();
        c.put(key, tag);
        return c;
    }

    private static CompoundTag warp(
            String k, Tag t,
            String k1, Tag t1
    ) {
        CompoundTag c = new CompoundTag();
        c.put(k, t);
        c.put(k1, t1);
        return c;
    }

    private static CompoundTag warp(
            String k, Tag t,
            String k1, Tag t1,
            String k2, Tag t2
    ) {
        CompoundTag c = new CompoundTag();
        c.put(k, t);
        c.put(k1, t1);
        c.put(k2, t2);
        return c;
    }

    private static CompoundTag warp(
            String k, Tag t,
            String k1, Tag t1,
            String k2, Tag t2,
            String k3, Tag t3
    ) {
        CompoundTag c = new CompoundTag();
        c.put(k, t);
        c.put(k1, t1);
        c.put(k2, t2);
        c.put(k3, t3);
        return c;
    }

    private static CompoundTag warp(
            String k, Tag t,
            String k1, Tag t1,
            String k2, Tag t2,
            String k3, Tag t3,
            String k4, Tag t4
    ) {
        CompoundTag c = new CompoundTag();
        c.put(k, t);
        c.put(k1, t1);
        c.put(k2, t2);
        c.put(k3, t3);
        c.put(k4, t4);
        return c;
    }
}
