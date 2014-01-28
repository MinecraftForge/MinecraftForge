package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class SoundEventAccessorComposite implements ISoundEventAccessor
{
    private final List field_148736_a = Lists.newArrayList();
    private final Random field_148734_b = new Random();
    private final ResourceLocation field_148735_c;
    private final SoundCategory field_148732_d;
    private double field_148733_e;
    private double field_148731_f;
    private static final String __OBFID = "CL_00001146";

    public SoundEventAccessorComposite(ResourceLocation p_i45120_1_, double p_i45120_2_, double p_i45120_4_, SoundCategory p_i45120_6_)
    {
        this.field_148735_c = p_i45120_1_;
        this.field_148731_f = p_i45120_4_;
        this.field_148733_e = p_i45120_2_;
        this.field_148732_d = p_i45120_6_;
    }

    public int func_148721_a()
    {
        int i = 0;
        ISoundEventAccessor isoundeventaccessor;

        for (Iterator iterator = this.field_148736_a.iterator(); iterator.hasNext(); i += isoundeventaccessor.func_148721_a())
        {
            isoundeventaccessor = (ISoundEventAccessor)iterator.next();
        }

        return i;
    }

    public SoundPoolEntry func_148720_g()
    {
        int i = this.func_148721_a();

        if (!this.field_148736_a.isEmpty() && i != 0)
        {
            int j = this.field_148734_b.nextInt(i);
            Iterator iterator = this.field_148736_a.iterator();
            ISoundEventAccessor isoundeventaccessor;

            do
            {
                if (!iterator.hasNext())
                {
                    return SoundHandler.field_147700_a;
                }

                isoundeventaccessor = (ISoundEventAccessor)iterator.next();
                j -= isoundeventaccessor.func_148721_a();
            }
            while (j >= 0);

            SoundPoolEntry soundpoolentry = (SoundPoolEntry)isoundeventaccessor.func_148720_g();
            soundpoolentry.func_148651_a(soundpoolentry.func_148650_b() * this.field_148733_e);
            soundpoolentry.func_148647_b(soundpoolentry.func_148649_c() * this.field_148731_f);
            return soundpoolentry;
        }
        else
        {
            return SoundHandler.field_147700_a;
        }
    }

    public void func_148727_a(ISoundEventAccessor p_148727_1_)
    {
        this.field_148736_a.add(p_148727_1_);
    }

    public ResourceLocation func_148729_c()
    {
        return this.field_148735_c;
    }

    public SoundCategory func_148728_d()
    {
        return this.field_148732_d;
    }
}