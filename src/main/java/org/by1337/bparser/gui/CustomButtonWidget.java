package org.by1337.bparser.gui;

import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class CustomButtonWidget extends AbstractButton {

    private final Runnable runnable;

    public CustomButtonWidget(int x, int y, int width, int height, Component component, Runnable runnable) {
        super(x, y, width, height, component);
        this.runnable = runnable;
    }


    @Override
    public void onPress() {
        runnable.run();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

/*
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

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {

    }*/

}
