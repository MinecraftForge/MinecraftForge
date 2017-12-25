# How to install Forge: For Players

Go to [http://files.minecraftforge.net](http://files.minecraftforge.net)
 and select the minecraft version you wish to get forge for from the list.

You can download the installer for the *Recommended Build* or the
 *Lastest build* there. Latest builds may have newer features  but may be
 more unstable as a result. The installer will attempt to install forge
 into your vanilla launcher environment, where you can then create a new
 profile using that version and play the game!

Here is a short video from Rorax showing how to install and setup Forge.

[![HOWTO Install Forge](https://img.youtube.com/vi/lB3ArN_-3Oc/0.jpg)](https://www.youtube.com/watch?v=lB3ArN_-3Oc)

For support and user questions, visit [http://www.minecraftforge.net](http://www.minecraftforge.net).
 
# How to install Forge: For Modders

If you wish to setup a new mod for Forge, visit
 [http://files.minecraftforge.net](http://files.minecraftforge.net) and
 select the **MDK** download.

This is the `Modder Developer Kit` - basically, an example mod with all
 the tooling needed to create a Forge mod workspace ready for use in your
 IDE of choice.

Here is a short video from @cpw showing how to install and setup the MDK
 in Intellij.

[![HOWTO Install MDK](https://img.youtube.com/vi/PfmlNiHonV0/0.jpg)](https://www.youtube.com/watch?v=PfmlNiHonV0)
 
# How to install Forge: For those wishing to work on Forge itself

If you wish to actually inspect Forge, submit PRs or otherwise work
 with Forge itself, you're in the right place! Clone this (either
 directly, or make a Fork first if you want to make a PR) and run
 ```gradlew.bat setupForge``` or ```gradlew setupForge```. This will download and 
 setup all the bits you need to have a functional Forge workspace.

After that, open a workspace in your favorite IDE and import existing projects
 the projects folder. 
 
Here is a short video from @cpw showing how to install and setup Forge
 in Intellij.
 
[![HOWTO Install MDK](https://img.youtube.com/vi/yanCpy8p2ZE/0.jpg)](https://www.youtube.com/watch?v=yanCpy8p2ZE)

## Pull requests

Pull requests should target the current default branch. Currently, that is 
 the 1.12.x branch for Minecraft 1.12.2.

If you intend to modify a minecraft patch, or add a new one, always run
```gradlew genPatches``` to generate your patch and validate that you
are not doing anything unexpected in your patch.

### Contributor License Agreement
As stated in the (https://github.com/MinecraftForge/MinecraftForge/blob/1.12.x/LICENSE-new.txt)
 file, we require all contributors to acknowledge the Forge Contributor
 License Agreement. Please ensure you have a valid email address
 associated with your github account to do this. If you have previously
 signed it, you should be OK.