package org.by1337.bparser.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class CustomButtonWidget extends ButtonWidget {

    public static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/toasts.png");

    public CustomButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress);
    }

    public CustomButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, TooltipSupplier tooltipSupplier) {
        super(x, y, width, height, message, onPress, tooltipSupplier);
    }

    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;
        minecraftClient.getTextureManager().bindTexture(WIDGETS_TEXTURE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        int offsets = 32 * (this.getYImage(this.isHovered()) - 1);

        this.drawTexture(matrices, this.x, this.y, 0, offsets, this.width / 2, this.height / 2);
        this.drawTexture(matrices, this.x, this.y + this.height / 2, 0, (32 - this.height / 2) + offsets, this.width / 2, this.height / 2);

        this.drawTexture(matrices, this.x + this.width / 2, this.y, 160 - this.width / 2, offsets, this.width / 2, this.height / 2);
        this.drawTexture(matrices, this.x + this.width / 2, this.y + this.height / 2, 160 - this.width / 2, (32 - this.height / 2) + offsets, this.width / 2, this.height / 2);


        this.renderBackground(matrices, minecraftClient, mouseX, mouseY);
        int j = this.active ? 16777215 : 10526880;
        drawCenteredText(matrices, textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }
}
