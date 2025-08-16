package io.github.imurx.localizedbrowser.mixin.screen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.imurx.localizedbrowser.access.IMEModeAccessor;
import io.github.imurx.localizedbrowser.util.SelectionManagerHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
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

@Mixin(AbstractSignEditScreen.class)
public abstract class MixinAbstractSignEditScreen extends Screen implements IMEModeAccessor {
    @Unique
    public final SelectionManagerHelper betterlocale$helper = new SelectionManagerHelper(() -> this.selectionManager);

    @Shadow
    private SelectionManager selectionManager;
    @Shadow
    @Final
    private String[] messages;
    @Shadow
    private int currentRow;

    @Override
    public boolean betterlocale$isIme() {
        return betterlocale$helper.isIme();
    }
    @Override
    public void betterlocale$setImeMode(boolean imeMode) {
        betterlocale$helper.setIme(imeMode);
    }

    protected MixinAbstractSignEditScreen(Text title) {
        super(title);
    }

    @Shadow
    private void setCurrentRowMessage(String message) {
    }

    @WrapOperation(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/SelectionManager;handleSpecialKey(I)Z"))
    private boolean onHandleSpecialKey(SelectionManager instance, int keyCode, Operation<Boolean> original) {
        boolean res = original.call(instance, keyCode);
        if(res) {
            betterlocale$helper.updateStart(true);
        }
        return res;
    }

    @Inject(method = "charTyped", at = @At("HEAD"))
    private void onCharTyped(char c, int i, CallbackInfoReturnable<Boolean> cir) {
        betterlocale$helper.updateStart();
    }

    @Inject(method = "charTyped", at = @At("RETURN"))
    private void onCharTypedReturn(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            betterlocale$helper.onCharTyped(this.messages[this.currentRow], this::setCurrentRowMessage);
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

    @Inject(method = "renderSign", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/ingame/AbstractSignEditScreen;renderSignText(Lnet/minecraft/client/gui/DrawContext;)V"
    ))
    private void onRenderSign(DrawContext context, CallbackInfo ci) {
        if (!betterlocale$helper.isIme()) {
            final float x = 44, y = -28, z = 5;
            context.getMatrices().translate(x, y, z);
            context.fill(0, 0, 8, 8, Colors.RED);
            context.getMatrices().translate(-x, -y, -z);
        }
    }
}
