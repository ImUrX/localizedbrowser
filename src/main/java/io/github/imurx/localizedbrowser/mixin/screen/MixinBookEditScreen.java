package io.github.imurx.localizedbrowser.mixin.screen;

import io.github.imurx.localizedbrowser.access.IMEModeAccessor;
import io.github.imurx.localizedbrowser.util.SelectionManagerHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BookEditScreen.class)
public class MixinBookEditScreen extends Screen implements IMEModeAccessor {
    @Unique
    public final SelectionManagerHelper betterlocale$helper = new SelectionManagerHelper(
            () -> this.signing ? this.bookTitleSelectionManager : this.currentPageSelectionManager
    );

    @Shadow
    @Final
    private static int WIDTH;
    @Shadow
    @Final
    private SelectionManager currentPageSelectionManager;
    @Shadow
    @Final
    private SelectionManager bookTitleSelectionManager;
    @Shadow
    private boolean signing;
    @Shadow
    private boolean dirty;
    @Shadow
    private String title;

    protected MixinBookEditScreen(Text title) {
        super(title);
    }

    @Shadow
    private String getCurrentPageContent() {
        return null;
    }

    @Shadow
    private void setPageContent(String newContent) {
    }

    @Shadow
    private void updateButtons() {
    }

    @Shadow
    private void invalidatePageContent() {
    }

    @Override
    public boolean betterlocale$isIme() {
        return betterlocale$helper.isIme();
    }
    @Override
    public void betterlocale$setImeMode(boolean imeMode) {
        betterlocale$helper.setIme(imeMode);
    }

    @Inject(method = "keyPressedEditMode", at = @At("RETURN"))
    private void onKeyPressedEditMode(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if(!cir.getReturnValueZ()) return;

        betterlocale$helper.updateStart(true);
    }

    @Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/SelectionManager;moveCursorTo(IZ)V", shift = At.Shift.AFTER))
    private void onMouseClicked(double d, double e, int i, CallbackInfoReturnable<Boolean> cir) {
        betterlocale$helper.updateStart(true);
    }

    @Inject(method = "charTyped", at = @At("HEAD"))
    private void onCharTyped(char c, int i, CallbackInfoReturnable<Boolean> cir) {
        betterlocale$helper.updateStart();
    }

    @Inject(method = "charTyped", at = @At("RETURN"))
    private void onCharTypedReturn(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            if (this.signing) {
                betterlocale$helper.onCharTyped(this.title, (s) -> this.title = s);
                updateButtons();
                this.dirty = true;
            } else {
                betterlocale$helper.onCharTyped(this.getCurrentPageContent(), this::setPageContent);
                invalidatePageContent();
            }
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        boolean pressed = betterlocale$helper.onKeyPressed(keyCode, scanCode);
        if (pressed) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "renderBackground", at = @At("TAIL"))
    private void onRenderBackground(DrawContext ctx, int i, int j, float f, CallbackInfo ci) {
        if (!betterlocale$helper.isIme()) {
            final int x0 = ((this.width - WIDTH) / 2) + (WIDTH - 24), y0 = 2;
            ctx.fill(x0, y0, x0 + 8, y0 + 8, Colors.RED);
        }
    }
}
