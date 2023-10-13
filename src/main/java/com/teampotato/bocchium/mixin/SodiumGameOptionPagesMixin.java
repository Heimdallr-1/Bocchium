package com.teampotato.bocchium.mixin;

import com.teampotato.bocchium.Bocchium;
import me.jellysquid.mods.sodium.client.gui.SodiumGameOptionPages;
import me.jellysquid.mods.sodium.client.gui.SodiumGameOptions;
import me.jellysquid.mods.sodium.client.gui.options.*;
import me.jellysquid.mods.sodium.client.gui.options.control.TickBoxControl;
import me.jellysquid.mods.sodium.client.gui.options.storage.SodiumOptionsStorage;
import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(value = SodiumGameOptionPages.class, remap = false)
public abstract class SodiumGameOptionPagesMixin {
    @Shadow @Final private static SodiumOptionsStorage sodiumOpts;

    @Inject(method = "performance", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", remap = false, ordinal = 0, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void insertSetting(CallbackInfoReturnable<OptionPage> cir, @NotNull List<OptionGroup> groups) {
        OptionImpl<SodiumGameOptions, Boolean> enableBocchium = OptionImpl.createBuilder(Boolean.class, sodiumOpts)
                .setName(I18n.translate("bocchium.enable"))
                .setTooltip(I18n.translate("bocchium.desc"))
                .setControl(TickBoxControl::new)
                .setBinding(
                        (option, value) -> Bocchium.enableBocchium.set(value),
                        option -> Bocchium.enableBocchium.get()
                )
                .setImpact(OptionImpact.LOW)
                .setFlags(new OptionFlag[]{OptionFlag.REQUIRES_RENDERER_RELOAD})
                .build();

        OptionImpl<SodiumGameOptions, Boolean> shouldCullTop = OptionImpl.createBuilder(Boolean.class, sodiumOpts)
                .setName(I18n.translate("bocchium.cull_top"))
                .setTooltip(I18n.translate("bocchium.cull_top.tooltip"))
                .setControl(TickBoxControl::new)
                .setBinding(
                        (option, value) -> Bocchium.shouldCullTopBedrock.set(value),
                        option -> Bocchium.shouldCullTopBedrock.get()
                )
                .setImpact(OptionImpact.LOW)
                .setFlags(new OptionFlag[]{OptionFlag.REQUIRES_RENDERER_RELOAD})
                .build();

        OptionImpl<SodiumGameOptions, Boolean> shouldCullBottom = OptionImpl.createBuilder(Boolean.class, sodiumOpts)
                .setName(I18n.translate("bocchium.cull_bottom"))
                .setTooltip(I18n.translate("bocchium.cull_bottom.tooltip"))
                .setControl(TickBoxControl::new)
                .setBinding(
                        (option, value) -> Bocchium.shouldCullBottomBedrock.set(value),
                        option -> Bocchium.shouldCullBottomBedrock.get()
                )
                .setImpact(OptionImpact.LOW)
                .setFlags(new OptionFlag[]{OptionFlag.REQUIRES_RENDERER_RELOAD})
                .build();

        groups.add(OptionGroup.createBuilder().add(enableBocchium).add(shouldCullTop).add(shouldCullBottom).build());
    }
}
