package net.minecraftforge.common.util;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.stats.StatBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

//Preliminary, simple Fake Player class
public class FakePlayer extends EntityPlayerMP
{
    public FakePlayer(WorldServer world, GameProfile name)
    {
        super(FMLCommonHandler.instance().getMinecraftServerInstance(), world, name, new ItemInWorldManager(world));
    }

    @Override public Vec3 getPositionVector(){ return new Vec3(0, 0, 0); }
    @Override public boolean canCommandSenderUseCommand(int i, String s){ return false; }
    @Override public void addChatComponentMessage(IChatComponent chatmessagecomponent){}
    @Override public void addStat(StatBase par1StatBase, int par2){}
    @Override public void openGui(Object mod, int modGuiId, World world, int x, int y, int z){}
    @Override public boolean isEntityInvulnerable(DamageSource source){ return true; }
    @Override public boolean canAttackPlayer(EntityPlayer player){ return false; }
    @Override public void onDeath(DamageSource source){ return; }
    @Override public void onUpdate(){ return; }
    @Override public void travelToDimension(int dim){ return; }
    @Override public void handleClientSettings(C15PacketClientSettings pkt){ return; }
}
