package net.minecraft.src;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.TickRegistry;
import net.minecraft.client.Minecraft;

@NetworkMod(channels={"mod_TestMod"},clientSideRequired=true,packetHandler=mod_testMod.PacketHandler.class)
public class mod_testMod extends BaseMod {
    public static class PacketHandler implements IPacketHandler
    {
        @Override
        public void onPacketData(NetworkManager manager, Packet250CustomPayload packet, Player player)
        {
        }
    }

    private long ts;
    @Override
    public String getVersion() {
        return "test";
    }

    @Override
    public void load() {
//        if (1==1) throw new RuntimeException();
//        ModLoader.setInGameHook(this, true, false);
//        ModLoader.setInGUIHook(this, true, false);
//        FMLCommonHandler.instance().registerTickHandler(this);
        ModLoader.registerKey(this, new KeyBinding("Fishhead",Keyboard.KEY_O), true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void keyboardEvent(KeyBinding event)
    {
        Mouse.setGrabbed(false);
        System.out.println(event.field_74515_c);
    }

    @Override
    public boolean onTickInGame(float time, Minecraft minecraftInstance)
    {
        long now = System.currentTimeMillis();
        long del=now-ts;
        ts=now;
        System.out.printf("%d %d %d MLTICK\n",del, ts, now);
        return true;
    }

    @Override
    public boolean onTickInGUI(float tick, Minecraft game, GuiScreen gui)
    {
        System.out.printf("%d MLGUITICK\n",System.currentTimeMillis());
        return true;
    }

    public class TickTester implements IScheduledTickHandler
    {
        public int interval;
        public long tsg = System.currentTimeMillis();
        @Override
        public void tickStart(EnumSet<TickType> type, Object... tickData)
        {
            long now = System.currentTimeMillis();
            long del=now-tsg;
            System.out.printf("Begin GAMETICK [%d] %d\n",interval, del);
        }

        @Override
        public void tickEnd(EnumSet<TickType> type, Object... tickData)
        {
            long now = System.currentTimeMillis();
            long del=now-tsg;
            tsg=now;
            System.out.printf("End GAMETICK [%d] %d\n",interval, del);
        }

        @Override
        public EnumSet<TickType> ticks()
        {
            return EnumSet.of(TickType.CLIENT);
        }

        /* (non-Javadoc)
         * @see cpw.mods.fml.common.ITickHandler#getLabel()
         */
        @Override
        public String getLabel()
        {
            return "TickTester";
        }

        @Override
        public int nextTickSpacing()
        {
            return interval;
        }
    }

    @Override
    public String getPriorities()
    {
        return "after:MockMod";
    }
}