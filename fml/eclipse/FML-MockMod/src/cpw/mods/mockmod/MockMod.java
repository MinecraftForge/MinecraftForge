package cpw.mods.mockmod;

import net.minecraft.src.ItemBlock;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Block;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.ModMetadata;

@Mod(modid="MockMod", name="Mock Mod",version="1.2.3", dependencies="before:mod_testMod", useMetadata=true)
public class MockMod
{
    public class TestItem extends ItemBlock
    {

        public TestItem(int id)
        {
            super(id);
        }

    }

    @Instance
    public static MockMod myInstance;
    
    @Metadata
    private ModMetadata meta;
    
    @Block(name="MyBlock", itemTypeClass=TestItem.class)
    private MockBlock myBlock;
    
    @Init
    public void init()
    {
        System.out.printf("Hello from mockmod init : %s %s\n", myInstance, meta);
    }
}
