package net.minecraftforge.common;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

//Preliminary, simple Fake Player class 
public class FakePlayer extends EntityPlayerMP
{
    public FakePlayer(World world, String name)
    {
        super(FMLCommonHandler.instance().getMinecraftServerInstance(), world, name, new ItemInWorldManager(world));
    }

    public void sendChatToPlayer(String s){}
    public boolean canCommandSenderUseCommand(int i, String s){ return false; }
    public ChunkCoordinates getPlayerCoordinates()
    {
        return new ChunkCoordinates(0,0,0);
    }

    @Override
    public void sendChatToPlayer(ChatMessageComponent chatmessagecomponent){}
    @Override
    public void addStat(StatBase par1StatBase, int par2){}
    @Override
    public void openGui(Object mod, int modGuiId, World world, int x, int y, int z){}
}
