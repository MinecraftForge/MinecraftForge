package net.minecraftforge.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

//Preliminary, simple Fake Player class 
public class FakePlayer extends EntityPlayer
{
    public FakePlayer(World world, String name)
    {
        super(world, name);
    }

    public void sendChatToPlayer(String s){}
    public boolean canCommandSenderUseCommand(int i, String s){ return false; }
    public ChunkCoordinates getPlayerCoordinates()
    {
        return new ChunkCoordinates(0,0,0);
    }

    @Override
    public void sendChatToPlayer(ChatMessageComponent chatmessagecomponent){}
}
