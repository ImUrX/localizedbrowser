# Localized Browser
A mod that makes things nicer for people that don't talk english.

Current features are:
* Base letter shows all possible graphemes with it
* If you use a grapheme it will only show items with that grapheme
* Advanced Japanese support (romaji, hiragana shows katakana stuff, kanji dictionary, and the features above do apply to japanese too)

To-do:
* Pinyin support (chinese one, need to check if taiwan has something different)
* Romaja support (korean one)
* Check if more languages need romanization support or something like japanese.

## Maven
### Fabric or Forge
I suggest using Modrinth as a repository. [Use this official tutorial on how to do it](https://docs.modrinth.com/docs/tutorials/maven/)
#### Fabric
```groovy
dependencies {
    modImplementation "maven.modrinth:better-locale:VERSION-fabric"
}
```
#### Forge (ForgeGradle)
```groovy
dependencies {
    implementation fg.deobf("maven.modrinth:better-locale:VERSION-forge")
}
```
### Architectury or similars
You can use Maven Central to implement only the common part of the mod by doing the following:
```groovy
repositories {
    mavenCentral()
}
dependencies {
    implementation "io.github.imurx.localizedbrowser:better-locale:VERSION"
}
```

### Acknowledgements
* [**wanakana-kt**](https://github.com/esnaultdev/wanakana-kt): is a Kotlin port of [WaniKani/WanaKana](https://github.com/WaniKani/WanaKana)
* [**Kuromoji**](https://github.com/atilika/kuromoji/): is a Japanese morphological analyzer
* [![JProfiler](https://www.ej-technologies.com/images/product_banners/jprofiler_small.png) for the Java profiler they provided me!](https://www.ej-technologies.com/products/jprofiler/overview.html)
