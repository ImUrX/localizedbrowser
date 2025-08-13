package io.github.imurx.localizedbrowser.mixin;

import io.github.imurx.localizedbrowser.LocalizedBrowser;
import io.github.imurx.localizedbrowser.util.UselessMath;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.util.Colors;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSignEditScreen.class)
public abstract class MixinAbstractSignEditScreen {
    @Unique
    private boolean betterlocale$imeMode = true;
    @Unique
    private int betterlocale$sliceStart = 0;

    @Shadow
    private SelectionManager selectionManager;
    @Shadow
    @Final
    private String[] messages;
    @Shadow
    private int currentRow;

    @Shadow
    private void setCurrentRowMessage(String message) {
    }

    @Inject(method = "charTyped", at = @At("HEAD"))
    private void onCharTyped(char c, int i, CallbackInfoReturnable<Boolean> cir) {
        if (!betterlocale$imeMode) return;
        int start = this.selectionManager.getSelectionStart();
        int end = this.selectionManager.getSelectionEnd();
        int min = Math.min(start, end);
        betterlocale$sliceStart = Math.min(min, betterlocale$sliceStart);
    }

    @Inject(method = "charTyped", at = @At("TAIL"))
    private void onCharTypedReturn(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!betterlocale$imeMode || !cir.getReturnValueZ() || LocalizedBrowser.getInstance().isPassthroughIme())
            return;

        int start = this.selectionManager.getSelectionStart();
        int end = this.selectionManager.getSelectionEnd();
        int min = Math.min(start, end);
        betterlocale$sliceStart = Math.min(min, betterlocale$sliceStart);
        // FIXME: Needs to only parse text being given from sliceStart to cursor position so text in front doesn't get modified
        var text = LocalizedBrowser.getInstance().imeParser(this.messages[this.currentRow].substring(betterlocale$sliceStart), min - betterlocale$sliceStart, Math.max(start, end) - betterlocale$sliceStart);
        this.setCurrentRowMessage(this.messages[this.currentRow].substring(0, betterlocale$sliceStart) + text.text());
        this.selectionManager.setSelection(text.selection().getStart() + betterlocale$sliceStart, text.selection().getEndInclusive() + betterlocale$sliceStart);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!betterlocale$imeMode) return;
        var locale = LocalizedBrowser.getInstance();
        if (locale.hasImeParser() && locale.changeLocale.get(UselessMath.packInt2Long(keyCode, scanCode))) {
            boolean toggle = locale.togglePassthroughIme();
            betterlocale$sliceStart = toggle ? 0 : this.selectionManager.getSelectionStart();
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

//    @Inject(method = "renderSign", at = @At(
//            value = "INVOKE",
//            target = "Lnet/minecraft/client/gui/screen/ingame/AbstractSignEditScreen;renderSignBackground(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/block/BlockState;)V",
//            shift = At.Shift.AFTER
//    ))
//    private void onRenderSign(DrawContext context, CallbackInfo ci) {
//        if (betterlocale$imeMode && LocalizedBrowser.getInstance().isPassthroughIme()) {
//            context.getMatrices().translate(0, 10, 0);
//            context.fill(0, 0, 64, 64, Colors.RED);
//            context.getMatrices().translate(0, -10, 0);
//        }
//    }

    public void betterlocale$setIme(boolean mode) {
        this.betterlocale$imeMode = mode;
    }

    public boolean betterlocale$getIme() {
        return this.betterlocale$imeMode;
    }
}
