package io.github.imurx.localizedbrowser.util;

import dev.esnault.wanakana.core.Wanakana;
import dev.esnault.wanakana.core.utils.ImeText;
import io.github.imurx.localizedbrowser.LocalizedBrowser;
import io.github.imurx.localizedbrowser.mixin.AccessorTextFieldWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class IMETextFieldWidget extends TextFieldWidget {
    public IMETextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        super(textRenderer, x, y, width, height, text);
    }

    public IMETextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, @Nullable TextFieldWidget copyFrom, Text text) {
        super(textRenderer, x, y, width, height, copyFrom, text);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!this.isActive() || !super.charTyped(chr, modifiers)) return false;
        int start = this.getCursor();
        int end = ((AccessorTextFieldWidget) this).getSelectionEnd();
        var text = Wanakana.toKanaIme(new ImeText(this.getText(), Math.min(start, end), Math.max(start, end)));
        this.setText(text.getText());
        this.setSelectionStart(text.getSelection().getStart());
        this.setSelectionEnd(text.getSelection().getEndInclusive());
        return true;
    }
}
