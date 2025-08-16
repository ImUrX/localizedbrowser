package io.github.imurx.localizedbrowser.mixin.jei;

import io.github.imurx.localizedbrowser.mixin.screen.MixinTextFieldWidget;
import mezz.jei.gui.input.GuiTextFieldFilter;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(GuiTextFieldFilter.class)
public abstract class MixinGuiTextFieldFilter extends MixinTextFieldWidget {
    public MixinGuiTextFieldFilter(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
    }

    @Dynamic
    @Inject(
            method = "/<init>/",
            at = @At("TAIL")
    )
    private void onInitialize(CallbackInfo ci) {
        betterlocale$setImeMode(true);
    }
}
