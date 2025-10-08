package org.by1337.bparser.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Box;
import org.lwjgl.opengl.GL11;

public class RenderUtil {

    public static void drawBox(Box box, float red, float green, float blue, float alpha) {
        drawBox(box, red, green, blue, alpha, 2.f);
    }
    public static void drawBox(Box box, float red, float green, float blue, float alpha, float lineWidth) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glLineWidth(lineWidth);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.disableDepthTest();

        RenderSystem.disableTexture();

        RenderSystem.color4f(red, green, blue, alpha);

        bufferBuilder.begin(GL11.GL_LINES, VertexFormats.POSITION);
        addBoxLines(bufferBuilder, box);
        tessellator.draw();

        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    private static void addBoxLines(BufferBuilder bufferBuilder, Box box) {
        double minX = box.minX, minY = box.minY, minZ = box.minZ;
        double maxX = box.maxX, maxY = box.maxY, maxZ = box.maxZ;

        bufferBuilder.vertex(minX, minY, minZ).next();
        bufferBuilder.vertex(maxX, minY, minZ).next();

        bufferBuilder.vertex(minX, minY, minZ).next();
        bufferBuilder.vertex(minX, maxY, minZ).next();

        bufferBuilder.vertex(minX, minY, minZ).next();
        bufferBuilder.vertex(minX, minY, maxZ).next();

        bufferBuilder.vertex(maxX, maxY, maxZ).next();
        bufferBuilder.vertex(minX, maxY, maxZ).next();

        bufferBuilder.vertex(maxX, maxY, maxZ).next();
        bufferBuilder.vertex(maxX, minY, maxZ).next();

        bufferBuilder.vertex(maxX, maxY, maxZ).next();
        bufferBuilder.vertex(maxX, maxY, minZ).next();

        bufferBuilder.vertex(maxX, minY, minZ).next();
        bufferBuilder.vertex(maxX, maxY, minZ).next();

        bufferBuilder.vertex(maxX, minY, minZ).next();
        bufferBuilder.vertex(maxX, minY, maxZ).next();

        bufferBuilder.vertex(minX, maxY, maxZ).next();
        bufferBuilder.vertex(minX, minY, maxZ).next();

        bufferBuilder.vertex(minX, maxY, maxZ).next();
        bufferBuilder.vertex(minX, maxY, minZ).next();

        bufferBuilder.vertex(minX, minY, maxZ).next();
        bufferBuilder.vertex(maxX, minY, maxZ).next();

        bufferBuilder.vertex(minX, maxY, minZ).next();
        bufferBuilder.vertex(maxX, maxY, minZ).next();
    }
}
