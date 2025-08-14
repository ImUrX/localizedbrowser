package io.github.imurx.localizedbrowser.util;

import io.github.imurx.localizedbrowser.LocalizedBrowser;
import net.minecraft.client.util.SelectionManager;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SelectionManagerHelper {
    public int sliceStart = 0;
    private final Supplier<SelectionManager> selectionManager;
    private boolean imeMode;

    public SelectionManagerHelper(Supplier<SelectionManager> selectionManager, boolean imeMode) {
        this.selectionManager = selectionManager;
        this.imeMode = imeMode;
    }

    public SelectionManagerHelper(Supplier<SelectionManager> selectionManager) {
        this(selectionManager, true);
    }

    public void updateStart(boolean ignoreStart) {
        if (!this.isIme()) return;

        var selectionManager = this.selectionManager.get();
        int start = selectionManager.getSelectionStart();
        int end = selectionManager.getSelectionEnd();
        int min = Math.min(start, end);
        if (ignoreStart) {
            sliceStart = min;
        } else {
            sliceStart = Math.min(min, sliceStart);
        }
    }
    public void updateStart() {
        updateStart(false);
    }

    public void onCharTyped(String curText, Consumer<String> updateText) {
        if (!this.isIme()) return;
        var selectionManager = this.selectionManager.get();

        int start = selectionManager.getSelectionStart();
        int end = selectionManager.getSelectionEnd();
        int min = Math.min(start, end);
        int max = Math.max(start, end);
        sliceStart = Math.min(min, sliceStart);
        var text = LocalizedBrowser.getInstance().imeParser(curText.substring(sliceStart, max), min - sliceStart, Math.max(start, end) - sliceStart);
        updateText.accept(curText.substring(0, sliceStart) + text.text() +  curText.substring(max));
        LocalizedBrowser.LOGGER.info("onCharTyped curText: {}\n{}", curText, curText.substring(0, sliceStart) + text.text() +  curText.substring(max));
        selectionManager.setSelection(text.selection().getStart() + sliceStart, text.selection().getEndInclusive() + sliceStart);
    }

    public boolean onKeyPressed(int keyCode, int scanCode) {
        if (!this.imeMode) return false;

        var locale = LocalizedBrowser.getInstance();
        if (locale.hasImeParser() && locale.changeLocale.get(UselessMath.packInt2Long(keyCode, scanCode))) {
            sliceStart = locale.togglePassthroughIme() ? 0 : this.selectionManager.get().getSelectionStart();
            return true;
        }

        return false;
    }

    public void setIme(boolean mode) {
        this.imeMode = mode;
    }

    public boolean isIme() {
        return this.imeMode && !LocalizedBrowser.getInstance().isPassthroughIme();
    }
}
