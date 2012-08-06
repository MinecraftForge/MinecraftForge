package cpw.mods.fml.common.network;

public class FMLNetworkException extends RuntimeException
{

    public FMLNetworkException(Exception e)
    {
        super(e);
    }

    public FMLNetworkException()
    {
    }
}
