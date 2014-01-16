package cpw.mods.fml.test.simplenet;

import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class SimpleNetHandler2 implements IMessageHandler<SimpleNetTestMessage2, SimpleNetTestMessage1>
{
    @Override
    public SimpleNetTestMessage1 onMessage(SimpleNetTestMessage2 message, MessageContext context)
    {
        return null;
    }

}
