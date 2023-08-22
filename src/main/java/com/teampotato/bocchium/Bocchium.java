package com.teampotato.bocchium;

import com.teampotato.bocchium.event.KeyInput;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.glfw.GLFW;

@Mod(Bocchium.MOD_ID)
public class Bocchium {
    public static MinecraftClient minecraftClient;
    public static final String MOD_ID = "bocchium";
    public static boolean PASS_SIDE = true;
    public static KeyBinding KEY_BINDING = new KeyBinding("keybind." + MOD_ID +".keyName", KeyConflictContext.IN_GAME, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "keybind." + MOD_ID + ".keyName");

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

    public static boolean canCullBottom(Direction facing, int height) {
        if (!shouldCullBottomBedrock.get()) return false;
        ClientWorld clientWorld = minecraftClient.world;
        if (clientWorld == null) return false;
        return facing == Direction.DOWN && height == 0;
    }

    public static boolean canCullTop(Direction facing, int height) {
        if (!shouldCullTopBedrock.get()) return false;
        ClientWorld clientWorld = minecraftClient.world;
        if (clientWorld == null) return false;
        DimensionType dimensionType = clientWorld.getDimension();
        return facing == Direction.UP && height == dimensionType.getLogicalHeight() - 1 && dimensionType.hasCeiling();
    }

    public Bocchium() {
        ModLoadingContext ctx = ModLoadingContext.get();
        ctx.registerConfig(ModConfig.Type.CLIENT, config);
        ctx.registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        minecraftClient = MinecraftClient.getInstance();
        MinecraftForge.EVENT_BUS.register(KeyInput.class);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
    }

    public static boolean shouldCull(Direction facing, int height) {
        return (canCullBottom(facing, height) || canCullTop(facing, height)) && PASS_SIDE;
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ClientRegistry.registerKeyBinding(KEY_BINDING));
    }
}
