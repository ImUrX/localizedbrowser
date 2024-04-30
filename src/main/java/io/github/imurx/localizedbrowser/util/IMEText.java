package io.github.imurx.localizedbrowser.util;

import kotlin.ranges.IntRange;

import java.util.Objects;

public record IMEText(String text, IntRange selection) {
    public IMEText {
        Objects.requireNonNull(text);
        Objects.requireNonNull(selection);
    }

    public IMEText(String text, int start, int end) {
        this(text, new IntRange(start, end));
    }
}
