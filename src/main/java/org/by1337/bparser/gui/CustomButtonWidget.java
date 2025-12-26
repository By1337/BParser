package org.by1337.bparser.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class CustomButtonWidget extends ButtonWidget {

    public static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/toasts.png");

    public CustomButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress, (m) -> Text.literal("okak"));
    }

  //  @Override
    protected void renderWidget0(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();

        boolean hovered = isMouseOver(mouseX, mouseY);
        int offsetY = hovered ? 32 : 0;

        client.getTextureManager().bindTexture(WIDGETS_TEXTURE);

        context.drawTexture(WIDGETS_TEXTURE, this.getX(), this.getY(), 0, offsetY, this.width / 2, this.height / 2);
        context.drawTexture(WIDGETS_TEXTURE, this.getX(), this.getY() + this.height / 2, 0, offsetY + (32 - this.height / 2), this.width / 2, this.height / 2);
        context.drawTexture(WIDGETS_TEXTURE, this.getX() + this.width / 2, this.getY(), 160 - this.width / 2, offsetY, this.width / 2, this.height / 2);
        context.drawTexture(WIDGETS_TEXTURE, this.getX() + this.width / 2, this.getY() + this.height / 2, 160 - this.width / 2, offsetY + (32 - this.height / 2), this.width / 2, this.height / 2);


        int color = this.active ? 0xFFFFFF : 0xA0A0A0;
        int alphaBits = MathHelper.ceil(this.alpha * 255.0F) << 24;
        context.drawText(client.textRenderer, getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, color | alphaBits, false);

    }

}
