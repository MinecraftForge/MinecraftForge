package net.minecraftforge.fml.test.simplenet;

import static org.junit.Assert.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import org.junit.Test;

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
