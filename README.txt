*** HOW TO INSTALL ***
For Mod Users:

Copy all files from this zip to your minecraft.jar, thats it.
Thats it, you're done, during first run FML will download all needed libraries for you.

For Mod Devs:

The install scripts should take care of everything for you, you do NOT need to 
have MCP installed before running install.cmd/sh as Forge will download it for you.

Just start the install.cmd/.sh, MCForge will install itself into the proper
locations and copy all needed files, as well as modifying the needed baseclasses.

Forge also includes a snapshot of the MCP mapings, this may not be the current 
version of the mapings. But you must use the provided mapings in order for the 
patch files to work together.


Requirements:
  You must ahve a JDK installed and accessible, there is a big in java 1.6_21 that 
  causes compile errors, just update your jdk to fix them.

For Mac users: 
  It should automatically set the execution bits on mcp/runtime/bin/astyle-osx, 
  if it does not, please do so manually: chmod +x mcp/runtime/bin/astyle-osx
  
For Linux users:
  Astyle is required for the patches to apply properly, so either install wine, or astyle
  Instalation instructions are distro specific, so I can not give them here.