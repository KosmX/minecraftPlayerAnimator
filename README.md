# PlayerAnimator

PlayerAnimator is a minecraft library to animate the **player** while trying to break as few mods as possible.    
If you want to add new entities, use [Geckolib](https://geckolib.com/#mods)  

# Include in your dev environment
```groovy
repositories {
    (...)
    maven {
        name "KosmX's maven"
        url 'https://maven.kosmx.dev/'
    }
}

dependencies {
    (...)
    
    //If you don't want to include the library in your jar, remove the include word
    //You can find the latest version in [](https://maven.kosmx.dev/dev/kosmx/player-anim/player-animation-lib-fabric/)
    include modImplementation("dev.kosmx.player-anim:player-animation-lib-fabric:${project.player_anim}")
    
    //You might want bendy-lib. playerAnimator will wrap it.
    //include modImplementation("io.github.kosmx.bendy-lib:bendy-lib-fabric:${project.bendylib_version}")
}

```
If you use [architectury](https://docs.architectury.dev/docs/forge_loom/) setup you can implement `player-animation-lib` package in *common*.  

Or you can implement `player-animation-lib-forge` on Forge. (but I don't know how to do that in ForgeGradle)

**For more advanced things, you might use `anim-core` package**.  
It is a minecraft-independent module, containing the animation format and the layers but not the playing mixins...  
Also it is **not** a minecraft mod, do not use `modImplementation` on this.



# Structure 
The library has an animation list of currently *played* animations
Higher priority animations will override others, but can be transparent...  

To add an animation to the player, use 
```java
AnimationStack animationStack = PlayerAnimationAccess.getPlayerAnimLayer(clientPlayer);
animationStack.addAnimLayer(...);
...
```
I advice using `AnimationContainer` and setting its animation. (this is null-tolerant)  

To play a keyframe animation from `emotecraft` or `geckolib` json, `dev.kosmx.playerAnim.core.data.gson.AnimationJson` will help you load it.  
`new KeyframeAnimationPlayer(animation)` will play it for you.

To modify/tweak animations, look into `dev.kosmx.playerAnim.api.layered` package, you might implement your own `IAnimation` or extend/modify an existing class.  
`ModifierLayer` will let you add modifiers but that is effectively an `AnimationContainer` layer.

## Notes
> GeckoLib is not guaranteed to work, try it
> molang is not supported

