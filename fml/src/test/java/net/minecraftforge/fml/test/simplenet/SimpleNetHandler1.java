package net.minecraftforge.fml.test.simplenet;

import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SimpleNetHandler1 implements IMessageHandler<SimpleNetTestMessage1, SimpleNetTestMessage2>
{
    @Override
    public SimpleNetTestMessage2 onMessage(SimpleNetTestMessage1 message, MessageContext context)
    {
        return null;
    }

}
