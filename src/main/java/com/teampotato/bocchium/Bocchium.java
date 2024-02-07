package com.teampotato.bocchium;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Direction;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.NetworkConstants;

@Mod(Bocchium.MOD_ID)
public class Bocchium {
    public static final String MOD_ID = "bocchium";
    public static final ForgeConfigSpec config;
    public static final ForgeConfigSpec.BooleanValue shouldCullTopBedrock, shouldCullBottomBedrock, enableBocchium;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("BocchiTheBedrock");
        enableBocchium = builder.define("enableBocchium", true);
        shouldCullTopBedrock = builder.define("shouldCullTopBedrock", true);
        shouldCullBottomBedrock = builder.define("shouldCullBottomBedrock", true);
        builder.pop();
        config = builder.build();
    }

    public static boolean canCullBottom(Direction facing, int height) {
        if (!shouldCullBottomBedrock.get()) return false;
        ClientWorld clientWorld = MinecraftClient.getInstance().world;
        if (clientWorld == null) return false;
        return facing == Direction.DOWN && height == clientWorld.getBottomY();
    }

    public static boolean canCullTop(Direction facing, int height) {
        if (!shouldCullTopBedrock.get()) return false;
        ClientWorld clientWorld = MinecraftClient.getInstance().world;
        if (clientWorld == null) return false;
        return facing == Direction.UP && height == clientWorld.getTopY() - 1 && clientWorld.getDimension().hasCeiling();
    }

    public Bocchium() {
        ModLoadingContext ctx = ModLoadingContext.get();
        ctx.registerConfig(ModConfig.Type.CLIENT, config);
        ctx.registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }

    public static boolean shouldCull(Direction facing, int height) {
        return (canCullBottom(facing, height) || canCullTop(facing, height)) && enableBocchium.get();
    }
}
