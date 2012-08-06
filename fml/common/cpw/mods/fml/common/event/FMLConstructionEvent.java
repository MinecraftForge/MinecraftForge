package cpw.mods.fml.common.event;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Table;

import cpw.mods.fml.common.LoaderState.ModState;
import cpw.mods.fml.common.ModClassLoader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import cpw.mods.fml.common.discovery.ModDiscoverer;

public class FMLConstructionEvent extends FMLStateEvent
{
    private ModClassLoader modClassLoader;
    private ASMDataTable asmData;

    public FMLConstructionEvent(Object... eventData)
    {
        this.modClassLoader = (ModClassLoader)eventData[0];
        this.asmData = (ASMDataTable) eventData[1];
    }

    public ModClassLoader getModClassLoader()
    {
        return modClassLoader;
    }

    @Override
    public ModState getModState()
    {
        return ModState.CONSTRUCTED;
    }

    public ASMDataTable getASMHarvestedData()
    {
        return asmData;
    }
}
