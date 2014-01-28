package net.minecraft.world;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import net.minecraft.nbt.NBTTagCompound;

public class GameRules
{
    private TreeMap theGameRules = new TreeMap();
    private static final String __OBFID = "CL_00000136";

    public GameRules()
    {
        this.addGameRule("doFireTick", "true");
        this.addGameRule("mobGriefing", "true");
        this.addGameRule("keepInventory", "false");
        this.addGameRule("doMobSpawning", "true");
        this.addGameRule("doMobLoot", "true");
        this.addGameRule("doTileDrops", "true");
        this.addGameRule("commandBlockOutput", "true");
        this.addGameRule("naturalRegeneration", "true");
        this.addGameRule("doDaylightCycle", "true");
    }

    // JAVADOC METHOD $$ func_82769_a
    public void addGameRule(String par1Str, String par2Str)
    {
        this.theGameRules.put(par1Str, new GameRules.Value(par2Str));
    }

    public void setOrCreateGameRule(String par1Str, String par2Str)
    {
        GameRules.Value value = (GameRules.Value)this.theGameRules.get(par1Str);

        if (value != null)
        {
            value.setValue(par2Str);
        }
        else
        {
            this.addGameRule(par1Str, par2Str);
        }
    }

    // JAVADOC METHOD $$ func_82767_a
    public String getGameRuleStringValue(String par1Str)
    {
        GameRules.Value value = (GameRules.Value)this.theGameRules.get(par1Str);
        return value != null ? value.getGameRuleStringValue() : "";
    }

    // JAVADOC METHOD $$ func_82766_b
    public boolean getGameRuleBooleanValue(String par1Str)
    {
        GameRules.Value value = (GameRules.Value)this.theGameRules.get(par1Str);
        return value != null ? value.getGameRuleBooleanValue() : false;
    }

    // JAVADOC METHOD $$ func_82770_a
    public NBTTagCompound writeGameRulesToNBT()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        Iterator iterator = this.theGameRules.keySet().iterator();

        while (iterator.hasNext())
        {
            String s = (String)iterator.next();
            GameRules.Value value = (GameRules.Value)this.theGameRules.get(s);
            nbttagcompound.setString(s, value.getGameRuleStringValue());
        }

        return nbttagcompound;
    }

    // JAVADOC METHOD $$ func_82768_a
    public void readGameRulesFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        Set set = par1NBTTagCompound.func_150296_c();
        Iterator iterator = set.iterator();

        while (iterator.hasNext())
        {
            String s = (String)iterator.next();
            String s1 = par1NBTTagCompound.getString(s);
            this.setOrCreateGameRule(s, s1);
        }
    }

    // JAVADOC METHOD $$ func_82763_b
    public String[] getRules()
    {
        return (String[])this.theGameRules.keySet().toArray(new String[0]);
    }

    // JAVADOC METHOD $$ func_82765_e
    public boolean hasRule(String par1Str)
    {
        return this.theGameRules.containsKey(par1Str);
    }

    static class Value
        {
            private String valueString;
            private boolean valueBoolean;
            private int valueInteger;
            private double valueDouble;
            private static final String __OBFID = "CL_00000137";

            public Value(String par1Str)
            {
                this.setValue(par1Str);
            }

            // JAVADOC METHOD $$ func_82757_a
            public void setValue(String par1Str)
            {
                this.valueString = par1Str;
                this.valueBoolean = Boolean.parseBoolean(par1Str);

                try
                {
                    this.valueInteger = Integer.parseInt(par1Str);
                }
                catch (NumberFormatException numberformatexception1)
                {
                    ;
                }

                try
                {
                    this.valueDouble = Double.parseDouble(par1Str);
                }
                catch (NumberFormatException numberformatexception)
                {
                    ;
                }
            }

            // JAVADOC METHOD $$ func_82756_a
            public String getGameRuleStringValue()
            {
                return this.valueString;
            }

            // JAVADOC METHOD $$ func_82758_b
            public boolean getGameRuleBooleanValue()
            {
                return this.valueBoolean;
            }
        }
}