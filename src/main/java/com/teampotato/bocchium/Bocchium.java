package com.teampotato.bocchium;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.Direction;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.NetworkConstants;
import org.lwjgl.glfw.GLFW;

import java.util.Map;

@Mod(Bocchium.MOD_ID)
public class Bocchium {
    private static MinecraftClient minecraftClient;
    public static final String MOD_ID = "bocchium";
    public static boolean PASS_SIDE = true;
    private static KeyBinding KEY_BINDING;

    public static final ForgeConfigSpec config;
    public static final ForgeConfigSpec.BooleanValue shouldCullTopBedrock;
    public static final ForgeConfigSpec.BooleanValue shouldCullBottomBedrock;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("BocchiTheBedrock");
        shouldCullTopBedrock = builder.define("shouldCullTopBedrock", true);
        shouldCullBottomBedrock = builder.define("shouldCullBottomBedrock", true);
        builder.pop();
        config = builder.build();
    }

    private static final Map<Boolean, String> TRUE_OR_FALSE = new Object2ObjectOpenHashMap<>();

    static {
        TRUE_OR_FALSE.put(true, "keybind.bocchium.true");
        TRUE_OR_FALSE.put(false, "keybind.bocchium.false");
    }

    public static boolean canCullBottom(Direction facing, int height) {
        if (!shouldCullBottomBedrock.get()) return false;
        ClientWorld clientWorld = minecraftClient.world;
        if (clientWorld == null) return false;
        return facing == Direction.DOWN && height == clientWorld.getBottomY();
    }

    public static boolean canCullTop(Direction facing, int height) {
        if (!shouldCullTopBedrock.get()) return false;
        ClientWorld clientWorld = minecraftClient.world;
        if (clientWorld == null) return false;
        return facing == Direction.UP && height == clientWorld.getDimension().logicalHeight() - 1;
    }


    public Bocchium() {
        ModLoadingContext ctx = ModLoadingContext.get();
        ctx.registerConfig(ModConfig.Type.CLIENT, config);
        ctx.registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

        minecraftClient = MinecraftClient.getInstance();

        KEY_BINDING = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "keybind." + MOD_ID +".keyName",
                        KeyConflictContext.IN_GAME,
                        InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,
                        "keybind." + MOD_ID + ".keyName"
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!KEY_BINDING.wasPressed()) return;
            PASS_SIDE = !PASS_SIDE;
            minecraftClient.worldRenderer.reload();
            ClientPlayerEntity clientPlayer = minecraftClient.player;
            if (clientPlayer == null) return;
            clientPlayer.sendMessage(MutableText.of(new LiteralTextContent(I18n.translate("keybind.bocchium.onSwitch") + I18n.translate(TRUE_OR_FALSE.get(PASS_SIDE)))));
        });
    }
}
