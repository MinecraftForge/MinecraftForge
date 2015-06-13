package net.minecraftforge.fml.debug.simplenet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.debug.simplenet.AsyncTestMessage.Response;

public class TestMessage implements IMessage
{
    
    @Override
    public void fromBytes(ByteBuf buf)
    {
        
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        
    }
    
    static class Handler implements IMessageHandler<TestMessage, IMessage>
    {

        @Override
        public IMessage onMessage(TestMessage message, MessageContext ctx)
        {
            System.out.println("received non-async message on thread: " + Thread.currentThread().getName());
            new Exception("debug").printStackTrace();
            return new Response();
        }
        
    }
    
    public static class Response implements IMessage {

        @Override
        public void fromBytes(ByteBuf buf)
        {
            
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            
        }
        
    }
    
    static class ResponseHandler implements IMessageHandler<Response, IMessage> {
        
        @Override
        public IMessage onMessage(Response message, MessageContext ctx)
        {
            System.out.println("received response from sync");
            return null;
        }
        
    }

}
