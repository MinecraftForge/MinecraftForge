-------------------------------------------
Source installation information for modders
-------------------------------------------
This code follows the Minecraft Forge installation methodology. It will apply
some small patches to the vanilla MCP source code, giving you and it access
to some of the data and functions you need to build a successful mod.

Note also that the patches are built against "unrenamed" MCP source code (AKA
"srgnames") - this means that you will not be able to read them directly
against normal code.

Minecraft Forge ships with Forge source code and installs it as part of the
Forge installation process; no separate action is required on your part.


Standalone Source Installation Procedure
========================================

You may want to watch LexManos' Install Video before you begin:
https://www.youtube.com/watch?v=8VEdtQLuLO0&feature=youtu.be

 1. Extract the MDK zip file to a folder

 2. Open a command line and browse into the unzipped MDK folder where you
    will see a file called 'gradlew'. This is the Gradle executable used for
    doing builds.

 3. In the command line, type:

    Windows: "gradlew setupDecompWorkspace"
    Linux/Mac OS: "./gradlew setupDecompWorkspace"

 4. After the Gradle tasks has finished, setup either Eclipse or IntelliJ
    IDEA using the instructions in the sections below.

Note: If you do not care about seeing Minecraft's source code you can
replace the "setupDecompWorkspace" task with one of the following:

   "setupDevWorkspace":
      Will patch, deobfuscate, and gather required assets to run Minecraft,
      but will not generated human-readable source code.

   "setupCIWorkspace":
      Same as setupDevWorkspace but will not download any assets. This is
      the fastest option for CI build servers because it does the least work.


Eclipse Setup
==============================

 1. In the command line, run the following to generate a workspace:

    Windows: "gradlew eclipse"
    Linux/Mac OS: "./gradlew eclipse"

 2. Open Eclipse and switch your workspace to /eclipse/


IntelliJ IDEA Setup
==============================

 1. Open IDEA, select File > Import Project, then select the MDK build.gradle
    file for import

 2. Once it's finished importing, close IDEA before continuing.

 3. In the command line, run the following command to generate Run/Debug
    profiles:

    Windows: "gradlew genIntellijRuns"
    Linux/Mac OS: "./gradlew genIntellijRuns"

 4. Open IDEA again. It should automatically start on your project.


Troubleshooting and Tips
==============================

If at any point you are missing libraries in your IDE, or you've run into
problems:

 1. Run "gradlew --refresh-dependencies" to refresh the local cache.
 2. Run "gradlew clean" to reset everything {this does not effect your code}
 3. Do the above "Standalone Source Installation" procedure again.

Should it still not work, join IRC channel #ForgeGradle on EsperNet for more
information about the Gradle environment.

TIP:

When using the Decomp workspace, the Minecraft source code is NOT added to your
workspace in a editable way. Minecraft is treated like a normal Library; the
sources are there for documentation and research purposes. They are usually
accessed under the 'referenced libraries' section of your IDE.

TIP:

For more details (updated more often) refer to the Forge Forums:
http://www.minecraftforge.net/forum/index.php/topic,14048.0.html

