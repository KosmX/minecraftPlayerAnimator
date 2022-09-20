package dev.kosmx.playerAnim.impl;

import dev.architectury.injectables.annotations.ExpectPlatform;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Helper Utility class
 */
@ApiStatus.Internal
public final class Helper {

    @Nullable
    private static AtomicBoolean isBendyLibLoaded = null;

    public static boolean isBendEnabled() {
        if (isBendyLibLoaded == null) isBendyLibLoaded = new AtomicBoolean(isBendyLibPresent());
        return isBendyLibLoaded.get();
    }

    @ExpectPlatform
    public static boolean isBendyLibPresent() {
        throw new AssertionError();
    }
}
