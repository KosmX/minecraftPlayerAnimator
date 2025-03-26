package dev.kosmx.playerAnim.core.util;

import java.util.function.Supplier;

/*
 * I'll use this...
 */
public class SetableSupplier<T> implements Supplier<T> {
    T object;

    public SetableSupplier(T initialValue) {
        object = initialValue;
    }

    /**
     * :D
     * @param object T
     */
    public void set(T object) {
        this.object = object;
    }

    @Override
    public T get() {
        return this.object;
    }
}
