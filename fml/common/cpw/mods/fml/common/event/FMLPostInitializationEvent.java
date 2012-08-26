package cpw.mods.fml.common.event;

import com.google.common.base.Throwables;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState.ModState;

public class FMLPostInitializationEvent extends FMLStateEvent
{
    public FMLPostInitializationEvent(Object... data)
    {
        super(data);
    }

    @Override
    public ModState getModState()
    {
        return ModState.POSTINITIALIZED;
    }

    public Object buildSoftDependProxy(String modId, String className)
    {
        if (Loader.isModLoaded(modId))
        {
            try
            {
                Class<?> clz = Class.forName(className,true,Loader.instance().getModClassLoader());
                return clz.newInstance();
            }
            catch (Exception e)
            {
                Throwables.propagateIfPossible(e);
                return null;
            }
        }
        return null;
    }
}
