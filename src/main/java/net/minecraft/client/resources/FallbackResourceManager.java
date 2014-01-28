package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class FallbackResourceManager implements IResourceManager
{
    protected final List resourcePacks = new ArrayList();
    private final IMetadataSerializer frmMetadataSerializer;
    private static final String __OBFID = "CL_00001074";

    public FallbackResourceManager(IMetadataSerializer par1MetadataSerializer)
    {
        this.frmMetadataSerializer = par1MetadataSerializer;
    }

    public void addResourcePack(IResourcePack par1ResourcePack)
    {
        this.resourcePacks.add(par1ResourcePack);
    }

    public Set getResourceDomains()
    {
        return null;
    }

    public IResource getResource(ResourceLocation par1ResourceLocation) throws IOException
    {
        IResourcePack iresourcepack = null;
        ResourceLocation resourcelocation1 = getLocationMcmeta(par1ResourceLocation);

        for (int i = this.resourcePacks.size() - 1; i >= 0; --i)
        {
            IResourcePack iresourcepack1 = (IResourcePack)this.resourcePacks.get(i);

            if (iresourcepack == null && iresourcepack1.resourceExists(resourcelocation1))
            {
                iresourcepack = iresourcepack1;
            }

            if (iresourcepack1.resourceExists(par1ResourceLocation))
            {
                InputStream inputstream = null;

                if (iresourcepack != null)
                {
                    inputstream = iresourcepack.getInputStream(resourcelocation1);
                }

                return new SimpleResource(par1ResourceLocation, iresourcepack1.getInputStream(par1ResourceLocation), inputstream, this.frmMetadataSerializer);
            }
        }

        throw new FileNotFoundException(par1ResourceLocation.toString());
    }

    public List getAllResources(ResourceLocation par1ResourceLocation) throws IOException
    {
        ArrayList arraylist = Lists.newArrayList();
        ResourceLocation resourcelocation1 = getLocationMcmeta(par1ResourceLocation);
        Iterator iterator = this.resourcePacks.iterator();

        while (iterator.hasNext())
        {
            IResourcePack iresourcepack = (IResourcePack)iterator.next();

            if (iresourcepack.resourceExists(par1ResourceLocation))
            {
                InputStream inputstream = iresourcepack.resourceExists(resourcelocation1) ? iresourcepack.getInputStream(resourcelocation1) : null;
                arraylist.add(new SimpleResource(par1ResourceLocation, iresourcepack.getInputStream(par1ResourceLocation), inputstream, this.frmMetadataSerializer));
            }
        }

        if (arraylist.isEmpty())
        {
            throw new FileNotFoundException(par1ResourceLocation.toString());
        }
        else
        {
            return arraylist;
        }
    }

    static ResourceLocation getLocationMcmeta(ResourceLocation par0ResourceLocation)
    {
        return new ResourceLocation(par0ResourceLocation.getResourceDomain(), par0ResourceLocation.getResourcePath() + ".mcmeta");
    }
}