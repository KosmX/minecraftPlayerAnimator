package dev.kosmx.playerAnim.core.util;

//I Didn't find any pair in Java common... so here is it

import lombok.Getter;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * Pair, stores two objects.
 * @param <L> Left object
 * @param <R> Right object
 */
@Getter
@Immutable
public class Pair<L, R> {
    final L left;
    final R right;

    /**
     * Creates a pair from two values
     * @param left  left member
     * @param right right member
     */
    public Pair(L left, R right){
        this.left = left;
        this.right = right;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean equals(Object o){
        if(o instanceof Pair){
            Pair o2 = (Pair) o;
            return Objects.equals(this.left, o2.left) && Objects.equals(right, o2.right);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = hash * 31 + (left == null ? 0 : left.hashCode());
        hash = hash * 31 + (right == null ? 0 : right.hashCode());
        return hash;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }
}
