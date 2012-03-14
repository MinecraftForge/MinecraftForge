package net.minecraft.src;
import net.minecraft.src.steamvent.*;
import net.minecraft.src.forge.*;
import java.io.File;
import java.util.*;
import net.minecraft.client.Minecraft;

public class mod_Steamvent extends BaseModMp
{
    public static InfiProps props;
    public static boolean resolveConflicts;
    
    public static Block steamPiston;
    public static Block steamPistonExtension;
    public static Block steamPistonMoving;

    public String getVersion()
    {
        return "v0.0.1";
    }

    public mod_Steamvent()
    {
    	
    	//MinecraftForgeClient.preloadTexture("/oretex/ores.png");
    	
    	ModLoader.registerBlock(steamPiston);
    	ModLoader.registerBlock(steamPistonExtension);
    	ModLoader.registerBlock(steamPistonMoving);
    	addNames();
    }
    
    public void addNames() {
    	//ModLoader.addLocalization("mineralCopper.name", "Copper Ore");
    }

    public void load() {}

    static
    {
    	/*File me = new File( (new StringBuilder().append(Minecraft.getMinecraftDir().getPath())
				.append('/').append("mDiyo").toString() ) );
        me.mkdir();
		props = new InfiProps((new File((new StringBuilder()).append(Minecraft.getMinecraftDir().getPath())
				.append('/').append("mDiyo").append('/').append("OrizonIDs.cfg").toString())).getPath());
		props = PropsHelperOrizon.InitProps(props);
		PropsHelperOrizon.getProps(props);
		props = new InfiProps((new File((new StringBuilder()).append(Minecraft.getMinecraftDir().getPath())
				.append('/').append("mDiyo").append('/').append("OrizonWorldGen.cfg").toString())).getPath());
		props = PropsHelperOrizon.InitSpawn(props);
		PropsHelperOrizon.getSpawn(props);
		
		if(resolveConflicts)
			PropsHelperOrizon.resolveIDs(props);*/
		
    	steamPiston = new SteamPistonBase(233, 167, false).setBlockName("steamPistonBase").setRequiresSelfNotify();
        steamPistonExtension = (BlockPistonExtension)(new BlockPistonExtension(234, 167)).setRequiresSelfNotify();
        steamPistonMoving = new SteamPistonMoving(236);
    }
}
