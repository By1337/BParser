package org.by1337.bparser.listener;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.by1337.bparser.event.NetworkEvent;
import org.by1337.bparser.toast.CustomToast;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ParticleRecorder {
    private static final long TICK_NANOS = TimeUnit.MILLISECONDS.toNanos(50);
    private boolean recording;
    private final StringBuilder sb = new StringBuilder();
    private String currentName;
    private long startTime = -1;
    private String lastSchema;

    public void register() {
        ClientCommandManager.DISPATCHER.register(
                LiteralArgumentBuilder.<FabricClientCommandSource>literal("//particle_record")
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("start")
                                .then(ClientCommandManager.argument("name", StringArgumentType.word())
                                        .executes(ctx -> {

                                            if (recording) {
                                                ctx.getSource().sendFeedback(new LiteralText("Already recording"));
                                                return 0;
                                            }

                                            String name = StringArgumentType.getString(ctx, "name");

                                            recording = true;
                                            startTime = -1;
                                            lastSchema = null;
                                            currentName = name;
                                            sb.setLength(0);

                                            MinecraftClient mc = MinecraftClient.getInstance();
                                            BlockPos pos = mc.player.getBlockPos();
                                            sb.append("#schema=type,offsets\n");
                                            sb.append("offsets,")
                                                    .append(pos.getX()).append(";")
                                                    .append(pos.getY()).append(";")
                                                    .append(pos.getZ()).append("\n");

                                            ctx.getSource().sendFeedback(new LiteralText("Recording started: " + name));
                                            return 1;
                                        })
                                )
                                .executes(ctx -> {
                                    ctx.getSource().sendFeedback(new LiteralText("use ///particle_record start <name>"));
                                    return 1;
                                })
                        )
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("stop")
                                .executes(ctx -> {

                                    if (!recording) {
                                        ctx.getSource().sendFeedback(new LiteralText("Not recording"));
                                        return 0;
                                    }

                                    recording = false;

                                    Path modsFolderPath = FabricLoader.getInstance().getGameDir().resolve("mods");
                                    Path folder = modsFolderPath.resolve("records");

                                    try {
                                        Files.createDirectories(folder);

                                        Path file = folder.resolve(currentName + ".txt");
                                        Files.write(file, sb.toString().getBytes(StandardCharsets.UTF_8));

                                        CustomToast toast = new CustomToast(
                                                new LiteralText(String.format("./mods/records/%s", file.getFileName().toString())),
                                                new LiteralText("Record saved!"),
                                                new ItemStack(Items.LIME_DYE)
                                        );

                                        MinecraftClient.getInstance()
                                                .getToastManager()
                                                .add(toast);

                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }

                                    ctx.getSource().sendFeedback(new LiteralText("Recording stopped"));
                                    currentName = null;

                                    return 1;
                                })
                        )
        );
        NetworkEvent.PARTICLE.register(packet -> {
            if (!recording || !MinecraftClient.getInstance().isOnThread()) return;
            if (startTime == -1){
                startTime = System.nanoTime();
            }
            String schema;
            if (packet.getParameters() instanceof BlockStateParticleEffect) {
                schema = "#schema=tick,type,pos,dist,speed,count,alwaysShow,extra_type,block";
            } else if (packet.getParameters() instanceof DustParticleEffect) {
                schema = "#schema=tick,type,pos,dist,speed,count,alwaysShow,extra_type,r,g,b,size";
            } else if (packet.getParameters() instanceof ItemStackParticleEffect) {
                schema = "#schema=tick,type,pos,dist,speed,count,alwaysShow,extra_type,item";
            } else {
                schema = "#schema=tick,type,pos,dist,speed,count,alwaysShow,extra_type";
            }
            if (!Objects.equals(schema, lastSchema)){
                lastSchema = schema;
                sb.append(schema).append("\n");
            }
            long nanos = System.nanoTime();
            long tick = (nanos - startTime) / TICK_NANOS;
            sb.append(tick).append(",");
            String type = Registry.PARTICLE_TYPE.getId(packet.getParameters().getType()).toString();
            sb.append(type).append(",");
            sb
                    .append(packet.getX()).append(";")
                    .append(packet.getY()).append(";")
                    .append(packet.getZ()).append(",");

            sb
                    .append(packet.getOffsetX()).append(";")
                    .append(packet.getOffsetY()).append(";")
                    .append(packet.getOffsetZ()).append(",");
            sb.append(packet.getSpeed()).append(",");
            sb.append(packet.getCount()).append(",");
            sb.append(packet.isLongDistance()).append(",");

            if (packet.getParameters() instanceof BlockStateParticleEffect) {
                sb.append("block,");
                BlockStateParticleEffect block = (BlockStateParticleEffect) packet.getParameters();
                String b = Registry.BLOCK.getId(block.getBlockState().getBlock()).toString();
                sb.append(b).append("\n");
            } else if (packet.getParameters() instanceof DustParticleEffect) {
                sb.append("dust,");
                DustParticleEffect dust = (DustParticleEffect) packet.getParameters();
                sb.append(dust.getRed()).append(",");
                sb.append(dust.getGreen()).append(",");
                sb.append(dust.getBlue()).append(",");
                sb.append(dust.getScale()).append("\n");
            } else if (packet.getParameters() instanceof ItemStackParticleEffect) {
                sb.append("item,");
                ItemStackParticleEffect itemStackParticleEffect = (ItemStackParticleEffect) packet.getParameters();
                String item = Registry.ITEM.getId(itemStackParticleEffect.getItemStack().getItem()).toString();
                sb.append(item).append("\n");
            } else {
                sb.append("default\n");
            }
        });
    }

}
