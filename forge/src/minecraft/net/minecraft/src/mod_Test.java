package net.minecraft.src;
import java.util.Map;

public class mod_Test extends BaseModMp
{
    public static Item xinstick;

    public String getVersion()
    {
        return "Test";
    }

    public void load()
    {
    }

    public mod_Test()
    {
    	 ModLoader.addRecipe(new ItemStack(Block.ice, 64, 0), new Object[]
         {
    		 "s", 's', Block.dirt
         });
    	 ModLoader.addRecipe(new ItemStack(Block.obsidian, 64, 0), new Object[]
    	 {
    	 	 "ss", 's', Block.dirt
    	 });
    	 ModLoader.addRecipe(new ItemStack(Block.netherrack, 64, 0), new Object[]
    	 {
    	 	 "s", "s", 's', Block.dirt
    	 });
    	 ModLoader.addRecipe(new ItemStack(Block.ice, 64, 0), new Object[]
    	 {
    	  	 "s ", " s", 's', Block.dirt
    	 });
    }
}
