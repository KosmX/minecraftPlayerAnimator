package dev.kosmx.playerAnim.impl.animation;

import dev.architectury.injectables.annotations.ExpectPlatform;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Helper Utility class
 */
public final class Helper {
    @Nullable
    private static AtomicBoolean isBendyLibLoaded = null;

    public static boolean isBendEnabled() {
        if (isBendyLibLoaded == null) isBendyLibLoaded = new AtomicBoolean(isBendyLibPresent());
        return isBendyLibLoaded.get();
    }

    @ExpectPlatform
    private static boolean isBendyLibPresent() {
        throw new AssertionError();
    }
}
