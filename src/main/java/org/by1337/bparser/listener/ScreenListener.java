package org.by1337.bparser.listener;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.world.Container;
import org.by1337.bparser.cfg.Config;
import org.by1337.bparser.gui.CustomButtonWidget;
import org.by1337.bparser.inv.ScreenUtil;
import org.by1337.bparser.inv.copy.MenuSaver;
import org.by1337.bparser.inv.copy.ScreenAnimationParser;
import org.by1337.bparser.text.ComponentUtil;
import org.by1337.bparser.text.StringUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ScreenListener {
    public ScreenListener() {
        final int buttonWidth = 58;
        final int buttonHeight = 16;

        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (!Config.INSTANCE.menuCopy) return;
            if (screen instanceof AbstractContainerScreen<?> handledScreen) {
                Container inventory = ScreenUtil.getInventory(handledScreen);
                if (inventory != null) {
                    final ScreenAnimationParser parser = new ScreenAnimationParser(inventory, screen);
                    Screens.getButtons(screen).add(
                            new CustomButtonWidget(
                                    (width / 2) - (buttonWidth / 2) + 58 - 3, (height / 2) - (buttonHeight / 2) - (103 + buttonHeight), buttonWidth, buttonHeight, Component.literal("save anim"), () -> {
                                if (!parser.isStop()) {
                                    //  CustomToast customToast = new CustomToast(
                                    //          Component.translatable("lang.bparser.menu.wait2"),
                                    //          Component.translatable("lang.bparser.menu.wait"),
                                    //          new ItemStack(Items.BARRIER)
                                    //  );
                                    //  MinecraftClient.getInstance().getToastManager().add(customToast);
                                } else {
                                    MenuSaver menuSaver = new MenuSaver(handledScreen, parser.getFrameCreator().frames, inventory);
                                    saveToFile(menuSaver.save(), generateSaveName(menuSaver, handledScreen));
                                }
                            })
                    );
                    Screens.getButtons(screen).add(
                            new CustomButtonWidget(
                                    (width / 2) - (buttonWidth / 2) + 58 - 3 - buttonWidth, (height / 2) - (buttonHeight / 2) - (103 + buttonHeight), buttonWidth, buttonHeight, Component.literal("save"), () -> {
                                MenuSaver menuSaver = new MenuSaver(handledScreen, parser.getFrameCreator().frames, inventory);
                                saveToFile(menuSaver.saveCurrentFrame(), generateSaveName(menuSaver, handledScreen));
                            })
                    );
                }
            }
        });
    }

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("//menu_copy")
                .executes(ctx -> {
                    Config.INSTANCE.menuCopy = !Config.INSTANCE.menuCopy;
                    if (Config.INSTANCE.menuCopy) {
                        ctx.getSource().sendFeedback(Component.translatable("lang.bparser.menu.on"));
                    } else {
                        ctx.getSource().sendFeedback(Component.translatable("lang.bparser.menu.off"));
                    }
                    Config.INSTANCE.save();
                    return 1;
                })
        );
    }

    private String generateSaveName(MenuSaver menuSaver, AbstractContainerScreen<?> screen) {
        Path menuFolder = FabricLoader.getInstance().getGameDir().resolve("mods/menus");

        if (screen.getTitle().getContents() instanceof ComponentContents) {
            String title = ComponentUtil.convert(screen.getTitle(), true);
            String s = StringUtil.removeIf(
                    title,
                    c ->
                            (!(c >= 'a' && c <= 'z') && !(c >= 'A' && c <= 'Z')) &&
                                    (!(c >= 'а' && c <= 'я') && !(c >= 'А' && c <= 'Я')) &&
                                    (!(c >= '0' && c <= '9')) &&
                                    c != ' '
            ).trim();

            String resultPath = s.replace(" ", "-");
            int x = 0;
            while (menuFolder.resolve(resultPath + ".yml").toFile().exists()) {
                resultPath = s + "(" + ++x + ")";
            }
            return resultPath + ".yml";
        }
        return menuSaver.getRandomUUID() + ".yml";
    }

    private void saveToFile(String data, String fileName) {
        Path modsFolderPath = FabricLoader.getInstance().getGameDir().resolve("mods");

        Path folder = modsFolderPath.resolve("menus");
        folder.toFile().mkdirs();

        try {
            Files.write(folder.resolve(fileName), data.getBytes(StandardCharsets.UTF_8));

            // CustomToast customToast = new CustomToast(
            //         Component.translatable("lang.bparser.menu.saved.path", fileName),
            //         Component.translatable("lang.bparser.menu.saved"),
            //         new ItemStack(Items.LIME_DYE)
            // );
            // MinecraftClient.getInstance().getToastManager().add(customToast);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
