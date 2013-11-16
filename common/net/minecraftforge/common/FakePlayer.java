package net.minecraftforge.common;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.network.packet.Packet204ClientInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
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
    @Override 
    public boolean isEntityInvulnerable(){ return true; }
    @Override
    public boolean canAttackPlayer(EntityPlayer player){ return false; }
    @Override
    public void onDeath(DamageSource source){ return; }
    @Override
    public void onUpdate(){ return; }
    @Override
    public void travelToDimension(int dim){ return; }
    @Override
    public void updateClientInfo(Packet204ClientInfo pkt){ return; }
}
