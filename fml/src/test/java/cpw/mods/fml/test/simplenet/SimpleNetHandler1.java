package cpw.mods.fml.test.simplenet;

import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class SimpleNetHandler1 implements IMessageHandler<SimpleNetTestMessage1, SimpleNetTestMessage2>
{
    @Override
    public SimpleNetTestMessage2 onMessage(SimpleNetTestMessage1 message, MessageContext context)
    {
        return null;
    }

}
