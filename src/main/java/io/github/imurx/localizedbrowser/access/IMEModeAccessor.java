package io.github.imurx.localizedbrowser.access;

public interface IMEModeAccessor {
    boolean betterlocale$isIme();

    void betterlocale$setImeMode(boolean imeMode);

    default boolean betterlocale$isTemporaryIme() {
        return false;
    }

    default void betterlocale$setTemporaryIme(boolean temporaryIme) {
    }

    default void betterlocale$setChatScreen(boolean chatScreen) {
    }
}
