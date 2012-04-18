/*
 * The FML Forge Mod Loader suite.
 * Copyright (C) 2012 cpw
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package cpw.mods.fml.server;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import net.minecraft.src.BaseMod;
import net.minecraft.src.MLProp;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IConsoleHandler;
import cpw.mods.fml.common.ICraftingHandler;
import cpw.mods.fml.common.IDispenseHandler;
import cpw.mods.fml.common.INetworkHandler;
import cpw.mods.fml.common.IPickupNotifier;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderException;
import cpw.mods.fml.common.ModContainer;

public class ModLoaderModContainer implements ModContainer
{
    private Class <? extends BaseMod > modClazz;
    private BaseMod mod;
    private boolean isTicking;
    private File modSource ;
    private ArrayList<String> dependencies;
    private ArrayList<String> preDependencies;
    private ArrayList<String> postDependencies;
    public ModLoaderModContainer(Class <? extends BaseMod > modClazz, File modSource)
    {
        this.modClazz = modClazz;
        this.modSource = modSource;
    }

    @Override
    public boolean wantsPreInit()
    {
        return true;
    }

    @Override
    public boolean wantsPostInit()
    {
        return true;
    }

    @Override
    public void preInit()
    {
        try
        {
            configureMod();
            mod = modClazz.newInstance();
        }
        catch (Exception e)
        {
            throw new LoaderException(e);
        }
    }

    /**
     *
     */
    private void configureMod()
    {
        File configDir = Loader.instance().getConfigDir();
        File modConfig = new File(configDir, String.format("%s.cfg", modClazz.getSimpleName()));
        Properties props = new Properties();

        if (modConfig.exists())
        {
            try
            {
                FileReader configReader = new FileReader(modConfig);
                props.load(configReader);
                configReader.close();
            }
            catch (Exception e)
            {
                Loader.log.severe(String.format("Error occured reading mod configuration file %s", modConfig.getName()));
                Loader.log.throwing("ModLoaderModContainer", "configureMod", e);
                throw new LoaderException(e);
            }
        }

        StringBuffer comments = new StringBuffer();
        comments.append("MLProperties: name (type:default) min:max -- information\n");

        try
        {
            for (Field f : modClazz.getDeclaredFields())
            {
                if (!Modifier.isStatic(f.getModifiers()))
                {
                    continue;
                }

                if (!f.isAnnotationPresent(MLProp.class))
                {
                    continue;
                }

                MLProp property = f.getAnnotation(MLProp.class);
                String propertyName = property.name().length() > 0 ? property.name() : f.getName();
                String propertyValue = null;
                Object defaultValue = null;

                try
                {
                    defaultValue = f.get(null);
                    propertyValue = props.getProperty(propertyName, extractValue(defaultValue));
                    Object currentValue = parseValue(propertyValue, property, f.getType());

                    if (currentValue != null && !currentValue.equals(defaultValue))
                    {
                        f.set(null, currentValue);
                    }
                }
                catch (Exception e)
                {
                    Loader.log.severe(String.format("Invalid configuration found for %s in %s", propertyName, modConfig.getName()));
                    Loader.log.throwing("ModLoaderModContainer", "configureMod", e);
                    throw new LoaderException(e);
                }
                finally
                {
                    comments.append(String.format("MLProp : %s (%s:%s", propertyName, f.getType().getName(), defaultValue));

                    if (property.min() != Double.MIN_VALUE)
                    {
                        comments.append(",>=").append(String.format("%.1f", property.min()));
                    }

                    if (property.max() != Double.MAX_VALUE)
                    {
                        comments.append(",<=").append(String.format("%.1f", property.max()));
                    }

                    comments.append(")");

                    if (property.info().length() > 0)
                    {
                        comments.append(" -- ").append(property.info());
                    }

                    if (propertyValue != null)
                    {
                        props.setProperty(propertyName, extractValue(propertyValue));
                    }
                }
            }
        }
        finally
        {
            try
            {
                FileWriter configWriter = new FileWriter(modConfig);
                props.store(configWriter, comments.toString());
                configWriter.close();
            }
            catch (IOException e)
            {
                Loader.log.warning(String.format("Error trying to write the config file %s", modConfig.getName()));
                Loader.log.throwing("ModLoaderModContainer", "configureMod", e);
                throw new LoaderException(e);
            }
        }
    }

    private Object parseValue(String val, MLProp property, Class<?> type)
    {
        if (type.isAssignableFrom(String.class))
        {
            return (String)val;
        }
        else if (type.isAssignableFrom(Boolean.TYPE) || type.isAssignableFrom(Boolean.class))
        {
            return Boolean.parseBoolean(val);
        }
        else if (Number.class.isAssignableFrom(type))
        {
            Number n = null;

            if (Double.class.isAssignableFrom(type))
            {
                n = Double.parseDouble(val);
            }
            if (Float.class.isAssignableFrom(type))
            {
                n = Float.parseFloat(val);
            }
            if (Long.class.isAssignableFrom(type))
            {
                n = Long.parseLong(val);
            }
            if (Integer.class.isAssignableFrom(type))
            {
                n = Integer.parseInt(val);
            }
            if (Short.class.isAssignableFrom(type))
            {
                n = Short.parseShort(val);
            }
            if (Byte.class.isAssignableFrom(type))
            {
                n = Byte.parseByte(val);
            }
            else
            {
                throw new IllegalArgumentException("MLProp declared on non-standard type");
            }

            if (n.doubleValue() < property.min() || n.doubleValue() > property.max())
            {
                return null;
            }
            else
            {
                return n;
            }
        }

        return null;
    }
    private String extractValue(Object value)
    {
        if (String.class.isInstance(value))
        {
            return (String)value;
        }
        else if (Number.class.isInstance(value) || Boolean.class.isInstance(value))
        {
            return String.valueOf(value);
        }
        else
        {
            throw new IllegalArgumentException("MLProp declared on non-standard type");
        }
    }
    @Override
    public void init()
    {
        mod.load();
    }

    @Override
    public void postInit()
    {
        mod.modsLoaded();
    }

    @Override
    public void tickStart()
    {
        if (isTicking)
        {
            isTicking = mod.onTickInGame(FMLServerHandler.instance().getServer());
        }
    }
    @Override
    public void tickEnd()
    {
        // NOOP for modloader
    }

    @Override
    public String getName()
    {
        return mod != null ? mod.getName() : null;
    }

    public static ModContainer findContainerFor(BaseMod mod)
    {
        for (ModContainer mc : Loader.getModList())
        {
            if (mc.matches(mod))
            {
                return mc;
            }
        }

        return null;
    }

    @Override
    public boolean matches(Object mod)
    {
        return modClazz.isInstance(mod);
    }

    public void setTicking(boolean enable)
    {
        isTicking = enable;
    }

    /**
     * Find all the BaseMods in the system 
     * @return
     */
    public static List<BaseMod> findAll()
    {
        ArrayList<BaseMod> modList = new ArrayList<BaseMod>();

        for (ModContainer mc : Loader.getModList())
        {
            if (mc instanceof ModLoaderModContainer)
            {
                modList.add(((ModLoaderModContainer)mc).mod);
            }
        }

        return modList;
    }

    @Override
    public File getSource()
    {
        return modSource;
    }

    @Override
    public Object getMod()
    {
        return mod;
    }

    @Override
    public boolean generatesWorld()
    {
        return true;
    }

    @Override
    public IWorldGenerator getWorldGenerator()
    {
        return mod;
    }

    @Override
    public int lookupFuelValue(int itemId, int itemDamage)
    {
        return mod.addFuel(itemId, itemDamage);
    }

    @Override
    public boolean wantsPickupNotification()
    {
        return true;
    }

    @Override
    public IPickupNotifier getPickupNotifier()
    {
        return mod;
    }

    @Override
    public boolean wantsToDispense()
    {
        return true;
    }

    @Override
    public IDispenseHandler getDispenseHandler()
    {
        return mod;
    }

    @Override
    public boolean wantsCraftingNotification()
    {
        return true;
    }

    @Override
    public ICraftingHandler getCraftingHandler()
    {
        return mod;
    }

    private void computeDependencies()
    {
        dependencies = new ArrayList<String>();
        preDependencies = new ArrayList<String>();
        postDependencies = new ArrayList<String>();

        if (mod.getPriorities() == null || mod.getPriorities().length() == 0)
        {
            return;
        }

        StringTokenizer st = new StringTokenizer(mod.getPriorities(), ";");

        for (; st.hasMoreTokens();)
        {
            String dep = st.nextToken();
            String[] depparts = dep.split(":");

            if (depparts.length < 2)
            {
                throw new LoaderException();
            }

            if ("required-before".equals(depparts[0]) || "required-after".equals(depparts[0]))
            {
                dependencies.add(depparts[1]);
            }

            if ("required-before".equals(depparts[0]) || "before".equals(depparts[0]))
            {
            	postDependencies.add(depparts[1]);
            }

            if ("required-after".equals(depparts[0]) || "after".equals(depparts[0]))
            {
                preDependencies.add(depparts[1]);
            }

        }
    }

    @Override
    public List<String> getDependencies()
    {
        if (dependencies == null)
        {
            computeDependencies();
        }

        return dependencies;
    }

    @Override
    public List<String> getPostDepends()
    {
        if (dependencies == null)
        {
            computeDependencies();
        }

        return postDependencies;
    }

    @Override
    public List<String> getPreDepends()
    {
        if (dependencies == null)
        {
            computeDependencies();
        }
        return preDependencies;
    }


    public String toString()
    {
        return modSource.getName();
    }

    @Override
    public boolean wantsNetworkPackets()
    {
        return true;
    }

    @Override
    public INetworkHandler getNetworkHandler()
    {
        return mod;
    }

    @Override
    public boolean ownsNetworkChannel(String channel)
    {
        return FMLCommonHandler.instance().getChannelListFor(this).contains(channel);
    }

    @Override
    public boolean wantsConsoleCommands()
    {
        return true;
    }

    @Override
    public IConsoleHandler getConsoleHandler()
    {
        return mod;
    }

    @Override
    public boolean wantsPlayerTracking()
    {
        return true;
    }

    @Override
    public IPlayerTracker getPlayerTracker()
    {
        return mod;
    }
}
