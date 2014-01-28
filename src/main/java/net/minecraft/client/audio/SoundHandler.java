package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class SoundHandler implements IResourceManagerReloadListener, IUpdatePlayerListBox
{
    private static final Logger field_147698_b = LogManager.getLogger();
    private static final Gson field_147699_c = (new GsonBuilder()).registerTypeAdapter(SoundList.class, new SoundListSerializer()).create();
    private static final ParameterizedType field_147696_d = new ParameterizedType()
    {
        private static final String __OBFID = "CL_00001148";
        public Type[] getActualTypeArguments()
        {
            return new Type[] {String.class, SoundList.class};
        }
        public Type getRawType()
        {
            return Map.class;
        }
        public Type getOwnerType()
        {
            return null;
        }
    };
    public static final SoundPoolEntry field_147700_a = new SoundPoolEntry(new ResourceLocation("meta:missing_sound"), 0.0D, 0.0D, false);
    private final SoundRegistry field_147697_e = new SoundRegistry();
    private final SoundManager field_147694_f;
    private final IResourceManager field_147695_g;
    private static final String __OBFID = "CL_00001147";

    public SoundHandler(IResourceManager p_i45122_1_, GameSettings p_i45122_2_)
    {
        this.field_147695_g = p_i45122_1_;
        this.field_147694_f = new SoundManager(this, p_i45122_2_);
    }

    public void onResourceManagerReload(IResourceManager par1ResourceManager)
    {
        this.field_147694_f.func_148596_a();
        this.field_147697_e.func_148763_c();
        Iterator iterator = par1ResourceManager.getResourceDomains().iterator();

        while (iterator.hasNext())
        {
            String s = (String)iterator.next();

            try
            {
                List list = par1ResourceManager.getAllResources(new ResourceLocation(s, "sounds.json"));

                for (int i = list.size() - 1; i >= 0; --i)
                {
                    IResource iresource = (IResource)list.get(i);

                    try
                    {
                        Map map = (Map)field_147699_c.fromJson(new InputStreamReader(iresource.getInputStream()), field_147696_d);
                        Iterator iterator1 = map.entrySet().iterator();

                        while (iterator1.hasNext())
                        {
                            Entry entry = (Entry)iterator1.next();
                            this.func_147693_a(new ResourceLocation(s, (String)entry.getKey()), (SoundList)entry.getValue());
                        }
                    }
                    catch (RuntimeException runtimeexception)
                    {
                        field_147698_b.warn("Invalid sounds.json", runtimeexception);
                    }
                }
            }
            catch (IOException ioexception)
            {
                ;
            }
        }
    }

    private void func_147693_a(ResourceLocation p_147693_1_, SoundList p_147693_2_)
    {
        SoundEventAccessorComposite soundeventaccessorcomposite;

        if (this.field_147697_e.func_148741_d(p_147693_1_) && !p_147693_2_.func_148574_b())
        {
            soundeventaccessorcomposite = (SoundEventAccessorComposite)this.field_147697_e.getObject(p_147693_1_);
        }
        else
        {
            soundeventaccessorcomposite = new SoundEventAccessorComposite(p_147693_1_, 1.0D, 1.0D, p_147693_2_.func_148573_c());
            this.field_147697_e.func_148762_a(soundeventaccessorcomposite);
        }

        Iterator iterator = p_147693_2_.func_148570_a().iterator();

        while (iterator.hasNext())
        {
            final SoundList.SoundEntry soundentry = (SoundList.SoundEntry)iterator.next();
            String s = soundentry.func_148556_a();
            ResourceLocation resourcelocation1 = new ResourceLocation(s);
            final String s1 = s.contains(":") ? resourcelocation1.getResourceDomain() : p_147693_1_.getResourceDomain();
            Object object;

            switch (SoundHandler.SwitchType.field_148765_a[soundentry.func_148563_e().ordinal()])
            {
                case 1:
                    ResourceLocation resourcelocation2 = new ResourceLocation(s1, "sounds/" + resourcelocation1.getResourcePath() + ".ogg");

                    try
                    {
                        this.field_147695_g.getResource(resourcelocation2);
                    }
                    catch (FileNotFoundException filenotfoundexception)
                    {
                        field_147698_b.warn("File {} does not exist, cannot add it to event {}", new Object[] {resourcelocation2, p_147693_1_});
                        continue;
                    }
                    catch (IOException ioexception)
                    {
                        field_147698_b.warn("Could not load sound file " + resourcelocation2 + ", cannot add it to event " + p_147693_1_, ioexception);
                        continue;
                    }

                    object = new SoundEventAccessor(new SoundPoolEntry(resourcelocation2, (double)soundentry.func_148560_c(), (double)soundentry.func_148558_b(), soundentry.func_148552_f()), soundentry.func_148555_d());
                    break;
                case 2:
                    object = new ISoundEventAccessor()
                    {
                        final ResourceLocation field_148726_a = new ResourceLocation(s1, soundentry.func_148556_a());
                        private static final String __OBFID = "CL_00001149";
                        public int func_148721_a()
                        {
                            SoundEventAccessorComposite soundeventaccessorcomposite1 = (SoundEventAccessorComposite)SoundHandler.this.field_147697_e.getObject(this.field_148726_a);
                            return soundeventaccessorcomposite1 == null ? 0 : soundeventaccessorcomposite1.func_148721_a();
                        }
                        public SoundPoolEntry func_148720_g()
                        {
                            SoundEventAccessorComposite soundeventaccessorcomposite1 = (SoundEventAccessorComposite)SoundHandler.this.field_147697_e.getObject(this.field_148726_a);
                            return soundeventaccessorcomposite1 == null ? SoundHandler.field_147700_a : soundeventaccessorcomposite1.func_148720_g();
                        }
                    };
                    break;
                default:
                    throw new IllegalStateException("IN YOU FACE");
            }

            soundeventaccessorcomposite.func_148727_a((ISoundEventAccessor)object);
        }
    }

    public SoundEventAccessorComposite func_147680_a(ResourceLocation p_147680_1_)
    {
        return (SoundEventAccessorComposite)this.field_147697_e.getObject(p_147680_1_);
    }

    public void func_147682_a(ISound p_147682_1_)
    {
        this.field_147694_f.func_148611_c(p_147682_1_);
    }

    public void func_147681_a(ISound p_147681_1_, int p_147681_2_)
    {
        this.field_147694_f.func_148599_a(p_147681_1_, p_147681_2_);
    }

    public void func_147691_a(EntityPlayer p_147691_1_, float p_147691_2_)
    {
        this.field_147694_f.func_148615_a(p_147691_1_, p_147691_2_);
    }

    public void func_147689_b()
    {
        this.field_147694_f.func_148610_e();
    }

    public void func_147690_c()
    {
        this.field_147694_f.func_148614_c();
    }

    public void func_147685_d()
    {
        this.field_147694_f.func_148613_b();
    }

    // JAVADOC METHOD $$ func_73660_a
    public void update()
    {
        this.field_147694_f.func_148605_d();
    }

    public void func_147687_e()
    {
        this.field_147694_f.func_148604_f();
    }

    public void func_147684_a(SoundCategory p_147684_1_, float p_147684_2_)
    {
        if (p_147684_1_ == SoundCategory.MASTER && p_147684_2_ <= 0.0F)
        {
            this.func_147690_c();
        }

        this.field_147694_f.func_148601_a(p_147684_1_, p_147684_2_);
    }

    public void func_147683_b(ISound p_147683_1_)
    {
        this.field_147694_f.func_148602_b(p_147683_1_);
    }

    public SoundEventAccessorComposite func_147686_a(SoundCategory ... p_147686_1_)
    {
        ArrayList arraylist = Lists.newArrayList();
        Iterator iterator = this.field_147697_e.func_148742_b().iterator();

        while (iterator.hasNext())
        {
            ResourceLocation resourcelocation = (ResourceLocation)iterator.next();
            SoundEventAccessorComposite soundeventaccessorcomposite = (SoundEventAccessorComposite)this.field_147697_e.getObject(resourcelocation);

            if (ArrayUtils.contains(p_147686_1_, soundeventaccessorcomposite.func_148728_d()))
            {
                arraylist.add(soundeventaccessorcomposite);
            }
        }

        if (arraylist.isEmpty())
        {
            return null;
        }
        else
        {
            return (SoundEventAccessorComposite)arraylist.get((new Random()).nextInt(arraylist.size()));
        }
    }

    public boolean func_147692_c(ISound p_147692_1_)
    {
        return this.field_147694_f.func_148597_a(p_147692_1_);
    }

    @SideOnly(Side.CLIENT)

    static final class SwitchType
        {
            static final int[] field_148765_a = new int[SoundList.SoundEntry.Type.values().length];
            private static final String __OBFID = "CL_00001150";

            static
            {
                try
                {
                    field_148765_a[SoundList.SoundEntry.Type.FILE.ordinal()] = 1;
                }
                catch (NoSuchFieldError var2)
                {
                    ;
                }

                try
                {
                    field_148765_a[SoundList.SoundEntry.Type.SOUND_EVENT.ordinal()] = 2;
                }
                catch (NoSuchFieldError var1)
                {
                    ;
                }
            }
        }
}