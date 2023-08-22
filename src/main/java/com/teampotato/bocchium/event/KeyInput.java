package com.teampotato.bocchium.event;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static com.teampotato.bocchium.Bocchium.*;

public class KeyInput {
    @SubscribeEvent
    public static void onClientEndTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && KEY_BINDING.wasPressed()) {
            PASS_SIDE = !PASS_SIDE;
            minecraftClient.worldRenderer.reload();
            ClientPlayerEntity clientPlayer = minecraftClient.player;
            if (clientPlayer == null) return;
            clientPlayer.sendMessage(new LiteralText(I18n.translate("keybind.bocchium.onSwitch") + I18n.translate(PASS_SIDE ? "keybind.bocchium.true" : "keybind.bocchium.false")), false);
        }
    }
}
