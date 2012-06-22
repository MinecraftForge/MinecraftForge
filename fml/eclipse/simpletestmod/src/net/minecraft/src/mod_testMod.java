package net.minecraft.src;

import java.util.EnumSet;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import net.minecraft.client.Minecraft;

public class mod_testMod extends BaseMod implements ITickHandler {
    private long ts;
    private long tsg;
    @Override
    public String getVersion() {
        return "test";
    }

    @Override
    public void load() {
        ModLoader.setInGameHook(this, true, true);
        ModLoader.setInGUIHook(this, true, false);
        FMLCommonHandler.instance().registerTickHandler(this);
        ts=System.currentTimeMillis();
        tsg=ts;
    }
    
    @Override
    public boolean onTickInGame(float time, Minecraft minecraftInstance)
    {
        long now = System.currentTimeMillis();
        long del=now-ts;
        ts=now;
        System.out.printf("%d %d %d %d MLTICK\n",del, ts, tsg, now);
        return true;
    }
    
    @Override
    public boolean onTickInGUI(float tick, Minecraft game, GuiScreen gui)
    {
        System.out.printf("%d MLGUITICK\n",System.currentTimeMillis());
        return true;
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
        
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
        long now = System.currentTimeMillis();
        long del=now-tsg;
        tsg=now;
        System.out.printf("%d %d %d %d GAMETICK\n",del, ts, tsg, now);
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.GAME);
    }

    /* (non-Javadoc)
     * @see cpw.mods.fml.common.ITickHandler#getLabel()
     */
    @Override
    public String getLabel()
    {
        // TODO Auto-generated method stub
        return "TickTester";
    }

}