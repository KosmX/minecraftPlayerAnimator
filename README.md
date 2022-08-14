# PlayerAnimator

PlayerAnimator is a minecraft library to animate the **player** while trying to break as few mods as possible.    
If you want to add new entities, use [Geckolib](https://geckolib.com/#mods)  

# Include in your dev environment
Fabric loom (or architectury loom)
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
    //include runtimeOnly("io.github.kosmx.bendy-lib:bendy-lib-fabric:${project.bendylib_version}")
}

```
If you use [architectury](https://docs.architectury.dev/docs/forge_loom/) setup you can implement `player-animation-lib` package in *common*.  

ForgeGradle
use the same maven,
```groovy
dependencies {
    (...)
    
    //If you don't want to include the library in your jar, remove the include word
    implementation fg.deobf("dev.kosmx.player-anim:player-animation-lib-fabric:${project.player_anim}")
    
    //Bendy-lib also has a Forge version:
    //runtimeOnly fg.deobf("io.github.kosmx.bendy-lib:bendy-lib-forge:${project.bendylib_version}")
    
    //Forge JarJar only works on MC 1.19. Do not use JarJar on older version!
}
```

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
```
I advice using `AnimationContainer` and setting its animation. (this is null-tolerant)  

To play a keyframe animation from `emotecraft` or `geckolib` json, `dev.kosmx.playerAnim.core.data.gson.AnimationJson` will help you load it.  
`new KeyframeAnimationPlayer(animation)` will play it for you.

To modify/tweak animations, look into `dev.kosmx.playerAnim.api.layered` package, you might implement your own `IAnimation` or extend/modify an existing class.  
`ModifierLayer` will let you add modifiers but that is effectively an `AnimationContainer` layer.

# Model
The player model is made of 6 body parts:  
- head  
- torso  
- right arm  
- left arm  
- right leg  
- left leg

And I added an extra: __body__:  
This is a bone for the whole player, transforming it will transform every part.  
*To move everything up by 2, you only need to move the `body` up.*  

> Most Blockbench player models use the name `body` for the part, I call `torso`. In that case, rename it to `torso` and that will fix the model for the library.  

Part names can be `snake_case` or `camelCase`:  
`right_arm` or `rightArm`, both will work.  

Supported transformations:  
offset, rotation

And bend if bendy-lib is loaded.    
Bend will `bend` the part in the middle, check the `Blender` model to see how.  

The library supports all easings from [easings.net](https://easings.net/#) and constant and linear.  
No easing parameters are supported. (everything was copied from easings.net)  

## Notes
> GeckoLib is not guaranteed to work, but you can try! (It will work most of the time)  
> [molang](https://docs.microsoft.com/minecraft/creator/reference/content/molangreference/) is not supported  

