package org.by1337.bparser.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.renderer.*;
import net.minecraft.world.phys.AABB;

import java.util.OptionalDouble;

public class RenderUtil {
    public static final RenderType.CompositeRenderType LINES_NO_DEPTH_TEST = RenderType.create(
            "lines_no_depth_test",
            1536,
            RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
                    .withLocation("pipeline/lines_no_depth_test")
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                    .build(),
            RenderType.CompositeState.builder()
                    .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
                    .setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING)
                    .setOutputState(RenderType.ITEM_ENTITY_TARGET)

                    .createCompositeState(false)
    );

    public static final RenderType.CompositeRenderType LINES_DEPTH_TEST = RenderType.create(
            "lines_depth_test",
            1536,
            RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
                    .withLocation("pipeline/lines_depth_test")
                    .build(),
            RenderType.CompositeState.builder()
                    .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
                    .setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING)
                    .setOutputState(RenderType.ITEM_ENTITY_TARGET)

                    .createCompositeState(false)
    );
    private static RenderType.CompositeRenderType current = LINES_DEPTH_TEST;

    public static void setCurrent(RenderType.CompositeRenderType current) {
        RenderUtil.current = current;
    }

    public static void drawBox(WorldRenderContext context, AABB box,
                                    float r, float g, float b, float a,
                                    float lineWidth) {

        drawBox(context, box, r * .4f, g * .4f, b * .4f, a, lineWidth, LINES_NO_DEPTH_TEST);
        drawBox(context, box, r, g, b, a, lineWidth, LINES_DEPTH_TEST);
    }

    public static void drawBox(WorldRenderContext context, AABB box,
                               float r, float g, float b, float a,
                               float lineWidth, RenderType.CompositeRenderType type) {
        PoseStack matrices = context.matrixStack();
        MultiBufferSource consumers = context.consumers();
        if (matrices == null || consumers == null) return;
        RenderSystem.lineWidth(lineWidth);
        VertexConsumer buffer = consumers.getBuffer(type);
        ShapeRenderer.renderLineBox(
                matrices,
                buffer,
                box.minX, box.minY, box.minZ,
                box.maxX, box.maxY, box.maxZ,
                r, g, b, a
        );
    }
}
