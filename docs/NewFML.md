# FML

Entrypoint : ```FMLServiceProvider.onLoad```

- Verify environment
-- Check all libraries are present: ```accesstransformer```, ```coremod```, ```deobfuscator```
-- Check versions are suitable: ```modlauncher```, ```accesstransformer```, ```coremod```, ```deobfuscator```
- Find deobfuscated and patched MC
-- If not found, trigger job to generate that (loaded later at FMLLaunchProvider.launch)

Injected arguments:

- ```mods``` : CSV list of mod files to load
- ```modlist``` : JSON formatted list of mod files in a maven-like repository format

Entrypoint : ```FMLServiceProvider.initializer```
- Setup paths
-- ```mods``` folder
-- ```config``` folder
- FML config file read (JSON?)
- Trigger initial loading

Entrypoint : ```FMLLaunchProvider.launch```
- Launches the patched and deobfuscated game
- - If that wasn't found it'll be generated from the vanilla jar and patch/deobf files (see above)
- Possible we'll use an alternative launch for the development environment

(A goal is that we pre-generate this JAR during Forge installation or modpack installation)

## Loading

- First we discover all mods
- - IModLocator instances present in system are found and queried to compile a master list of all mod artifacts of the three types
- LANGPROVIDERs are added to the supported language system : IModLanguageProvider
- The MODS from the list have their META-INF/mods.json file queried for mod instances to load
- - MODS are cross-referenced for their language being available
- - MODS are cross-referenced for their libraries being available
- - MODS are cross-referenced for their MOD dependencies being available
- - MODS with META-INF/coremods.json specification are loaded into the COREMOD system
- - MODS with META-INF/accesstransformer.json are loaded into the ACCESSTRANSFORMER system
- - MODS are enqueued to the background loading system
- FMLLaunchProvider will be triggered from the ModLauncher to start the game

## Game loading

- Events triggered from loadGame will launch various phases of modloading as before
-- Deprecation of old "FML" events in favour of Forge events
-- If a mod hasn't completed scanning there might be a pause waiting for it to complete before loading (testing indicates this should be a very rare occurrence and isn't worse than existing status quo where all scanning and loading is on-thread)

### ```IModLocator```

Finds mods from various sources of various types
manifest is queried for type: FMLModType

- MOD : this file will be scanned as a mod. This is default if nothing is present
- LIBRARY : this file will be added to the classpath and no further processing is done
- LANGPROVIDER : this file will be loaded as a language provider

#### ```ModsFolderLocator```

- scans for jar files from mods folder
-- able to load all types
-- loads in all environments
-- maybe able to do runtime name transformation for dev time?

Standard mechanism to find and load mods

#### ```ExplodedDirectoryLocator```

- loads a directory as a mod
-- only able to load mods
-- should only load in dev env

Intended as a mechanism to allow development of mods easily by pointing at a compiled output directory

#### ```ArgumentModsLocator``` : NYI

- loads mods from the arguments on the command line
-- able to load all types
-- loads in all environments

### ```IModLanguageProvider```

Provides mod and language provider services

- Provides a means to transform a Mod File (from the IModLocator) into a list of modcontainer objects based on mods.json
- Scanning work will be done in background threads
- Language providers can be provided as JARs via the modlocator for ease of distribution

#### ```FMLModLanguageProvider```

This is the standard Java @Mod implementation provider

- provided by default (always supported)
- All scanning for @Mod will be done in the background thread

#### ```ScalaModLanguageProvider```

This is the scala language provider. It'll be provided separately as a JAR mod download.

- API contract will need definition

Other languages? Kotlin? Alternative Scala versions? Javascript?

## Coremods

Coremods are now forcefully separated into _injection_ and _runtime_ phases.

### Injection

Injection is the act of intercepting the loading of a class and modifying the inbound class with alterations such as additions, replacements and deletions

Injections will be written using Javascript. Two functions are required: a function providing the list of injection sites, and the function to transform the injection site.

All injection sites will need to be pre-specified by the coremod in their metadata. Lookup services should be provided in the Javascript API.

Javascript API for common coremod injection tasks needs to be developed, to allow common best-practice tooling for these complex tasks.

Runtime coremod code (code that has been injected) is Java as usual.

## Background loading thread

Early in loading, a background thread is triggered which will perform scanning tasks for common resources, such as @Mod instances and other annotations. This thread will also be responsible for loading any cached resources, either those defined at build time or pre-computed from previous runs. It may also be tasked with writing those precomputed resources.

There will be a IPreLoaderJobProvider that will allow extension of the scope of these tasks


