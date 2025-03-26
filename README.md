# PlayerAnimator

PlayerAnimator is a minecraft library to animate the **player** while trying to break as few mods as possible.    
If you want to add new entities, use [Geckolib](https://geckolib.com/#mods).

You can use [Zigy's Player Animator API](https://github.com/ZigyTheBird/ZigysPlayerAnimatorAPI/wiki/Getting-Started) in order to play animations from the server, with advanced options and good documentation.  
You can also use [Emotecraft's server-side API](https://github.com/KosmX/emotes/tree/dev/emotesAPI/src/main/java/io/github/kosmx/emotes/api/events/server).

## Official projects
**GitHub project** https://github.com/KosmX/minecraftPlayerAnimator  
**Modrinth** https://modrinth.com/mod/playeranimator  
**CurseForge** https://www.curseforge.com/minecraft/mc-mods/playeranimator  
**KosmX's Maven (for API use)** https://maven.kosmx.dev/dev/kosmx/player-anim/  
> Avoid downloading the library from other sources!  


## Example mods
[Fabric example](https://github.com/KosmX/fabricPlayerAnimatorExample)  
[Forge example](https://github.com/KosmX/forgePlayerAnimatorExample)  


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
    //You can find the latest version in https://maven.kosmx.dev/dev/kosmx/player-anim/player-animation-lib-fabric/
    include modImplementation("dev.kosmx.player-anim:player-animation-lib-fabric:${project.player_anim}")
    
    //You might want bendy-lib. playerAnimator will wrap it.
    //include modRuntimeOnly("io.github.kosmx.bendy-lib:bendy-lib-fabric:${project.bendylib_version}")
}

```
If you use [architectury](https://docs.architectury.dev/docs/forge_loom/) setup you can implement `player-animation-lib` package in *common*.  

NeoGradle  
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
    
    //You can find the latest version in https://maven.kosmx.dev/dev/kosmx/player-anim/player-animation-lib-forge/
    implementation "dev.kosmx.player-anim:player-animation-lib-forge:${project.player_anim}"
    
    //You might want bendy-lib. playerAnimator will wrap it.
    //runtimeOnly "io.github.kosmx.bendy-lib:bendy-lib-forge:${project.bendylib_version}"
    
    //JarJar only works on MC 1.19+. Do not use JarJar on older versions!
}
```

**For more advanced things, you might use `anim-core` package**.  
It is a minecraft-independent module, containing the animation format and the layers but no mixins.  
Also it is **not** a minecraft mod, do not use `modImplementation` on this.  

> [!CAUTION]
> Do **not** shadow the library in your mod, this library can not be loaded multiple times safely. (even from different packages)  
> The license would allow it, but it would break many things.  
You may use `include`(fabric any version) or JarJar(forge 1.19.1+).

# Structure 
The library has an animation list of currently *played* animations
Higher priority animations will override others, but can be transparent...  

To add an animation to the player, use 
```java
AnimationStack animationStack = PlayerAnimationAccess.getPlayerAnimLayer(clientPlayer);
animationStack.addAnimLayer(...);
```
I advice using `ModifierLayer` and setting its animation. (this is null-tolerant)
`ModifierLayer` is an `AnimationContainer` but with modifiers and fade-in/out.

To play a keyframe animation from `emotecraft` or `geckolib` json, `dev.kosmx.playerAnim.core.data.gson.AnimationJson` will help you load it.  
`new KeyframeAnimationPlayer(animation)` will play it for you.

To modify/tweak animations, look into `dev.kosmx.playerAnim.api.layered` package, you might implement your own `IAnimation` or extend/modify an existing class.  
`ModifierLayer` will let you add modifiers. It is effectively an `AnimationContainer` layer.  

You might find some usage in the [fabric testmod](https://github.com/KosmX/minecraftPlayerAnimator/blob/dev/minecraft/fabric/src/testmod/java/dev/kosmx/animatorTestmod/PlayerAnimTestmod.java)  
The forge usage is similar. For most fabric users, you can use [linkie](https://linkie.shedaniel.me/mappings) to translate mojmap to Yarn.  

## Animate
You can use GeckoLib or *Emotecraft* format to create animations.  
Be careful, the model has to match the player or the animation won't work as it should.  
Here are some [models](https://github.com/KosmX/emotes/tree/dev/blender) you can use for animation.  

To load an animation, put the file(s) into `assets/modid/player_animations/`  
Then you can get the animation with `dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry#getAnimation()`  
[Molang](https://docs.microsoft.com/minecraft/creator/reference/content/molangreference/) is not currently supported, but it may be soon.   

The library supports all easings from [easings.net](https://easings.net/#) and constant and linear.  

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

Part names can be `snake_case` or `camelCase`:  
`right_arm` or `rightArm`, both will work.  

And bend if bendy-lib is loaded.    
Bend will `bend` the part in the middle.  

## Feel free to ask for help in the Emotecraft Discord's dev section:  
https://discord.com/invite/x22jkxRpsD
