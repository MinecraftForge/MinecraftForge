package cpw.mods.fml.common.event;

public class FMLEvent
{
    public final String getEventType()
    {
        return getClass().getSimpleName();
    }
}
