package io.github.imurx.localizedbrowser.mixin.emi;

import dev.emi.emi.screen.widget.EmiSearchWidget;
import io.github.imurx.localizedbrowser.LocalizedBrowser;
import io.github.imurx.localizedbrowser.mixin.screen.MixinTextFieldWidget;
import io.github.imurx.localizedbrowser.util.UselessMath;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(EmiSearchWidget.class)
public abstract class MixinEmiSearchWidget extends MixinTextFieldWidget {
    public MixinEmiSearchWidget(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(TextRenderer textRenderer, int x, int y, int width, int height, CallbackInfo ci) {
        this.betterlocale$imeMode = true;
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if(!this.betterlocale$imeMode) return;

        var locale = LocalizedBrowser.getInstance();
        if (locale.hasImeParser() && locale.changeLocale.get(UselessMath.packInt2Long(keyCode, scanCode))) {
            betterlocale$sliceStart = locale.togglePassthroughIme() ? 0 : this.getCursor();
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
