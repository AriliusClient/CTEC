package me.constantindev.ctec.mixin;

import me.constantindev.ctec.client.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(ClientPlayerEntity.class)
public class ChatMixin {
    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void sendChatMessage(String msg, CallbackInfo cbi) {
        if (msg.startsWith("@ctec")) {
            cbi.cancel();
            String[] args = msg.substring(5).trim().split(" +");
            String cmd = args[0].toLowerCase();
            switch (cmd) {
                case "toggle":
                    Config.isEnabled = !Config.isEnabled;
                    MinecraftClient.getInstance().player.sendMessage(Text.of((Config.isEnabled ? "En" : "Dis") + "abled crasher"), true);
                    break;
                case "setdelay":
                    if (args.length < 2) {
                        MinecraftClient.getInstance().player.sendMessage(Text.of("Argument missing smh"), true);
                        break;
                    }
                    String ndl = args[1];
                    int ndlI = 0;
                    try {
                        ndlI = Integer.parseInt(ndl);
                    } catch (Exception exc) {
                        MinecraftClient.getInstance().player.sendMessage(Text.of("okay lets try that again, but this time with a valid number okay?"), true);
                        break;
                    }
                    Config.waitTime = ndlI;
                    MinecraftClient.getInstance().player.sendMessage(Text.of("aight, set delay to " + ndlI), true);
                    break;
                case "setrecipes":
                    if (args.length < 3) {
                        MinecraftClient.getInstance().player.sendMessage(Text.of("Arguments missing smh"), true);
                        break;
                    }
                    String r1s = args[1];
                    String r2s = args[2];
                    RecipeManager mg = MinecraftClient.getInstance().player.world.getRecipeManager();
                    Optional r1o = mg.get(new Identifier(r1s));
                    Optional r2o = mg.get(new Identifier(r2s));
                    if (!r1o.isPresent()) {
                        MinecraftClient.getInstance().player.sendMessage(Text.of(r1s + " aint existing."), true);
                        break;
                    }
                    if (!r2o.isPresent()) {
                        MinecraftClient.getInstance().player.sendMessage(Text.of(r2s + " aint existing."), true);
                        break;
                    }
                    Config.r1 = (Recipe) r1o.get();
                    Config.r2 = (Recipe) r2o.get();
                    break;
                case "searchrecipe":
                    if (args.length < 2) {
                        MinecraftClient.getInstance().player.sendMessage(Text.of("argument missing smh"), true);
                        break;
                    }
                    String search = args[1];
                    RecipeManager mgr = MinecraftClient.getInstance().player.world.getRecipeManager();
                    List<Identifier> results = new ArrayList<>();
                    mgr.keys().forEach(identifier -> {
                        if (identifier.getPath().toLowerCase().contains(search.toLowerCase())) results.add(identifier);
                    });
                    if (results.size() == 0) {
                        MinecraftClient.getInstance().player.sendMessage(Text.of("\"" + search + "\" didnt amount to any results."), false);
                        break;
                    }
                    MinecraftClient.getInstance().player.sendMessage(Text.of("Results:"), false);
                    results.forEach(identifier -> {
                        MinecraftClient.getInstance().player.sendMessage(Text.of(identifier.getPath().replaceAll(search, Formatting.AQUA + search + Formatting.RESET)), false);
                    });
                    break;
                default:
                    MinecraftClient.getInstance().player.sendMessage(Text.of("my man choose a working command please. (toggle, setdelay, setrecipes, searchrecipe)"), false);
            }
        }
    }
}
