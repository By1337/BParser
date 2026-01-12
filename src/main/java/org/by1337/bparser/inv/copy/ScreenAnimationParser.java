package org.by1337.bparser.inv.copy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.Container;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScreenAnimationParser {
    private final Container inventory;
    private final Screen screen;
    private volatile boolean stop;
    private final FrameCreator frameCreator = new FrameCreator();

    public ScreenAnimationParser(Container inventory, Screen screen) {
        this.screen = screen;
        this.inventory = inventory;
        frameCreator.screenshot();
        new Thread(() -> {
            while (!stop) {
                if (Minecraft.getInstance().screen != this.screen) {
                    return;
                }
                if (frameCreator.noDiff > 150) {
                    frameCreator.end();
                    stop = true;
                    return;
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                frameCreator.screenshot();
            }
        }).start();
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }


    public boolean isStop() {
        return stop;
    }

    public FrameCreator getFrameCreator() {
        return frameCreator;
    }

    public class FrameCreator {
        private long lastFrame;
        private int noDiff;
        public List<ScreenData> frames = new ArrayList<>();
        private ScreenData last;

        public void end() {
            if (last != null) {
                frames.add(last);
            }
        }

        public void screenshot() {
            ScreenData current = new ScreenData(inventory);
            if (last == null) {
                last = current;
                frames.add(last);
            } else {
                Map<Integer, InvItem> diff = last.getDiff(current);
                if (diff.isEmpty()) {
                    noDiff++;
                } else {
                    last = current;
                    frames.add(last);
                }
            }
        }
    }
}
