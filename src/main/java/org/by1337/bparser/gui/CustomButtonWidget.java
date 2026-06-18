package org.by1337.bparser.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Supplier;

public class CustomButtonWidget extends Button {

    private final Runnable runnable;

    public CustomButtonWidget(int x, int y, int width, int height, Component component, Runnable runnable) {
       // super(x, y, width, height, component);
        super(x, y, width, height, component, new OnPress() {
            @Override
            public void onPress(Button button) {
                runnable.run();
            }
        }, new CreateNarration() {
            @Override
            public MutableComponent createNarrationMessage(Supplier<MutableComponent> supplier) {
                return supplier.get();
            }
        });
        this.runnable = runnable;
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int i, int j, float f) {
        this.renderDefaultSprite(guiGraphics);
        this.renderDefaultLabel(guiGraphics.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE));
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
