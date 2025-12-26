package org.by1337.bparser.toast;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.Objects;

public class CustomToast implements Toast {
    private static final Identifier TEXTURE = new Identifier("toast/advancement");
    private final Text title;
    private final Text frameText;
    private final ItemStack icon;

    public CustomToast(Text title, Text frameText, ItemStack icon) {
        this.title = title;
        this.frameText = frameText;
        this.icon = icon;
    }

    public Toast.Visibility draw(DrawContext context, ToastManager manager, long startTime) {
        context.drawGuiTexture(TEXTURE, 0, 0, this.getWidth(), this.getHeight());
        {
            List<OrderedText> list = manager.getClient().textRenderer.wrapLines(title, 125);
            int i = 16746751;
            if (list.size() == 1) {
                context.drawText(manager.getClient().textRenderer, frameText, 30, 7, i | -16777216, false);
                context.drawText(manager.getClient().textRenderer, (OrderedText) list.get(0), 30, 18, -1, false);
            } else {
                int j = 1500;
                float f = 300.0F;
                if (startTime < 1500L) {
                    int k = MathHelper.floor(MathHelper.clamp((float) (1500L - startTime) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
                    context.drawText(manager.getClient().textRenderer, frameText, 30, 11, i | k, false);
                } else {
                    int k = MathHelper.floor(MathHelper.clamp((float) (startTime - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
                    int var10000 = this.getHeight() / 2;
                    int var10001 = list.size();
                    Objects.requireNonNull(manager.getClient().textRenderer);
                    int l = var10000 - var10001 * 9 / 2;

                    for (OrderedText orderedText : list) {
                        context.drawText(manager.getClient().textRenderer, orderedText, 30, l, 16777215 | k, false);
                        Objects.requireNonNull(manager.getClient().textRenderer);
                        l += 9;
                    }
                }
            }

            context.drawItemWithoutEntity(icon, 8, 8);
            return (double) startTime >= (double) 5000.0F * manager.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
        }
    }
}
