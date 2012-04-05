This is Forge Mod Loader, or FML for short, by cpw.

It is a clean reimplementation of a mod loading system for server side use,
incorporating API implementations of client side ModLoader by Risugami.

It can be installed on its own, or as part of Minecraft Forge.

If you have downloaded the server zip file you can install it as follows:

Installation
============
To install this on it's own into a minecraft server, simply copy the contents
of the fml server zip file into the minecraft_server.jar file.

Forge Installation
==================
This code also ships as a part of Minecraft Forge. You do not need to install it
separately from your Minecraft Forge installation.

NOTE: there is no companion client side installation. This is, at present, a
server-side only installation.

-------------------------------------------
Source installation information for modders
-------------------------------------------
This code follows the Minecraft Forge installation methodology. It will apply
some small patches to your MCP extracted source code, giving you and it access 
to some of the data and functions you need to build a successful mod.

It uses the fernflower decompiler to decompile minecraft, and the patches will
only work with fernflower decompiled source. It will attempt to download 
fernflower and fail if it cannot. You can also install fernflower manually
if you wish.

Note also that the patches are built against "unrenamed" MCP source code- this
means that you will not be able to read them directly against normal code.

Source pack installation information:

Standalone source installation
==============================

To install this source code for development purposes, extract this zip file 
into an mcp installation containing vanilla jars only. It should create a new
folder "fml" inside that installation.

Once extracted, run the install.sh or install.bat script provided from the fml
directory. It should decompile and patch your MCP source code for the server.

Forge source installation
=========================
MinecraftForge should ship with this code and install it as part of the forge
installation process, no further action should be required on your part.


For reference this is version @MAJOR@.@MINOR@.@REV@.@BUILD@ of FML
for Minecraft version @MCVERSION@.
