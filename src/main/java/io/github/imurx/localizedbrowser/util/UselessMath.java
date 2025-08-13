package io.github.imurx.localizedbrowser.util;

public class UselessMath {
    static public long packInt2Long(int x, int y) {
        return (((long) x) << 32)
                | (y & 0xFFFF_FFFFL);
    }
}
