package com.ic.er.common;

import java.util.UUID;

public class Utils {
    public static Long generateID() {
        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }
}
