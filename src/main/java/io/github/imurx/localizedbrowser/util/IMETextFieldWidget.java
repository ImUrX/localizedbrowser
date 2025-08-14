package io.github.imurx.localizedbrowser.util;

import io.github.imurx.localizedbrowser.LocalizedBrowser;
import io.github.imurx.localizedbrowser.mixin.screen.AccessorTextFieldWidget;
import io.github.imurx.localizedbrowser.mixin.screen.MixinTextFieldWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.jetbrains.annotations.Nullable;

/**
 * Replaced for just {@link MixinTextFieldWidget}
 */
@Deprecated
public class IMETextFieldWidget extends TextFieldWidget {
    public IMETextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        super(textRenderer, x, y, width, height, text);
    }

    public IMETextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, @Nullable TextFieldWidget copyFrom, Text text) {
        super(textRenderer, x, y, width, height, copyFrom, text);
    }

    private int sliceStart = 0;

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (LocalizedBrowser.getInstance().isPassthroughIme()) {
            return super.charTyped(chr, modifiers);
        }

        // Try getting where we started originally typing
        {
            int start = this.getCursor();
            int end = ((AccessorTextFieldWidget) this).getSelectionEnd();
            int min = Math.min(start, end);
            sliceStart = Math.min(min, sliceStart);
        }
        if (!this.isActive() || !super.charTyped(chr, modifiers)) return false;
        // Check if we went back after typing for some reason
        int start = this.getCursor();
        int end = ((AccessorTextFieldWidget) this).getSelectionEnd();
        int min = Math.min(start, end);
        int max = Math.max(start, end);
        sliceStart = Math.min(min, sliceStart);
        // Slice from last sliceStart to the max cursor position
        var text = LocalizedBrowser.getInstance().imeParser(this.getText().substring(sliceStart, max), min - sliceStart, max - sliceStart);
        this.setText(this.getText().substring(0, sliceStart) + text.text() + this.getText().substring(max));
        this.setSelectionStart(text.selection().getStart() + sliceStart);
        this.setSelectionEnd(text.selection().getEndInclusive() + sliceStart);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        var locale = LocalizedBrowser.getInstance();
        if (locale.hasImeParser() && locale.changeLocale.get(UselessMath.packInt2Long(keyCode, scanCode))) {
            boolean toggle = locale.togglePassthroughIme();
            sliceStart = toggle ? 0 : this.getCursor();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        if (this.isVisible() && LocalizedBrowser.getInstance().isPassthroughIme()) {
            final int x1 = this.getX() + this.getWidth() - 8 - (this.drawsBackground() ? 2 : 0);
            final int x2 = this.getX() + this.getWidth() - (this.drawsBackground() ? 2 : 0);
            final int y1 = this.getY() + (this.drawsBackground() ? 2 : 0);
            final int y2 = this.getY() + this.getHeight() - (this.drawsBackground() ? 2 : 1);
            context.fill(x1, y1, x2, y2, Colors.RED);
        }
    }
}
