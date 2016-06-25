# Installation Instructions

## Mod Users

Download the latest **Installer** (or **Installer-win** for Windows users) from http://files.minecraftforge.net and follow the installation instructions.

## Mod Devs

Read the [MinecraftForge Documentation](http://http://mcforge.readthedocs.io/)

The Forge source distribution is called the **Minecraft Development Kit (MDK)** download it from http://files.minecraftforge.net and unzip it to a folder.

Open a command prompt, navigate to the directory where you unzipped the **MDK**, and run:

    gradlew setupDevWorkspace

To use Eclipse, run this instead:

    gradlew eclipse 
    
You can install the [Gradle Plugin for Eclipse](https://marketplace.eclipse.org/content/gradle-sts-integration-eclipse) and import the MDK folder.

To get the decompiled classes:

    gradlew setupDecompWorkspace

##  Contributors

    git clone git@github.com:MinecraftForge/MinecraftForge.git
    gradlew setupForge

To use Eclipse, point your Eclipse workspace at the `eclipse` folder.

## Development Requirements 
(For mod devs and contributors)

  - [JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
  - [Gradle](https://docs.gradle.org/current/userguide/installation.html) can be used in place of `gradlew` above if installed.
