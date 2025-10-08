package org.by1337.bparser.schem;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.by1337.bparser.render.RenderUtil;
import org.lwjgl.opengl.GL11;

public class SchemSelector {
    private static final int MAX_SIZE = 16 * 40;

    private boolean enabled = false;
    private BlockPos pos1;
    private BlockPos pos2;

    public void register() {
        ClientCommandManager.DISPATCHER.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("//schem")
                .executes(ctx -> {
                            ctx.getSource().sendFeedback(new TranslatableText("lang.bparser.select.usage"));
                            return 1;
                        }
                )
                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("select")
                        .executes(ctx -> {
                            enabled = !enabled;
                            if (enabled) {
                                ctx.getSource().sendFeedback(new TranslatableText("lang.bparser.select.on"));
                            } else {
                                pos1 = null;
                                pos2 = null;
                                ctx.getSource().sendFeedback(new TranslatableText("lang.bparser.select.off"));
                            }
                            return 1;
                        })
                )
                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("pos1")
                        .executes(ctx -> {
                            enabled = true;
                            PlayerEntity player = MinecraftClient.getInstance().player;
                            pos1 = player.getBlockPos();
                            fixSize();
                            return 1;
                        })
                )

                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("pos2")
                        .executes(ctx -> {
                            enabled = true;
                            PlayerEntity player = MinecraftClient.getInstance().player;
                            pos2 = player.getBlockPos();
                            fixSize();
                            return 1;
                        })
                )
                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("save")
                        .then(ClientCommandManager.argument("name", StringArgumentType.string())
                                .executes(ctx -> {
                                    String name = StringArgumentType.getString(ctx, "name");
                                    if (pos1 != null && pos2 != null) {
                                        try {
                                            new SchemSaver(new Region(pos1, pos2)).save(name + ".schem");
                                        } catch (Throwable t) {
                                            t.printStackTrace();
                                            ctx.getSource().sendFeedback(new TranslatableText("lang.bparser.save.failed.error", t.getMessage()));
                                            return 1;
                                        }
                                        ctx.getSource().sendFeedback(new TranslatableText("lang.bparser.save.successfully"));
                                    } else {
                                        ctx.getSource().sendFeedback(new TranslatableText("lang.bparser.save.failed.norg"));
                                    }
                                    return 1;
                                })
                        )
                )
                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("up")
                        .then(ClientCommandManager.argument("blocks", IntegerArgumentType.integer())
                                .executes(ctx -> {
                                    int blocks = IntegerArgumentType.getInteger(ctx, "blocks");
                                    if (pos1 != null && pos2 != null) {
                                        Region region = new Region(pos1, pos2);
                                        if (blocks > 0) {
                                            region.maxY += blocks;
                                            region.resize();
                                            ctx.getSource().sendFeedback(new TranslatableText("lang.bparser.expand.up", blocks));
                                        } else {
                                            region.minY -= blocks;
                                            region.resize();
                                            ctx.getSource().sendFeedback(new TranslatableText("lang.bparser.expand.down", blocks));
                                        }
                                        pos1 = new BlockPos(region.minX, region.minY, region.minZ);
                                        pos2 = new BlockPos(region.maxX, region.maxY, region.maxZ);
                                        fixSize();
                                    } else {
                                        ctx.getSource().sendFeedback(new TranslatableText("lang.bparser.save.failed.norg"));
                                    }
                                    return 1;
                                })
                        )
                )
                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("forward")
                        .then(ClientCommandManager.argument("blocks", IntegerArgumentType.integer())
                                .executes(ctx -> {
                                    int blocks = IntegerArgumentType.getInteger(ctx, "blocks");
                                    if (pos1 != null && pos2 != null) {
                                        ClientPlayerEntity player = MinecraftClient.getInstance().player;
                                        if (player != null) {
                                            Region region = new Region(pos1, pos2);
                                            Vec3d lookDirection = player.getRotationVec(1.0f).normalize();
                                            if (Math.abs(lookDirection.y) > Math.abs(lookDirection.x) && Math.abs(lookDirection.y) > Math.abs(lookDirection.z)) {
                                                if (lookDirection.y < 0) {
                                                    region.minY -= blocks;
                                                } else {
                                                    region.maxY += blocks;
                                                }
                                            } else if (Math.abs(lookDirection.x) > Math.abs(lookDirection.z)) {
                                                if (lookDirection.x > 0) {
                                                    region.maxX += blocks;
                                                } else {
                                                    region.minX -= blocks;
                                                }
                                            } else {
                                                if (lookDirection.z > 0) {
                                                    region.maxZ += blocks;
                                                } else {
                                                    region.minZ -= blocks;
                                                }
                                            }
                                            ctx.getSource().sendFeedback(new TranslatableText("lang.bparser.expand.forward", blocks));
                                            region.resize();
                                            pos1 = new BlockPos(region.minX, region.minY, region.minZ);
                                            pos2 = new BlockPos(region.maxX, region.maxY, region.maxZ);
                                            fixSize();
                                        }
                                    } else {
                                        ctx.getSource().sendFeedback(new TranslatableText("lang.bparser.save.failed.norg"));
                                    }
                                    return 1;
                                })
                        )
                )
        );

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (!enabled) return ActionResult.PASS;
            pos1 = pos;
            if (pos2 == null) pos2 = pos;
            return ActionResult.PASS;
        });
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!enabled) return ActionResult.PASS;
            BlockPos pos = hitResult.getBlockPos();
            pos2 = pos;
            if (pos1 == null) pos1 = pos;
            return ActionResult.PASS;
        });
        WorldRenderEvents.LAST.register(this::onRenderWorld);
    }

    private void onRenderWorld(WorldRenderContext context) {
        if (!enabled) return;
        if (pos1 != null && pos2 != null) {
            Vec3d cameraPos = context.camera().getPos();
            Region region = new Region(pos1, pos2);

            double minX = region.minX;
            double minY = region.minY;
            double minZ = region.minZ;
            double maxX = region.maxX + 1;
            double maxY = region.maxY + 1;
            double maxZ = region.maxZ + 1;

            {
                Box box = new Box(minX, minY, minZ, maxX, maxY, maxZ);
                Box shiftedBox = box.offset(-cameraPos.x, -cameraPos.y, -cameraPos.z);
                renderRegion(shiftedBox);
                RenderUtil.drawBox(shiftedBox, 1f, 0, 0, 1, 3.f);
            }
            {
                Box box = new Box(pos1.getX() + 0.3, pos1.getY() + 0.3, pos1.getZ() + 0.3, pos1.getX() + 0.7, pos1.getY() + 0.7, pos1.getZ() + 0.7);
                Box shiftedBox = box.offset(-cameraPos.x, -cameraPos.y, -cameraPos.z);
                RenderUtil.drawBox(shiftedBox, 0f, 1, 0, 1);
            }
            {
                Box box = new Box(pos2.getX() + 0.3, pos2.getY() + 0.3, pos2.getZ() + 0.3, pos2.getX() + 0.7, pos2.getY() + 0.7, pos2.getZ() + 0.7);
                Box shiftedBox = box.offset(-cameraPos.x, -cameraPos.y, -cameraPos.z);
                RenderUtil.drawBox(shiftedBox, 0f, 1, 0, 1);
            }

        }
    }

    private void fixSize() {
        Region region = new Region(any(pos1, pos2), any(pos2, pos1));

        int width = Math.abs(region.maxX - region.minX);
        int length = Math.abs(region.maxZ - region.minZ);

        if (width > MAX_SIZE) {
            region.maxX = region.minX + MAX_SIZE;
        }
        if (length > MAX_SIZE) {
            region.maxZ = region.minZ + MAX_SIZE;
        }

        if (region.maxY > 255) {
            region.maxY = 255;
        }
        if (region.minY < 0) {
            region.minY = 0;
        }

        pos1 = new BlockPos(region.minX, region.minY, region.minZ);
        pos2 = new BlockPos(region.maxX, region.maxY, region.maxZ);
    }

    private <T> T any(T o, T o1) {
        return o != null ? o : o1;
    }

    private void renderRegion(Box box) {

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glLineWidth(2.0f);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.disableDepthTest();

        RenderSystem.disableTexture();

        RenderSystem.color4f(0.6f, 04f, 0.7f, 0.3f);

        bufferBuilder.begin(GL11.GL_LINES, VertexFormats.POSITION);

        drawBoxWithGrid(bufferBuilder, box, 1);

        tessellator.draw();

        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    public static void drawBoxWithGrid(BufferBuilder bufferBuilder, Box box, double step) {
        double minX = box.minX;
        double minY = box.minY;
        double minZ = box.minZ;
        double maxX = box.maxX;
        double maxY = box.maxY;
        double maxZ = box.maxZ;

        for (double z = minZ; z <= maxZ; z += step) {
            bufferBuilder.vertex(minX, minY, z).next();
            bufferBuilder.vertex(maxX, minY, z).next();

            bufferBuilder.vertex(minX, maxY, z).next();
            bufferBuilder.vertex(maxX, maxY, z).next();

            if (z != minZ && z != maxZ) {
                bufferBuilder.vertex(minX, maxY, z).next();
                bufferBuilder.vertex(minX, minY, z).next();

                bufferBuilder.vertex(maxX, maxY, z).next();
                bufferBuilder.vertex(maxX, minY, z).next();
            }
        }

        for (double x = minX; x <= maxX; x += step) {

            bufferBuilder.vertex(x, minY, minZ).next();
            bufferBuilder.vertex(x, minY, maxZ).next();

            bufferBuilder.vertex(x, maxY, minZ).next();
            bufferBuilder.vertex(x, maxY, maxZ).next();

            bufferBuilder.vertex(x, maxY, maxZ).next();
            bufferBuilder.vertex(x, minY, maxZ).next();

            bufferBuilder.vertex(x, maxY, minZ).next();
            bufferBuilder.vertex(x, minY, minZ).next();
        }

        for (double y = minY; y <= maxY; y += step) {
            bufferBuilder.vertex(minX, y, minZ).next();
            bufferBuilder.vertex(maxX, y, minZ).next();

            bufferBuilder.vertex(minX, y, maxZ).next();
            bufferBuilder.vertex(maxX, y, maxZ).next();

            bufferBuilder.vertex(minX, y, minZ).next();
            bufferBuilder.vertex(minX, y, maxZ).next();

            bufferBuilder.vertex(maxX, y, minZ).next();
            bufferBuilder.vertex(maxX, y, maxZ).next();
        }
    }

    public static class Region {
        public int minX;
        public int minY;
        public int minZ;

        public int maxX;
        public int maxY;
        public int maxZ;

        public Region() {
        }

        public Region(BlockPos pos1, BlockPos pos2) {
            resize(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ());
        }

        public void resize() {
            resize(minX, minY, minZ, maxX, maxY, maxZ);
        }

        public void resize(int x1, int y1, int z1, int x2, int y2, int z2) {
            this.minX = Math.min(x1, x2);
            this.minY = Math.min(y1, y2);
            this.minZ = Math.min(z1, z2);
            this.maxX = Math.max(x1, x2);
            this.maxY = Math.max(y1, y2);
            this.maxZ = Math.max(z1, z2);
        }
    }

    public boolean enabled() {
        return enabled;
    }

    public BlockPos pos1() {
        return pos1;
    }

    public BlockPos pos2() {
        return pos2;
    }
}
