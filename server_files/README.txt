If you need help with your Forge server, join our Discord or forums:
- https://discord.minecraftforge.net
- https://forums.minecraftforge.net

Quick start guide
=================
The steps will vary depending on if you're self-hosting or using a hosting provider:

Self-hosting
------------
On Windows, start the server by double-clicking the run.bat file.
On Linux or macOS, start the server by running the run.sh script.

- To change the amount of RAM allocated to the game, edit the user_jvm_args.txt file.
- To hide the GUI, edit the run.bat or run.sh file and refer to its instructions.
- To change server settings, edit the server.properties file and config files in the config folder.

Hosting providers
-----------------
The steps will vary depending on your hosting provider and what panel they use.

Some providers require you to install Forge through an option in their panel, others require you to upload the files
yourself and select a jar file. It's recommended to install Forge through the panel if possible, as it'll be easier.

If you need to select a jar file, upload your Forge server install (all files in the folder this readme is in) and
select the shim jar file.

If you're unsure, contact your hosting provider's or server panel's support.

Performance tuning
==================
Here are some tips and advice to improve server performance:

- Use the latest version of MC, Forge and mods when possible. Newer versions usually have performance improvements.

- Turn down the view distance and simulation distance in the server.properties file. Simulation distance should be the
  same as or marginally lower than view distance.

- Don't allocate excessive amounts of RAM to the game - especially if self-hosting. Too much can cause lag, as it could
  cause resource contention with other things running on the same machine, such as the OS, drivers and other apps.

- When using custom JVM args, test the before and after. There isn't a one-size-fits-all solution, and some settings
  might make things worse with your specific hardware and combination of mods.

- Use a profiler (such as the Spark mod by lucko) to find the cause of lag. Some mods may be poorly optimised or simply
  do a lot of things. When you find the cause, you may want to disable that specific part of the mod in its config file,
  check for an update, report the issue to the mod author, find an alternative and/or remove the mod entirely.

- Contrary to popular belief, modern Minecraft isn't single-threaded. After a certain amount of RAM, you may get better
  performance from adding more fast CPU cores rather than adding more RAM.

For more help with performance, please ask on the forums or Discord.
