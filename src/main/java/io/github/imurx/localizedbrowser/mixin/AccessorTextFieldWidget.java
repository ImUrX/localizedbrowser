package io.github.imurx.localizedbrowser.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextFieldWidget.class)
public interface AccessorTextFieldWidget {
    @Accessor
    int getSelectionEnd();

    @Accessor
    TextRenderer getTextRenderer();
}
