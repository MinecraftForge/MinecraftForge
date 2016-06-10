# Installation Instructions

## Mod Users

Download the latest **Installer** (or **Installer-win** for Windows users) from http://files.minecraftforge.net and follow the installation instructions.

## Mod Devs

Read the [MinecraftForge Documentation]([MinecraftForge Documentation](http://http://mcforge.readthedocs.io/)

The Forge source distribution is called the **Minecraft Development Kit (MDK)** download it from http://files.minecraftforge.net and unzip it to a folder.

Open a command prompt, navigate to the directory where you unzipped the **MDK**, and run:

    gradle setupDevWorkspace

To use Eclipse, run this instead:

    gradle eclipse 
    
You can install the [Gradle Plugin for Eclipse](https://marketplace.eclipse.org/content/gradle-sts-integration-eclipse) and import the MDK folder.

To get the decompiled classes:

    gradle setupDecompWorkspace

##  Contributors

    git clone git@github.com:MinecraftForge/MinecraftForge.git
    gradle setupForge

To use Eclipse, point your Eclipse workspace at the `eclipse` folder.

## Development Requirements 
(For mod devs and contributors)

  - JDK
  - [Gradle](https://docs.gradle.org/current/userguide/installation.html) is recommended, but you can use  `gradlew` (or `gradlew.bat`) instead.
