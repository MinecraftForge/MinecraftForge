package net.minecraft.client.resources;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class SimpleReloadableResourceManager implements IReloadableResourceManager
{
    private static final Logger field_147967_a = LogManager.getLogger();
    private static final Joiner joinerResourcePacks = Joiner.on(", ");
    private final Map domainResourceManagers = Maps.newHashMap();
    private final List reloadListeners = Lists.newArrayList();
    private final Set setResourceDomains = Sets.newLinkedHashSet();
    private final IMetadataSerializer rmMetadataSerializer;
    private static final String __OBFID = "CL_00001091";

    public SimpleReloadableResourceManager(IMetadataSerializer par1MetadataSerializer)
    {
        this.rmMetadataSerializer = par1MetadataSerializer;
    }

    public void reloadResourcePack(IResourcePack par1ResourcePack)
    {
        FallbackResourceManager fallbackresourcemanager;

        for (Iterator iterator = par1ResourcePack.getResourceDomains().iterator(); iterator.hasNext(); fallbackresourcemanager.addResourcePack(par1ResourcePack))
        {
            String s = (String)iterator.next();
            this.setResourceDomains.add(s);
            fallbackresourcemanager = (FallbackResourceManager)this.domainResourceManagers.get(s);

            if (fallbackresourcemanager == null)
            {
                fallbackresourcemanager = new FallbackResourceManager(this.rmMetadataSerializer);
                this.domainResourceManagers.put(s, fallbackresourcemanager);
            }
        }
    }

    public Set getResourceDomains()
    {
        return this.setResourceDomains;
    }

    public IResource getResource(ResourceLocation par1ResourceLocation) throws IOException
    {
        IResourceManager iresourcemanager = (IResourceManager)this.domainResourceManagers.get(par1ResourceLocation.getResourceDomain());

        if (iresourcemanager != null)
        {
            return iresourcemanager.getResource(par1ResourceLocation);
        }
        else
        {
            throw new FileNotFoundException(par1ResourceLocation.toString());
        }
    }

    public List getAllResources(ResourceLocation par1ResourceLocation) throws IOException
    {
        IResourceManager iresourcemanager = (IResourceManager)this.domainResourceManagers.get(par1ResourceLocation.getResourceDomain());

        if (iresourcemanager != null)
        {
            return iresourcemanager.getAllResources(par1ResourceLocation);
        }
        else
        {
            throw new FileNotFoundException(par1ResourceLocation.toString());
        }
    }

    private void clearResources()
    {
        this.domainResourceManagers.clear();
        this.setResourceDomains.clear();
    }

    public void reloadResources(List par1List)
    {
        this.clearResources();
        field_147967_a.info("Reloading ResourceManager: " + joinerResourcePacks.join(Iterables.transform(par1List, new Function()
        {
            private static final String __OBFID = "CL_00001092";
            public String apply(IResourcePack par1ResourcePack)
            {
                return par1ResourcePack.getPackName();
            }
            public Object apply(Object par1Obj)
            {
                return this.apply((IResourcePack)par1Obj);
            }
        })));
        Iterator iterator = par1List.iterator();

        while (iterator.hasNext())
        {
            IResourcePack iresourcepack = (IResourcePack)iterator.next();
            this.reloadResourcePack(iresourcepack);
        }

        this.notifyReloadListeners();
    }

    public void registerReloadListener(IResourceManagerReloadListener par1ResourceManagerReloadListener)
    {
        this.reloadListeners.add(par1ResourceManagerReloadListener);
        par1ResourceManagerReloadListener.onResourceManagerReload(this);
    }

    private void notifyReloadListeners()
    {
        Iterator iterator = this.reloadListeners.iterator();

        while (iterator.hasNext())
        {
            IResourceManagerReloadListener iresourcemanagerreloadlistener = (IResourceManagerReloadListener)iterator.next();
            iresourcemanagerreloadlistener.onResourceManagerReload(this);
        }
    }
}