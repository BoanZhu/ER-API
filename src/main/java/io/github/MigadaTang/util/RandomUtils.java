package io.github.MigadaTang.util;

import java.util.UUID;

public class RandomUtils {
    public static Long generateID() {
        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }
}


