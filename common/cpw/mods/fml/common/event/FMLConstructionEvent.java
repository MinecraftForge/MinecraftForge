package cpw.mods.fml.common.event;

import cpw.mods.fml.common.LoaderState.ModState;
import cpw.mods.fml.common.ModClassLoader;
import cpw.mods.fml.common.discovery.ASMDataTable;

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
