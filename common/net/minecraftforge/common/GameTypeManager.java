package net.minecraftforge.common;

import java.util.List;
import net.minecraft.world.EnumGameType;
import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GameTypeManager
{
    public static final List<EnumGameType> customGameTypes = Lists.newArrayList();

    /**
     * Used by EnumGameType.
     * @see EnumGameType#getByID(int)
     */
    public static EnumGameType getCustomByID(int id)
    {
        for (EnumGameType customGameType : customGameTypes)
        {
            if (customGameType.getID() == id)
            {
                return customGameType;
            }
        }
        
        return EnumGameType.SURVIVAL;
    }

    /**
     * Used by EnumGameType.
     * @see EnumGameType#getByName(String)
     */
    @SideOnly(Side.CLIENT)
    public static EnumGameType getCustomByName(String name)
    {
        for (EnumGameType customGameType : customGameTypes)
        {
            if (customGameType.getName().equals(name))
            {
                return customGameType;
            }
        }
        
        return EnumGameType.SURVIVAL;
    }
    
    /**
     * To be called sometime before the server is started or the Singleplayer button in clicked.
     * @param gameType The custom EnumGameType to add.
     */
    public static void registerGameType(EnumGameType gameType)
    {
        customGameTypes.add(gameType);
    }
}
