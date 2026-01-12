package org.by1337.bparser.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.phys.AABB;


public class RenderUtil {

    public static void drawBox(WorldRenderContext context, AABB box, float red, float green, float blue, float alpha) {
        drawBox(context, box, red, green, blue, alpha, 2.f);
    }

    public static void drawBox(WorldRenderContext context, AABB box, float red, float green, float blue, float alpha, float lineWidth) {
      //  var buffer = context.consumers().getBuffer(RenderLayers.LINES);
      //  MatrixStack matrixStack = context.matrixStack();
      //  WorldRenderer.drawBox(matrixStack, buffer, box, red, green, blue, alpha);
    }
}
