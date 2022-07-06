package dev.kosmx.playerAnim.api.layered;

public abstract class IModifier extends AnimationContainer<IAnimation> {


    public IModifier() {
        super(null);
    }

    /**
     * @return modifier can be removed.
     */
    public boolean canRemove() {
        return false;
    }

}
