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
    modRuntimeOnly "net.creeperhost:polylib-fabric:VERSION"
}
