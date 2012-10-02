package cpw.mods.fml.common;

public class WrongMinecraftVersionException extends RuntimeException
{

    public ModContainer mod;

    public WrongMinecraftVersionException(ModContainer mod)
    {
        this.mod = mod;
    }

}
