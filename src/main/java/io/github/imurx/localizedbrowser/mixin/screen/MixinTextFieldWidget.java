package io.github.imurx.localizedbrowser.mixin.screen;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.github.imurx.localizedbrowser.LocalizedBrowser;
import io.github.imurx.localizedbrowser.access.IMEModeAccessor;
import io.github.imurx.localizedbrowser.util.UselessMath;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextFieldWidget.class)
public abstract class MixinTextFieldWidget extends ClickableWidget implements IMEModeAccessor {
    @Unique
    public int betterlocale$sliceStart = 0;

    @Unique
    public boolean betterlocale$imeMode = false;

    @Unique
    public boolean betterlocale$temporaryIme = false;

    @Unique
    public boolean betterlocale$chatScreen = false;

    @Shadow
    private int selectionEnd;

    public MixinTextFieldWidget(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
    }

    @Override
    public boolean betterlocale$isIme() {
        return betterlocale$imeMode;
    }

    @Override
    public void betterlocale$setImeMode(boolean imeMode) {
        this.betterlocale$imeMode = imeMode;
    }

    @Override
    public boolean betterlocale$isTemporaryIme() {
        return betterlocale$temporaryIme;
    }

    @Override
    public void betterlocale$setTemporaryIme(boolean temporaryIme) {
        this.betterlocale$temporaryIme = temporaryIme;
    }

    @Override
    public void betterlocale$setChatScreen(boolean chatScreen) {
        betterlocale$chatScreen = chatScreen;
    }

    @Shadow
    public abstract int getCursor();
    @Shadow
    public abstract String getText();
    @Shadow
    public abstract void setText(String text);
    @Shadow
    public abstract boolean isActive();
    @Shadow
    public abstract void setSelectionStart(int selectionStart);
    @Shadow
    public abstract void setSelectionEnd(int selectionEnd);
    @Shadow
    public abstract boolean isVisible();
    @Shadow
    public abstract boolean drawsBackground();

    @Shadow
    @Final
    private TextRenderer textRenderer;

    @Inject(method = "moveCursor", at = @At("RETURN"))
    private void onMoveCursor(int offset, boolean shiftKeyPressed, CallbackInfo ci) {
        int start = this.getCursor();
        int end = this.selectionEnd;
        betterlocale$sliceStart = Math.min(start, end);
    }

    @WrapMethod(method = "charTyped")
    private boolean onCharTyped(char chr, int modifiers, Operation<Boolean> original) {
        var locale = LocalizedBrowser.getInstance();
        if (!betterlocale$imeMode || locale.isPassthroughIme()) {
            return original.call(chr, modifiers);
        }

        // Try getting where we started originally typing
        {
            int start = this.getCursor();
            int end = this.selectionEnd;
            int min = Math.min(start, end);
            betterlocale$sliceStart = Math.min(min, betterlocale$sliceStart);
        }
        boolean wasEmpty = this.getText().isEmpty();
        if (!this.isActive() || !original.call(chr, modifiers)) return false;
        // Detect if it's slash command and return if it is for now
        if (betterlocale$chatScreen && locale.hasImeParser() && !locale.isPassthroughIme() && chr == '/' && wasEmpty) {
            locale.togglePassthroughIme();
            betterlocale$sliceStart = 0;
            betterlocale$temporaryIme = true;
            return true;
        }
        // Check if we went back after typing for some reason
        int start = this.getCursor();
        int end = this.selectionEnd;
        int min = Math.min(start, end);
        int max = Math.max(start, end);
        betterlocale$sliceStart = Math.min(min, betterlocale$sliceStart);
        // Slice from last sliceStart to the max cursor position
        var text = locale.imeParser(this.getText().substring(betterlocale$sliceStart, max), min - betterlocale$sliceStart, max - betterlocale$sliceStart);
        this.setText(this.getText().substring(0, betterlocale$sliceStart) + text.text() + this.getText().substring(max));
        this.setSelectionStart(text.selection().getStart() + betterlocale$sliceStart);
        this.setSelectionEnd(text.selection().getEndInclusive() + betterlocale$sliceStart);
        return true;
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!betterlocale$imeMode) return;

        var locale = LocalizedBrowser.getInstance();
        if (locale.hasImeParser() && locale.changeLocale.get(UselessMath.packInt2Long(keyCode, scanCode))) {
            betterlocale$sliceStart = locale.togglePassthroughIme() ? 0 : this.getCursor();
            betterlocale$temporaryIme = false;
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "setText", at = @At("HEAD"))
    private void onSetText(String text, CallbackInfo ci) {
        var locale = LocalizedBrowser.getInstance();
        if (betterlocale$chatScreen && locale.hasImeParser() && !locale.isPassthroughIme() && text.equals("/")) {
            locale.togglePassthroughIme();
            betterlocale$sliceStart = 0;
            betterlocale$temporaryIme = true;
        }
    }

    @Inject(method = "renderWidget", at = @At("TAIL"))
    private void onRender(DrawContext context, int i, int j, float f, CallbackInfo ci) {
        if (betterlocale$imeMode && this.isVisible() && LocalizedBrowser.getInstance().isPassthroughIme()) {
            final int x1 = this.getX() + this.getWidth() - 8 - (this.drawsBackground() ? 2 : 0);
            final int x2 = this.getX() + this.getWidth() - (this.drawsBackground() ? 2 : 0);
            final int y1 = this.getY() + (this.drawsBackground() ? 2 : 0);
            final int y2 = this.getY() + (this.drawsBackground() ? this.getHeight() - 2 : this.textRenderer.fontHeight);
            context.fill(x1, y1, x2, y2, ColorHelper.Argb.getArgb(125, 255, 0, 0));
        }
    }
}
