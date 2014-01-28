package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class TextureManager implements ITickable, IResourceManagerReloadListener
{
    private static final Logger field_147646_a = LogManager.getLogger();
    private final Map mapTextureObjects = Maps.newHashMap();
    private final Map mapResourceLocations = Maps.newHashMap();
    private final List listTickables = Lists.newArrayList();
    private final Map mapTextureCounters = Maps.newHashMap();
    private IResourceManager theResourceManager;
    private static final String __OBFID = "CL_00001064";

    public TextureManager(IResourceManager par1ResourceManager)
    {
        this.theResourceManager = par1ResourceManager;
    }

    public void bindTexture(ResourceLocation par1ResourceLocation)
    {
        Object object = (ITextureObject)this.mapTextureObjects.get(par1ResourceLocation);

        if (object == null)
        {
            object = new SimpleTexture(par1ResourceLocation);
            this.loadTexture(par1ResourceLocation, (ITextureObject)object);
        }

        TextureUtil.bindTexture(((ITextureObject)object).getGlTextureId());
    }

    public ResourceLocation getResourceLocation(int par1)
    {
        return (ResourceLocation)this.mapResourceLocations.get(Integer.valueOf(par1));
    }

    public boolean loadTextureMap(ResourceLocation par1ResourceLocation, TextureMap par2TextureMap)
    {
        if (this.loadTickableTexture(par1ResourceLocation, par2TextureMap))
        {
            this.mapResourceLocations.put(Integer.valueOf(par2TextureMap.getTextureType()), par1ResourceLocation);
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean loadTickableTexture(ResourceLocation par1ResourceLocation, ITickableTextureObject par2TickableTextureObject)
    {
        if (this.loadTexture(par1ResourceLocation, par2TickableTextureObject))
        {
            this.listTickables.add(par2TickableTextureObject);
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean loadTexture(ResourceLocation par1ResourceLocation, final ITextureObject par2TextureObject)
    {
        boolean flag = true;
        ITextureObject par2TextureObject2 = par2TextureObject;

        try
        {
            ((ITextureObject)par2TextureObject).loadTexture(this.theResourceManager);
        }
        catch (IOException ioexception)
        {
            field_147646_a.warn("Failed to load texture: " + par1ResourceLocation, ioexception);
            par2TextureObject2 = TextureUtil.missingTexture;
            this.mapTextureObjects.put(par1ResourceLocation, par2TextureObject2);
            flag = false;
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Registering texture");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Resource location being registered");
            crashreportcategory.addCrashSection("Resource location", par1ResourceLocation);
            crashreportcategory.addCrashSectionCallable("Texture object class", new Callable()
            {
                private static final String __OBFID = "CL_00001065";
                public String call()
                {
                    return par2TextureObject.getClass().getName();
                }
            });
            throw new ReportedException(crashreport);
        }

        this.mapTextureObjects.put(par1ResourceLocation, par2TextureObject2);
        return flag;
    }

    public ITextureObject getTexture(ResourceLocation par1ResourceLocation)
    {
        return (ITextureObject)this.mapTextureObjects.get(par1ResourceLocation);
    }

    public ResourceLocation getDynamicTextureLocation(String par1Str, DynamicTexture par2DynamicTexture)
    {
        Integer integer = (Integer)this.mapTextureCounters.get(par1Str);

        if (integer == null)
        {
            integer = Integer.valueOf(1);
        }
        else
        {
            integer = Integer.valueOf(integer.intValue() + 1);
        }

        this.mapTextureCounters.put(par1Str, integer);
        ResourceLocation resourcelocation = new ResourceLocation(String.format("dynamic/%s_%d", new Object[] {par1Str, integer}));
        this.loadTexture(resourcelocation, par2DynamicTexture);
        return resourcelocation;
    }

    public void tick()
    {
        Iterator iterator = this.listTickables.iterator();

        while (iterator.hasNext())
        {
            ITickable itickable = (ITickable)iterator.next();
            itickable.tick();
        }
    }

    public void func_147645_c(ResourceLocation p_147645_1_)
    {
        ITextureObject itextureobject = this.getTexture(p_147645_1_);

        if (itextureobject != null)
        {
            TextureUtil.func_147942_a(itextureobject.getGlTextureId());
        }
    }

    public void onResourceManagerReload(IResourceManager par1ResourceManager)
    {
        Iterator iterator = this.mapTextureObjects.entrySet().iterator();

        while (iterator.hasNext())
        {
            Entry entry = (Entry)iterator.next();
            this.loadTexture((ResourceLocation)entry.getKey(), (ITextureObject)entry.getValue());
        }
    }
}