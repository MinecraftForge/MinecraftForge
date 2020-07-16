package net.minecraftforge.debug.world;

import net.minecraft.entity.EntityType;
import net.minecraft.world.raid.Raid;
import net.minecraftforge.fml.common.Mod;

@Mod("raid_enum_test")
public class RaidEnumTest 
{
    private static final boolean ENABLE = false;
	
    public RaidEnumTest()
    {
        if (ENABLE)
            Raid.WaveMember.create("thebluemengroup", EntityType.ILLUSIONER, new int[]{0, 5, 0, 1, 0, 1, 0, 2});
    }
}
