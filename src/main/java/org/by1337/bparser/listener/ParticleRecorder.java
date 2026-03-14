package org.by1337.bparser.listener;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

    public ParticleRecorder() {
        NetworkEvent.PARTICLE.register(packet -> {
            if (!recording) return;
            if (startTime == -1) {
                startTime = System.nanoTime();
            }
            String schema;
            if (packet.getParticle() instanceof ColorParticleOption) {
                schema = "#schema=tick,type,pos,dist,speed,count,alwaysShow,extra_type,r,g,b,a";
            } else if (packet.getParticle() instanceof BlockParticleOption) {
                schema = "#schema=tick,type,pos,dist,speed,count,alwaysShow,extra_type,block";
            } else if (packet.getParticle() instanceof DustColorTransitionOptions) {
                schema = "#schema=tick,type,pos,dist,speed,count,alwaysShow,extra_type,size,r0,g0,b0,r1,g1,b1";
            } else if (packet.getParticle() instanceof DustParticleOptions) {
                schema = "#schema=tick,type,pos,dist,speed,count,alwaysShow,extra_type,r,g,b,size";
            } else if (packet.getParticle() instanceof ItemParticleOption) {
                schema = "#schema=tick,type,pos,dist,speed,count,alwaysShow,extra_type,item";
            } else if (packet.getParticle() instanceof SculkChargeParticleOptions) {
                schema = "#schema=tick,type,pos,dist,speed,count,alwaysShow,extra_type,roll";
            } else if (packet.getParticle() instanceof ShriekParticleOption) {
                schema = "#schema=tick,type,pos,dist,speed,count,alwaysShow,extra_type,delay";
            } else if (packet.getParticle() instanceof TrailParticleOption) {
                schema = "#schema=tick,type,pos,dist,speed,count,alwaysShow,extra_type,target,r,g,b,duration";
            }/*else if (packet.getParticle() instanceof VibrationParticleOption) {
                schema = "#schema=tick,type,pos,dist,speed,count,alwaysShow,extra_type,target,r,g,b,duration";
            }*/ else {
                schema = "#schema=tick,type,pos,dist,speed,count,alwaysShow,extra_type";
            }
            if (!Objects.equals(schema, lastSchema)) {
                lastSchema = schema;
                sb.append(schema).append("\n");
            }
            long nanos = System.nanoTime();
            long tick = (nanos - startTime) / TICK_NANOS;
            sb.append(tick).append(",");
            String type = BuiltInRegistries.PARTICLE_TYPE.getKey(packet.getParticle().getType()).toString();
            sb.append(type).append(",");
            sb
                    .append(packet.getX()).append(";")
                    .append(packet.getY()).append(";")
                    .append(packet.getZ()).append(",");

            sb
                    .append(packet.getXDist()).append(";")
                    .append(packet.getYDist()).append(";")
                    .append(packet.getZDist()).append(",");
            sb.append(packet.getMaxSpeed()).append(",");
            sb.append(packet.getCount()).append(",");
            sb.append(packet.isOverrideLimiter()).append(",");

            if (packet.getParticle() instanceof ColorParticleOption c) {
                //"r,g,b,a";
                sb.append("color,");
                sb
                        .append(c.getRed()).append(",")
                        .append(c.getGreen()).append(",")
                        .append(c.getBlue()).append(",")
                        .append(c.getAlpha()).append("\n");
            } else if (packet.getParticle() instanceof BlockParticleOption b) {
                //"block";
                sb.append("block,");
                sb.append(BuiltInRegistries.BLOCK.getKey(b.getState().getBlock())).append("\n");
            } else if (packet.getParticle() instanceof DustColorTransitionOptions dust) {
                // "size,r0,g0,b0,r1,g1,b1";
                sb.append("transition_dust,");
                var c0 = dust.getFromColor();
                var c1 = dust.getToColor();
                sb
                        .append(dust.getScale()).append(",")
                        .append(c0.x).append(",")
                        .append(c0.y).append(",")
                        .append(c0.z).append(",")
                        .append(c1.x).append(",")
                        .append(c1.y).append(",")
                        .append(c1.z).append("\n");
            } else if (packet.getParticle() instanceof DustParticleOptions dust) {
                //"#r,g,b,size";
                sb.append("dust,");
                var c = dust.getColor();
                sb
                        .append(c.x).append(",")
                        .append(c.y).append(",")
                        .append(c.z).append(",")
                        .append(dust.getScale()).append("\n");
            } else if (packet.getParticle() instanceof ItemParticleOption i) {
                //"item";
                sb.append("item,");
                sb.append(BuiltInRegistries.ITEM.getKey(i.getItem().getItem())).append("\n");
            } else if (packet.getParticle() instanceof SculkChargeParticleOptions s) {
                //"roll";
                sb.append("sculk,");
                sb.append(s.roll()).append("\n");
            } else if (packet.getParticle() instanceof ShriekParticleOption s) {
                //"delay";
                sb.append("shriek,");
                sb.append(s.getDelay()).append("\n");
            } else if (packet.getParticle() instanceof TrailParticleOption t) {
                // "target,r,g,b,duration";
                sb.append("trail,");
                var target = t.target();
                var color = ARGB.vector3fFromRGB24(t.color());
                sb
                        .append(target.x).append(";")
                        .append(target.y).append(";")
                        .append(target.z).append(",")
                        .append(color.x).append(",")
                        .append(color.y).append(",")
                        .append(color.z).append(",")
                        .append(t.duration()).append("\n");

            } else {
                sb.append("default\n");
            }
        });
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                LiteralArgumentBuilder.<FabricClientCommandSource>literal("//particle_record")
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("start")
                                .then(ClientCommandManager.argument("name", StringArgumentType.word())
                                        .executes(ctx -> {

                                            if (recording) {
                                                ctx.getSource().sendFeedback(Component.literal("Already recording"));
                                                return 0;
                                            }

                                            String name = StringArgumentType.getString(ctx, "name");

                                            recording = true;
                                            startTime = -1;
                                            lastSchema = null;
                                            currentName = name;
                                            sb.setLength(0);

                                            Minecraft mc = Minecraft.getInstance();
                                            BlockPos pos = mc.player.getOnPos();
                                            sb.append("#schema=type,offsets\n");
                                            sb.append("offsets,")
                                                    .append(pos.getX()).append(";")
                                                    .append(pos.getY()).append(";")
                                                    .append(pos.getZ()).append("\n");

                                            ctx.getSource().sendFeedback(Component.literal("Recording started: " + name));
                                            return 1;
                                        })
                                )
                                .executes(ctx -> {
                                    ctx.getSource().sendFeedback(Component.literal("use ///particle_record start <name>"));
                                    return 1;
                                })
                        )
                        .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("stop")
                                .executes(ctx -> {

                                    if (!recording) {
                                        ctx.getSource().sendFeedback(Component.literal("Not recording"));
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
                                                Component.literal(String.format("./mods/records/%s", file.getFileName().toString())),
                                                Component.literal("Record saved!"),
                                                new ItemStack(Items.LIME_DYE)
                                        );

                                        Minecraft.getInstance()
                                                .getToastManager()
                                                .addToast(toast);

                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }

                                    ctx.getSource().sendFeedback(Component.literal("Recording stopped"));
                                    currentName = null;

                                    return 1;
                                })
                        )
        );
    }
}