package dev.kosmx.playerAnim.impl;

import dev.kosmx.playerAnim.core.impl.AnimationPlayer;
import dev.kosmx.playerAnim.core.util.SetableSupplier;

public interface IMutableModel {

    void setEmoteSupplier(SetableSupplier<AnimationPlayer> emoteSupplier);

    SetableSupplier<AnimationPlayer> getEmoteSupplier();

}
