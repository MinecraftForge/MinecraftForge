package cpw.mods.fml.common.network;

import cpw.mods.fml.relauncher.Side;

public abstract class NetworkSide {
    public static final ServerSide SERVER = new ServerSide();
    public static final ClientSide CLIENT = new ClientSide();
    public final Side side;
    NetworkSide(Side side) {
        this.side = side;
    }

    public static final class ServerSide extends NetworkSide {
        private ServerSide()
        {
            super(Side.SERVER);
        }
    }

    public static final class ClientSide extends NetworkSide {
        private ClientSide()
        {
            super(Side.CLIENT);
        }
    }
}
