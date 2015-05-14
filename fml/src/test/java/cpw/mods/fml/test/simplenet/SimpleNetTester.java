package cpw.mods.fml.test.simplenet;

import static org.junit.Assert.*;
import org.junit.Test;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class SimpleNetTester {

    @Test
    public void test()
    {
        SimpleNetworkWrapper simpleChannel = NetworkRegistry.INSTANCE.newSimpleChannel("TEST");
        simpleChannel.registerMessage(SimpleNetHandler1.class, SimpleNetTestMessage1.class, 1, Side.SERVER);
        simpleChannel.registerMessage(SimpleNetHandler2.class, SimpleNetTestMessage2.class, 2, Side.CLIENT);
        assertTrue("Hello", true);
    }

}
