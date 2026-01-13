
package org.by1337.bparser.toast;


import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CustomToast implements Toast {
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/advancement");
    private final Component title;
    private final Component frameText;
    private final ItemStack icon;
    private Toast.Visibility wantedVisibility = Toast.Visibility.HIDE;

    public CustomToast(Component title, Component frameText, ItemStack icon) {
        this.title = title;
        this.frameText = frameText;
        this.icon = icon;
    }


    @Override
    public Visibility getWantedVisibility() {
        return wantedVisibility;
    }

    @Override
    public void update(ToastManager toastManager, long l) {
        this.wantedVisibility = l >= 5000.0 * toastManager.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }

    @Override
    public void render(GuiGraphics guiGraphics, Font font, long l) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
        List<FormattedCharSequence> list = font.split(title, 125);
        int i = -30465;
        if (list.size() == 1) {
            guiGraphics.drawString(font, frameText, 30, 7, i, false);
            guiGraphics.drawString(font, list.get(0), 30, 18, -1, false);
        } else {
            if (l < 1500L) {
                int k = Mth.floor(Mth.clamp((float) (1500L - l) / 300.0F, 0.0F, 1.0F) * 255.0F);
                guiGraphics.drawString(font, frameText, 30, 11, ARGB.color(k, i), false);
            } else {
                int k = Mth.floor(Mth.clamp((float) (l - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F);
                int m = this.height() / 2 - list.size() * 9 / 2;

                for (FormattedCharSequence formattedCharSequence : list) {
                    guiGraphics.drawString(font, formattedCharSequence, 30, m, ARGB.color(k, -1), false);
                    m += 9;
                }
            }
        }

        guiGraphics.renderFakeItem(icon, 8, 8);
    }
}

