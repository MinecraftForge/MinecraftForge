package net.minecraft.command;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

public class PlayerSelector
{
    // JAVADOC FIELD $$ field_82389_a
    private static final Pattern tokenPattern = Pattern.compile("^@([parf])(?:\\[([\\w=,!-]*)\\])?$");
    // JAVADOC FIELD $$ field_82387_b
    private static final Pattern intListPattern = Pattern.compile("\\G([-!]?[\\w-]*)(?:$|,)");
    // JAVADOC FIELD $$ field_82388_c
    private static final Pattern keyValueListPattern = Pattern.compile("\\G(\\w+)=([-!]?[\\w-]*)(?:$|,)");
    private static final String __OBFID = "CL_00000086";

    // JAVADOC METHOD $$ func_82386_a
    public static EntityPlayerMP matchOnePlayer(ICommandSender par0ICommandSender, String par1Str)
    {
        EntityPlayerMP[] aentityplayermp = matchPlayers(par0ICommandSender, par1Str);
        return aentityplayermp != null && aentityplayermp.length == 1 ? aentityplayermp[0] : null;
    }

    public static IChatComponent func_150869_b(ICommandSender p_150869_0_, String p_150869_1_)
    {
        EntityPlayerMP[] aentityplayermp = matchPlayers(p_150869_0_, p_150869_1_);

        if (aentityplayermp != null && aentityplayermp.length != 0)
        {
            IChatComponent[] aichatcomponent = new IChatComponent[aentityplayermp.length];

            for (int i = 0; i < aichatcomponent.length; ++i)
            {
                aichatcomponent[i] = aentityplayermp[i].func_145748_c_();
            }

            return CommandBase.func_147177_a(aichatcomponent);
        }
        else
        {
            return null;
        }
    }

    // JAVADOC METHOD $$ func_82380_c
    public static EntityPlayerMP[] matchPlayers(ICommandSender par0ICommandSender, String par1Str)
    {
        Matcher matcher = tokenPattern.matcher(par1Str);

        if (!matcher.matches())
        {
            return null;
        }
        else
        {
            Map map = getArgumentMap(matcher.group(2));
            String s1 = matcher.group(1);
            int i = getDefaultMinimumRange(s1);
            int j = getDefaultMaximumRange(s1);
            int k = getDefaultMinimumLevel(s1);
            int l = getDefaultMaximumLevel(s1);
            int i1 = getDefaultCount(s1);
            int j1 = WorldSettings.GameType.NOT_SET.getID();
            ChunkCoordinates chunkcoordinates = par0ICommandSender.getPlayerCoordinates();
            Map map1 = func_96560_a(map);
            String s2 = null;
            String s3 = null;
            boolean flag = false;

            if (map.containsKey("rm"))
            {
                i = MathHelper.parseIntWithDefault((String)map.get("rm"), i);
                flag = true;
            }

            if (map.containsKey("r"))
            {
                j = MathHelper.parseIntWithDefault((String)map.get("r"), j);
                flag = true;
            }

            if (map.containsKey("lm"))
            {
                k = MathHelper.parseIntWithDefault((String)map.get("lm"), k);
            }

            if (map.containsKey("l"))
            {
                l = MathHelper.parseIntWithDefault((String)map.get("l"), l);
            }

            if (map.containsKey("x"))
            {
                chunkcoordinates.posX = MathHelper.parseIntWithDefault((String)map.get("x"), chunkcoordinates.posX);
                flag = true;
            }

            if (map.containsKey("y"))
            {
                chunkcoordinates.posY = MathHelper.parseIntWithDefault((String)map.get("y"), chunkcoordinates.posY);
                flag = true;
            }

            if (map.containsKey("z"))
            {
                chunkcoordinates.posZ = MathHelper.parseIntWithDefault((String)map.get("z"), chunkcoordinates.posZ);
                flag = true;
            }

            if (map.containsKey("m"))
            {
                j1 = MathHelper.parseIntWithDefault((String)map.get("m"), j1);
            }

            if (map.containsKey("c"))
            {
                i1 = MathHelper.parseIntWithDefault((String)map.get("c"), i1);
            }

            if (map.containsKey("team"))
            {
                s3 = (String)map.get("team");
            }

            if (map.containsKey("name"))
            {
                s2 = (String)map.get("name");
            }

            World world = flag ? par0ICommandSender.getEntityWorld() : null;
            List list;

            if (!s1.equals("p") && !s1.equals("a"))
            {
                if (!s1.equals("r"))
                {
                    return null;
                }
                else
                {
                    list = MinecraftServer.getServer().getConfigurationManager().findPlayers(chunkcoordinates, i, j, 0, j1, k, l, map1, s2, s3, world);
                    Collections.shuffle(list);
                    list = list.subList(0, Math.min(i1, list.size()));
                    return list != null && !list.isEmpty() ? (EntityPlayerMP[])list.toArray(new EntityPlayerMP[0]) : new EntityPlayerMP[0];
                }
            }
            else
            {
                list = MinecraftServer.getServer().getConfigurationManager().findPlayers(chunkcoordinates, i, j, i1, j1, k, l, map1, s2, s3, world);
                return list != null && !list.isEmpty() ? (EntityPlayerMP[])list.toArray(new EntityPlayerMP[0]) : new EntityPlayerMP[0];
            }
        }
    }

