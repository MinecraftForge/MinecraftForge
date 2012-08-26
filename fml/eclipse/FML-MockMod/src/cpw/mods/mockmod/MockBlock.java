package cpw.mods.mockmod;

import cpw.mods.fml.common.Mod.Instance;
import net.minecraft.src.Block;
import net.minecraft.src.Material;

public class MockBlock extends Block
{
    @Instance("mod_testMod")
    public static Object tstInstance;

    public MockBlock(int id)
    {
        super(id,Material.field_76259_v);
    }
}
