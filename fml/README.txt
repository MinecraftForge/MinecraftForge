This is Forge Mod Loader, or FML for short, by cpw.

More information can be found at https://github.com/MinecraftForge/FML/wiki

It is a clean reimplementation of a mod loading system for client and server.

It can be installed on its own, or as part of Minecraft Forge.

----------------------
About Forge Mod Loader
----------------------

Environments and compatibility
==============================
FML covers two main environments: client and server. All environments share
the main mod loading code base, with additional varying hooks based on the 
specific environment.

There are some very visible changes to the client when FML is installed.

There are some bukkit compatibility hooks available, consult MCPC+, now known as Cauldron
for more information.

Minecraft Forge in all cases bundles FML as it's modloading technology of choice
because FML is open source, freely distributable, and can be easily updated by
contributors through github.

Notable integrations
====================
Optifine has FML compatibility. It varies from Optifine release to release, however
in general it will work well alongside an FML or 
Minecraft Forge installation. FML will detect and ensure the good operation of
Optifine (you can see it in your client as an additional data line on the 
bottom left).

Mod information
===============
FML exposes the mod information through a mod list visible on the main screen as
well as some small branding enhancements. For full data mods need to provide an
information file. This file is a standard format so hopefully tools providing
launch capabilities can also leverage this content.

-------------------------------
Binary installation information
-------------------------------
If you have downloaded a binary zip file you can install it as follows (client
or server):

Installation
============
To install on a server, simply execute the FML or Forge jar file, with a copy of
minecraft_server-@MC_VERSION@.jar placed in the same directory. FML will launch
it's patched copy.

To install on a client, run the installer by executing java -jar <installer>.jar.
It will identify the location of you minecraft installation (this can be customized)
and create a new profile "FML" there.

Forge Installation
==================
This code also ships as a part of Minecraft Forge. You do not need to install it
separately from your Minecraft Forge installation. Minecraft Forge contains the
exact same code as this. You should not install FML if you are also installing
Minecraft Forge.

-------------------------------------------
Source installation information for modders
-------------------------------------------
This code follows the Minecraft Forge installation methodology. It will apply
some small patches to the vanilla MCP source code, giving you and it access 
to some of the data and functions you need to build a successful mod.

Note also that the patches are built against "unrenamed" MCP source code (aka
srgnames) - this means that you will not be able to read them directly against
normal code.

Source pack installation information:

Standalone source installation
==============================

To install this source code for development purposes, extract this zip file.
It ships with a demonstration mod. Run "gradle setupDevWorkspace" to create
a gradle environment primed with FML. Run gradle eclipse or gradle idea to
create an IDE workspace of your choice.
Refer to ForgeGradle for more information about the gradle environment

Forge source installation
=========================
MinecraftForge ships with this code and installs it as part of the forge
installation process, no further action is required on your part.

For reference this is version @MAJOR@.@MINOR@.@REV@.@BUILD@ of FML
for Minecraft version @MCVERSION@.
