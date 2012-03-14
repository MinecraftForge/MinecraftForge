package net.minecraft.src;

public class ExtendedBlockStorage
{
    private int field_48615_a;
    private int field_48613_b;
    private int field_48614_c;
    private byte[] field_48611_d;
    private NibbleArray field_48612_e;
    private NibbleArray field_48609_f;
    private NibbleArray field_48610_g;
    private NibbleArray field_48616_h;

    public ExtendedBlockStorage(int par1)
    {
        this.field_48615_a = par1;
        this.field_48611_d = new byte[4096];
        this.field_48609_f = new NibbleArray(this.field_48611_d.length, 4);
        this.field_48616_h = new NibbleArray(this.field_48611_d.length, 4);
        this.field_48610_g = new NibbleArray(this.field_48611_d.length, 4);
    }

    public int func_48591_a(int par1, int par2, int par3)
    {
        int var4 = this.field_48611_d[par2 << 8 | par3 << 4 | par1] & 255;
        return this.field_48612_e != null ? this.field_48612_e.get(par1, par2, par3) << 8 | var4 : var4;
    }

    public void func_48588_a(int par1, int par2, int par3, int par4)
    {
        int var5 = this.field_48611_d[par2 << 8 | par3 << 4 | par1] & 255;

        if (this.field_48612_e != null)
        {
            var5 |= this.field_48612_e.get(par1, par2, par3) << 8;
        }

        if (var5 == 0 && par4 != 0)
        {
            ++this.field_48613_b;

            if (Block.blocksList[par4] != null && Block.blocksList[par4].func_48125_m())
            {
                ++this.field_48614_c;
            }
        }
        else if (var5 != 0 && par4 == 0)
        {
            --this.field_48613_b;

            if (Block.blocksList[var5] != null && Block.blocksList[var5].func_48125_m())
            {
                --this.field_48614_c;
            }
        }
        else if (Block.blocksList[var5] != null && Block.blocksList[var5].func_48125_m() && (Block.blocksList[par4] == null || !Block.blocksList[par4].func_48125_m()))
        {
            --this.field_48614_c;
        }
        else if ((Block.blocksList[var5] == null || !Block.blocksList[var5].func_48125_m()) && Block.blocksList[par4] != null && Block.blocksList[par4].func_48125_m())
        {
            ++this.field_48614_c;
        }

        this.field_48611_d[par2 << 8 | par3 << 4 | par1] = (byte)(par4 & 255);

        if (par4 > 255)
        {
            if (this.field_48612_e == null)
            {
                this.field_48612_e = new NibbleArray(this.field_48611_d.length, 4);
            }

            this.field_48612_e.set(par1, par2, par3, (par4 & 3840) >> 8);
        }
        else if (this.field_48612_e != null)
        {
            this.field_48612_e.set(par1, par2, par3, 0);
        }
    }

    public int func_48598_b(int par1, int par2, int par3)
    {
        return this.field_48609_f.get(par1, par2, par3);
    }

    public void func_48585_b(int par1, int par2, int par3, int par4)
    {
        this.field_48609_f.set(par1, par2, par3, par4);
    }

    public boolean func_48595_a()
    {
        return this.field_48613_b == 0;
    }

    public boolean func_48607_b()
    {
        return this.field_48614_c > 0;
    }

    public int func_48597_c()
    {
        return this.field_48615_a;
    }

    public void func_48592_c(int par1, int par2, int par3, int par4)
    {
        this.field_48616_h.set(par1, par2, par3, par4);
    }

    public int func_48602_c(int par1, int par2, int par3)
    {
        return this.field_48616_h.get(par1, par2, par3);
    }

    public void func_48608_d(int par1, int par2, int par3, int par4)
    {
        this.field_48610_g.set(par1, par2, par3, par4);
    }

    public int func_48604_d(int par1, int par2, int par3)
    {
        return this.field_48610_g.get(par1, par2, par3);
    }

    public void func_48599_d()
    {
        this.field_48613_b = 0;
        this.field_48614_c = 0;

        for (int var1 = 0; var1 < 16; ++var1)
        {
            for (int var2 = 0; var2 < 16; ++var2)
            {
                for (int var3 = 0; var3 < 16; ++var3)
                {
                    int var4 = this.func_48591_a(var1, var2, var3);

                    if (var4 > 0)
                    {
                        if (Block.blocksList[var4] == null)
                        {
                            this.field_48611_d[var2 << 8 | var3 << 4 | var1] = 0;

                            if (this.field_48612_e != null)
                            {
                                this.field_48612_e.set(var1, var2, var3, 0);
                            }
                        }
                        else
                        {
                            ++this.field_48613_b;

                            if (Block.blocksList[var4].func_48125_m())
                            {
                                ++this.field_48614_c;
                            }
                        }
                    }
                }
            }
        }
    }

    public void func_48603_e() {}

    public int func_48587_f()
    {
        return this.field_48613_b;
    }

    public byte[] func_48590_g()
    {
        return this.field_48611_d;
    }

    public NibbleArray func_48601_h()
    {
        return this.field_48612_e;
    }

    public NibbleArray func_48594_i()
    {
        return this.field_48609_f;
    }

    public NibbleArray func_48600_j()
    {
        return this.field_48610_g;
    }

    public NibbleArray func_48605_k()
    {
        return this.field_48616_h;
    }

    public void func_48596_a(byte[] par1ArrayOfByte)
    {
        this.field_48611_d = par1ArrayOfByte;
    }

    public void func_48593_a(NibbleArray par1NibbleArray)
    {
        this.field_48612_e = par1NibbleArray;
    }

    public void func_48586_b(NibbleArray par1NibbleArray)
    {
        this.field_48609_f = par1NibbleArray;
    }

    public void func_48606_c(NibbleArray par1NibbleArray)
    {
        this.field_48610_g = par1NibbleArray;
    }

    public void func_48589_d(NibbleArray par1NibbleArray)
    {
        this.field_48616_h = par1NibbleArray;
    }
}
