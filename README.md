# TLA Api
[![Badge showing the amount of downloads on modrinth](https://img.shields.io/badge/dynamic/json?color=2d2d2d&colorA=5da545&label=&suffix=%20downloads%20&query=downloads&url=https://api.modrinth.com/v2/project/gxkkdmTQ&style=flat-square&logo=modrinth&logoColor=2d2d2d)](https://modrinth.com/mod/tla-api)
[![Badge showing the amount of downloads on curseforge](https://img.shields.io/badge/dynamic/json?query=value&url=https://img.shields.io/curseforge/dt/959230.json&label=&logo=curseforge&color=2d2d2d&style=flat&labelColor=F16436&logoColor=2d2d2d&suffix=%20downloads)](https://www.curseforge.com/minecraft/mc-mods/tla-api)
[![Badge linking to issues on github](https://img.shields.io/badge/dynamic/json?query=value&url=https%3A%2F%2Fimg.shields.io%2Fgithub%2Fissues-raw%2Fmattidragon%2Ftlaapi.json&label=&logo=github&color=2d2d2d&style=flat-square&labelColor=6e5494&logoColor=2d2d2d&suffix=%20issues)](https://github.com/MattiDragon/TlaApi/issues)
[![Badge linking to support on discord](https://img.shields.io/discord/760524772189798431?label=&logo=discord&color=2d2d2d&style=flat-square&labelColor=5865f2&logoColor=2d2d2d)](https://discord.gg/26T5KK2PBv)

TLA api is an abstraction layer over recipe viewer apis for minecraft.
It allows mod developers to write their recipe viewer integration once and have it work with multiple recipe viewers.
Currently supported recipe viewers are: [EMI](https://modrinth.com/mod/emi), [REI](https://modrinth.com/mod/rei) and [JEI](https://modrinth.com/mod/jei)

## Usage
### Gradle
TLA api is available on jitpack. Add the following to your build.gradle to use it:
```groovy
repositories {
    maven {
        url "https://jitpack.io"
    }
}

dependencies {
    modImplementation "com.github.mattidragon:TlaApi:${tla_api_version}"
    include "com.github.mattidragon:TlaApi:${tla_api_version}"
}
```

### Entrypoint
To use the api you need to implement the `TlaApiPlugin` interface and add your implementation to the `tla-api` entrypoint.
Note that the entire api is client side only. If you are using split source sets, 
you need to implement the api in the client source set. 
```java
public class MyTlaPlugin implements TlaApiPlugin {
    @Override
    public void register(PluginContext context) {
        // Register your api here
    }
}
```
```json
{
  "entrypoints": {
    "tla-api": [
      "my.package.MyTlaPlugin"
    ]
  }
}
```

### Registering Content
Once you've set up your entrypoint you can begin registering content. 
The entire api is documented using javadocs, so you can use your IDE to explore the api.
The api design is mostly based on EMIs, but I've had to make some changes to accommodate REI and JEI.

### Things to Consider
While the TLA api abstracts everything, you will still need to verify yourself that your code works with all> recipe viewers.
For example, you still need to translate all of your tags for EMI. Some widgets might also render slightly differently.

A useful thing to look for while using the api are the following annotations. 
They help with explaining how the api should be used. 
- `@ApiStatus.Internal`: This means that the part of code is not meant to be used by mod developers.
- `@ImplementationOnly`: This means that the part of code is only meant to be used by the implementation of the api for recipe viewers.
Unless you are creating your own recipe viewer integration, you should not use these parts of the api.
- `@PluginOnly`: This means that the part of code is only meant to be by plugins and not by the implementation of the api for recipe viewers.
- `@ImplementationsExtend`: This indicates that the interface is implemented by the implementations of the api and plugins generally shouldn't implement it.
- `@PluginsExtend`: This indicates that the interface is implemented by plugins and the implementations of the api generally shouldn't implement it.

## Questions
#### Q: Does this allow mods that only support one viewer to work on both?
A: No. This api just makes it easier for mod developers to support all recipe viewers.
#### Q: Why is this api client side only?
A: EMIs entire api is client side only. REI has a server side api, but it doesn't contain any features that TLA uses.
#### Q: Are there any examples?
A: Yes, you can find the test mod [here](https://github.com/mattidragon/TlaApi/tree/1.20.4/src/testmod).
It does a few things you wouldn't normally do, but generally does a good job of showing off the api.
