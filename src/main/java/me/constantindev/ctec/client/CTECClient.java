package me.constantindev.ctec.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.network.packet.c2s.play.CraftRequestC2SPacket;
import net.minecraft.recipe.Recipe;

@Environment(EnvType.CLIENT)
public class CTECClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CTECClient.startThreader();
    }

    public static void startThreader() {
        Config.executor = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(Config.waitTime == 0 ? 1 : Config.waitTime);
                    if (!Config.isEnabled) {
                    } else if (MinecraftClient.getInstance().currentScreen instanceof CraftingScreen) {
                        // CraftRequestC2SPacket
                        int sid = MinecraftClient.getInstance().player.currentScreenHandler.syncId;
                        Recipe r1 = Config.r1;
                        Recipe r2 = Config.r2;
                        CraftRequestC2SPacket p1 = new CraftRequestC2SPacket(sid, r1, true);
                        CraftRequestC2SPacket p2 = new CraftRequestC2SPacket(sid, r2, true);
                        MinecraftClient.getInstance().player.world.sendPacket(p1);
                        Thread.sleep(Config.waitTime == 0 ? 1 : Config.waitTime);
                        MinecraftClient.getInstance().player.world.sendPacket(p2);

                    }
                } catch (Exception exc) {

                }
            }

        });
        Config.executor.start();
    }
}
