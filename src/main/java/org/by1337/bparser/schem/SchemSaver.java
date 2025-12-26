package org.by1337.bparser.schem;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

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
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private static final int MAX_SIZE = Short.MAX_VALUE - Short.MIN_VALUE;

    public SchemSaver(SchemSelector.Region region) {
        this.region = region;
    }

    public void save(String name) {
        PlayerEntity player = Objects.requireNonNull(mc.player);
        var pos = player.getBlockPos();
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

        NbtCompound compound = new NbtCompound();
        compound.putInt("DataVersion", SharedConstants.getGameVersion().getSaveVersion().getId());
        compound.putInt("Version", 2);

        compound.putIntArray("Offset", new int[]{min.getX(), min.getY(), min.getZ()});

        compound.putShort("Width", (short) width);
        compound.putShort("Height", (short) height);
        compound.putShort("Length", (short) length);


        NbtCompound metadata = new NbtCompound();
        metadata.putString("CreatedByMod", "BParser");
        metadata.putString("ModAuthor", "By1337");
        metadata.putString("Name", name);
        metadata.putInt("WEOffsetX", offset.getX());
        metadata.putInt("WEOffsetY", offset.getY());
        metadata.putInt("WEOffsetZ", offset.getZ());

        compound.put("Metadata", metadata);


        NbtList tileEntities = new NbtList();

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
                    BlockState state = mc.world.getBlockState(point);

                    BlockEntity blockEntity = mc.world.getBlockEntity(point);
                    if (blockEntity != null) {
                        NbtCompound data =blockEntity.createNbt();
                        data.remove("id"); // Remove 'id' if it exists. We want 'Id'
                        // Positions are kept in NBT, we don't want that.
                        data.remove("x");
                        data.remove("y");
                        data.remove("z");

                        Identifier identifier = BlockEntityType.getId(blockEntity.getType());
                        data.putString("Id", identifier.toString());
                        data.putIntArray("Pos", new int[]{x, y, z});
                        tileEntities.add(data);
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
        compound.putInt("PaletteMax", paletteMax);
        NbtCompound paletteTag = new NbtCompound();
        palette.forEach(paletteTag::putInt);
        compound.put("Palette", paletteTag);
        compound.putByteArray("BlockData", buffer.toByteArray());
        compound.put("BlockEntities", tileEntities);

        try {
            schemFolder.toFile().mkdirs();
            NbtIo.writeCompressed(compound, new File(schemFolder.toFile(), name).toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
