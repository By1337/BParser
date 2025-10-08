package org.by1337.bparser.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.Iterator;
import java.util.List;

public class CustomToast implements Toast {
    private final Text title;
    private final Text frameText;
    private final ItemStack icon;

    public CustomToast(Text title, Text frameText, ItemStack icon) {
        this.title = title;
        this.frameText = frameText;
        this.icon = icon;
    }

    @Override
    public Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        manager.getGame().getTextureManager().bindTexture(TEXTURE);
        RenderSystem.color3f(1.0F, 1.0F, 1.0F);
        manager.drawTexture(matrices, 0, 0, 0, 0, this.getWidth(), this.getHeight());
        List<OrderedText> list = manager.getGame().textRenderer.wrapLines(title, 125);
        int i = 6746751;
        if (list.size() == 1) {
            manager.getGame().textRenderer.draw(matrices, frameText, 30.0F, 7.0F, i | -16777216);
            manager.getGame().textRenderer.draw(matrices, list.get(0), 30.0F, 18.0F, -1);
        } else {
            int k;
            if (startTime < 1500L) {
                k = MathHelper.floor(MathHelper.clamp((float) (1500L - startTime) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
                manager.getGame().textRenderer.draw(matrices, frameText, 30.0F, 11.0F, i | k);
            } else {
                k = MathHelper.floor(MathHelper.clamp((float) (startTime - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
                int var10000 = this.getHeight() / 2;
                int var10001 = list.size();
                int l = var10000 - var10001 * 9 / 2;

                for (Iterator<OrderedText> var12 = list.iterator(); var12.hasNext(); l += 9) {
                    OrderedText orderedText = var12.next();
                    manager.getGame().textRenderer.draw(matrices, orderedText, 30.0F, (float) l, 16777215 | k);
                }
            }
        }
        manager.getGame().getItemRenderer().renderInGui(icon, 8, 8);
        return startTime >= 5000L ? Visibility.HIDE : Visibility.SHOW;
    }
}
