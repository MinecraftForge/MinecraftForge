This is Forge Mod Loader, or FML for short, by cpw.

More information can be found at https://github.com/cpw/FML/wiki

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
Optifine, with the Cx series have started performing the actions necessary for 
FML compatibility. This means optifine will work well alongside an FML or 
Minecraft Forge installation. FML will detect and ensure the good operation of
Optifine (you can see it in your client as an additional data line on the 
bottom left).

Client notes
============
FML does provide a standard pattern for mods to provide HD textures. This is why
optifine needed integration code. FML supports very very basic HD texture packs
up to 128x resolution (some 256x resolution packs work but not all). For full
featured HD optifine is recommended.

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
To install this on it's own into a minecraft environment, simply copy the
contents of the fml zip file into the minecraft jar file, using your preferred
zip management tool (I recommend 7 zip on windows).

For servers: the minecraft jar file is minecraft_server.jar.
For clients: the minecraft jar file is minecraft.jar. You will additionally need
to delete the "META-INF" folder in the minecraft.jar file.
For bukkit: the mcportcentral custom builds of craftbukkit contain all you need
already. Please refer to mcportcentral.co.za for more information.

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
directory. It should decompile and patch your MCP source code for the client
and server.

Forge source installation
=========================
MinecraftForge ships with this code and installs it as part of the forge
installation process, no further action is required on your part.

For reference this is version @MAJOR@.@MINOR@.@REV@.@BUILD@ of FML
for Minecraft version @MCVERSION@.
