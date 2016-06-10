# Installation Instructions

## Mod Users

Download the latest **Installer** (or **Installer-win** for Windows users) from http://files.minecraftforge.net and follow the installation instructions.

## Mod Devs

Read the [MinecraftForge Documentation]([MinecraftForge Documentation](http://http://mcforge.readthedocs.io/)

The Forge source distribution is called the **Minecraft Development Kit (MDK)** download it from http://files.minecraftforge.net and unzip it to a folder.

Open a command prompt, navigate to the directory where you unzipped the **MDK**, and run:

If you have [Gradle](https://docs.gradle.org/current/userguide/installation.html)

    gradle setupDevWorkspace

If you DO NOT have Gradle installed:

**Windows**

    ./gradlew.bat setupDevWorkspace

**MacOS/Linux**

    ./gradlew setupDevWorkspace

If you wish to use the Eclipse IDE, run this instead:

    gradle eclipse 
    
You can also install the [Gradle Plugin for Eclipse](https://marketplace.eclipse.org/content/gradle-sts-integration-eclipse) and import the Forge source folder as a Gradle project.

To get the decompiled classes:

If you have Gradle:

    gradle setupDecompWorkspace

If you DO NOT have Gradle installed:

**Windows**

    ./gradlew.bat setupDecompWorkspace

**MacOS/Linux**

    ./gradlew setupDecompWorkspace

##  Contributors

If you don't have Gradle installed. , use `gradlew.bat` or `./gradlew` instead of `gradle` below.

    git clone git@github.com:MinecraftForge/MinecraftForge.git
    gradle setupForge

To use Eclipse, point your Eclipse workspace at the `eclipse` folder.

## Development Requirements 
(For mod devs and contributors)

  - You must have a JDK installed and accessible.
  - If you do not wish to use the gradle wrapper, you can install Gradle from http://www.gradle.org/ .
 
