package net.minecraftforge.common.util;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

//Preliminary, simple Fake Player class 
public class FakePlayer extends EntityPlayerMP
{
    public FakePlayer(WorldServer world, GameProfile name)
    {
        super(FMLCommonHandler.instance().getMinecraftServerInstance(), world, name, new ItemInWorldManager(world));
    }

    @Override public boolean canCommandSenderUseCommand(int i, String s){ return false; }
    @Override public ChunkCoordinates getPlayerCoordinates()
    {
        return new ChunkCoordinates(0,0,0);
    }

    @Override public void addChatComponentMessage(IChatComponent chatmessagecomponent){}
    @Override public void addStat(StatBase par1StatBase, int par2){}
    @Override public void openGui(Object mod, int modGuiId, World world, int x, int y, int z){}
    @Override public boolean isEntityInvulnerable(){ return true; }
    @Override public boolean canAttackPlayer(EntityPlayer player){ return false; }
    @Override public void onDeath(DamageSource source){ return; }
    @Override public void onUpdate(){ return; }
    @Override public void travelToDimension(int dim){ return; }
    @Override public void func_147100_a(C15PacketClientSettings pkt){ return; }
}
