# PolyLib

<p align="left">
    <a href="https://www.curseforge.com/minecraft/mc-mods/polylib"><img src="http://cf.way2muchnoise.eu/full_576589_downloads.svg" /></a>
    <a href="https://www.curseforge.com/minecraft/mc-mods/polylib"><img src="http://cf.way2muchnoise.eu/versions/576589.svg" /></a>
    <a href="https://github.com/Direwolf20-MC/BuildingGadgets/blob/master/LICENSE.md"><img alt="GitHub license" src="https://img.shields.io/github/license/CreeperHost/PolyLib"></a>
    <a href="https://github.com/direwolf20-MC/buildinggadgets/issues"><img alt="GitHub issues" src="https://img.shields.io/github/issues/CreeperHost/PolyLib"></a>
    <a href="https://github.com/direwolf20-MC/buildinggadgets/stargazers"><img alt="GitHub stars" src="https://img.shields.io/github/stars/CreeperHost/PolyLib"></a>
    <a href="https://github.com/Direwolf20-MC/BuildingGadgets/commits/master"><img alt="activity" src="https://img.shields.io/github/last-commit/CreeperHost/PolyLib" ></a>
</p>


# Maven
```gradle
repositories {
    maven { url "https://maven.creeperhost.net" }
}
```


### Architectury
```gradle
// Common
dependencies {
    modCompileOnly "net.creeperhost:polylib-fabric:VERSION"
}

// Fabric
dependencies {
    modRuntimeOnly "net.creeperhost:polylib-fabric:VERSION"
}

// Forge
dependencies {
    modRuntimeOnly "net.creeperhost:polylib-forge:VERSION"
}
```

### Important note for resource packs developers
The textures in the textures/gui/dynamic directory are dynamically resized by the gui system.
This is done by slicing the textures in a 3x3 pattern, then the sections are tiled and stitched back together.
If you wish to customise these, they must be made in such a way that they won't look broken when cut up and tiled.
The standard border width is defined as 5 pixes from all sides, anything inside this will be cut up and tiled as part of the texture interior.

### Related note for mod developers
Ideally we want to try to make life easy for the resource pack developers, so try to avoid using dynamic textures as much as possible.
They are fine for things like buttons that come in all shapes and sizes, but for things like gui backgrounds,
use the dynamic texture data generator to generate proper textures based on the dynamic textures.
This is better for performance, and it is better for the resource pack devs.
