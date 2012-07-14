package cpw.mods.mockmod;

import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Block;

@Mod(modid="MockMod", name="Mock Mod",version="1.2.3", dependsOn="noone", useMetadata=true)
public class MockMod
{
    @Block(typeClass="myclass",itemTypeClass="",name="MyBlock")
    private Object myBlock;
}
