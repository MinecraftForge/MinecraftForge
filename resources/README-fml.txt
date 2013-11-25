This is Forge Mod Loader, or FML for short, by cpw.

More information can be found at https://github.com/MinecraftForge/FML/wiki

It is a clean reimplementation of a mod loading system for client, server and
bukkit use, incorporating API implementations of client side ModLoader by
Risugami.

It can be installed on its own, or as part of Minecraft Forge.

----------------------
About Forge Mod Loader
----------------------

Environments and compatibility
==============================
FML covers the three main environments: client, server and bukkit. All
environments share the main mod loading code base, with additional varying hooks
based on the specific environment. Compatibility is almost 100% with all 
ModLoader based mods- any mod with any difficulty (unless noted below) should
be reported as a ticket to the github, please.

Client side mods should be 100% compatible with ModLoader. The only exception is
Optifine, however new versions of optifine are compatible. There are some very
visible changes to the client when FML is installed.

Server side mods are mostly compatible with previous MLMP based incarnations,
however, this compatibility is not guaranteed. Most mods have adapted to FML 
and MLMP is not assured and will cause errors. Ask your mod author to update
to FML.

Bukkit side modding is an adaptation of the server side mod code to the bukkit
environment. Mods need to be recompiled against the bukkit decompilation to work
however there are guides and tools in development to make this process easier.
Consult mcportcentral.co.za for more information.

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

Client notes
============
FML does provide a standard pattern for mods to provide HD textures. This is why
optifine needed integration code. FML supports HD texture packs
up to 128x resolution.

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
minecraft_server.jar placed in the same directory. FML will launch it's patched
copy.

To install on a client, FIRST delete META-INF from the minecraft.jar file, then
copy the contents of the FML or Forge jar file into it. FML and Forge provide 
their own META-INF data which should *not* be deleted.

Forge Installation
==================
This code also ships as a part of Minecraft Forge. You do not need to install it
separately from your Minecraft Forge installation. Minecraft Forge contains the
exact same code as this. Generally, you should not install FML if you are also
installing Minecraft Forge.

-------------------------------------------
Source installation information for modders
-------------------------------------------
This code follows the Minecraft Forge installation methodology. It will apply
some small patches to your MCP extracted source code, giving you and it access 
to some of the data and functions you need to build a successful mod.

Note also that the patches are built against "unrenamed" MCP source code- this
means that you will not be able to read them directly against normal code.

Source pack installation information:

Standalone source installation
==============================

To install this source code for development purposes, extract this zip file 
into an mcp installation containing vanilla jars only. It should create a new
folder "fml" inside that installation.

Be sure that you have both vanilla client jars, and the vanilla server jar installed
as per the MCP install instructions. Both are required for FML to run properly.

Once extracted, run the install.sh or install.bat script provided from the fml
directory. It should decompile and patch your MCP source code for the client
and server.

Forge source installation
=========================
MinecraftForge ships with this code and installs it as part of the forge
installation process, no further action is required on your part.

For reference this is version 6.4.45.953 of FML
for Minecraft version 1.6.4.