    public static Map func_96560_a(Map par0Map)
    {
        HashMap hashmap = new HashMap();
        Iterator iterator = par0Map.keySet().iterator();

        while (iterator.hasNext())
        {
            String s = (String)iterator.next();

            if (s.startsWith("score_") && s.length() > "score_".length())
            {
                String s1 = s.substring("score_".length());
                hashmap.put(s1, Integer.valueOf(MathHelper.parseIntWithDefault((String)par0Map.get(s), 1)));
            }
        }

        return hashmap;
    }

    // JAVADOC METHOD $$ func_82377_a
    public static boolean matchesMultiplePlayers(String par0Str)
    {
        Matcher matcher = tokenPattern.matcher(par0Str);

        if (matcher.matches())
        {
            Map map = getArgumentMap(matcher.group(2));
            String s1 = matcher.group(1);
            int i = getDefaultCount(s1);

            if (map.containsKey("c"))
            {
                i = MathHelper.parseIntWithDefault((String)map.get("c"), i);
            }

            return i != 1;
        }
        else
        {
            return false;
        }
    }

    // JAVADOC METHOD $$ func_82383_a
    public static boolean hasTheseArguments(String par0Str, String par1Str)
    {
        Matcher matcher = tokenPattern.matcher(par0Str);

        if (matcher.matches())
        {
            String s2 = matcher.group(1);
            return par1Str == null || par1Str.equals(s2);
        }
        else
        {
            return false;
        }
    }

    // JAVADOC METHOD $$ func_82378_b
    public static boolean hasArguments(String par0Str)
    {
        // JAVADOC METHOD $$ func_82383_a
        return hasTheseArguments(par0Str, (String)null);
    }

    // JAVADOC METHOD $$ func_82384_c
    private static final int getDefaultMinimumRange(String par0Str)
    {
        return 0;
    }

    // JAVADOC METHOD $$ func_82379_d
    private static final int getDefaultMaximumRange(String par0Str)
    {
        return 0;
    }

    // JAVADOC METHOD $$ func_82376_e
    private static final int getDefaultMaximumLevel(String par0Str)
    {
        return Integer.MAX_VALUE;
    }

    // JAVADOC METHOD $$ func_82375_f
    private static final int getDefaultMinimumLevel(String par0Str)
    {
        return 0;
    }

    // JAVADOC METHOD $$ func_82382_g
    private static final int getDefaultCount(String par0Str)
    {
        return par0Str.equals("a") ? 0 : 1;
    }

    // JAVADOC METHOD $$ func_82381_h
    private static Map getArgumentMap(String par0Str)
    {
        HashMap hashmap = new HashMap();

        if (par0Str == null)
        {
            return hashmap;
        }
        else
        {
            Matcher matcher = intListPattern.matcher(par0Str);
            int i = 0;
            int j;

            for (j = -1; matcher.find(); j = matcher.end())
            {
                String s1 = null;

                switch (i++)
                {
                    case 0:
                        s1 = "x";
                        break;
                    case 1:
                        s1 = "y";
                        break;
                    case 2:
                        s1 = "z";
                        break;
                    case 3:
                        s1 = "r";
                }

                if (s1 != null && matcher.group(1).length() > 0)
                {
                    hashmap.put(s1, matcher.group(1));
                }
            }

            if (j < par0Str.length())
            {
                matcher = keyValueListPattern.matcher(j == -1 ? par0Str : par0Str.substring(j));

                while (matcher.find())
                {
                    hashmap.put(matcher.group(1), matcher.group(2));
                }
            }

            return hashmap;
        }
    }
}