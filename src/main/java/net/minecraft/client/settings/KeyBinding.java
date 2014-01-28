package net.minecraft.client.settings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IntHashMap;

@SideOnly(Side.CLIENT)
public class KeyBinding implements Comparable
{
    private static final List keybindArray = new ArrayList();
    private static final IntHashMap hash = new IntHashMap();
    private static final Set field_151473_c = new HashSet();
    private final String keyDescription;
    private final int field_151472_e;
    private final String field_151471_f;
    private int keyCode;
    // JAVADOC FIELD $$ field_74513_e
    private boolean pressed;
    private int field_151474_i;
    private static final String __OBFID = "CL_00000628";

    public static void onTick(int par0)
    {
        if (par0 != 0)
        {
            KeyBinding keybinding = (KeyBinding)hash.lookup(par0);

            if (keybinding != null)
            {
                ++keybinding.field_151474_i;
            }
        }
    }

    public static void setKeyBindState(int par0, boolean par1)
    {
        if (par0 != 0)
        {
            KeyBinding keybinding = (KeyBinding)hash.lookup(par0);

            if (keybinding != null)
            {
                keybinding.pressed = par1;
            }
        }
    }

    public static void unPressAllKeys()
    {
        Iterator iterator = keybindArray.iterator();

        while (iterator.hasNext())
        {
            KeyBinding keybinding = (KeyBinding)iterator.next();
            keybinding.unpressKey();
        }
    }

    public static void resetKeyBindingArrayAndHash()
    {
        hash.clearMap();
        Iterator iterator = keybindArray.iterator();

        while (iterator.hasNext())
        {
            KeyBinding keybinding = (KeyBinding)iterator.next();
            hash.addKey(keybinding.keyCode, keybinding);
        }
    }

    public static Set func_151467_c()
    {
        return field_151473_c;
    }

    public KeyBinding(String p_i45001_1_, int p_i45001_2_, String p_i45001_3_)
    {
        this.keyDescription = p_i45001_1_;
        this.keyCode = p_i45001_2_;
        this.field_151472_e = p_i45001_2_;
        this.field_151471_f = p_i45001_3_;
        keybindArray.add(this);
        hash.addKey(p_i45001_2_, this);
        field_151473_c.add(p_i45001_3_);
    }

    public boolean func_151470_d()
    {
        return this.pressed;
    }

    public String func_151466_e()
    {
        return this.field_151471_f;
    }

    public boolean func_151468_f()
    {
        if (this.field_151474_i == 0)
        {
            return false;
        }
        else
        {
            --this.field_151474_i;
            return true;
        }
    }

    private void unpressKey()
    {
        this.field_151474_i = 0;
        this.pressed = false;
    }

    public String func_151464_g()
    {
        return this.keyDescription;
    }

    public int func_151469_h()
    {
        return this.field_151472_e;
    }

    public int func_151463_i()
    {
        return this.keyCode;
    }

    public void func_151462_b(int p_151462_1_)
    {
        this.keyCode = p_151462_1_;
    }

    public int compareTo(KeyBinding p_151465_1_)
    {
        int i = I18n.getStringParams(this.field_151471_f, new Object[0]).compareTo(I18n.getStringParams(p_151465_1_.field_151471_f, new Object[0]));

        if (i == 0)
        {
            i = I18n.getStringParams(this.keyDescription, new Object[0]).compareTo(I18n.getStringParams(p_151465_1_.keyDescription, new Object[0]));
        }

        return i;
    }

    public int compareTo(Object par1Obj)
    {
        return this.compareTo((KeyBinding)par1Obj);
    }
}