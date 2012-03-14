package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KeyBinding
{
    public static List keybindArray = new ArrayList();
    public static IntHashMap hash = new IntHashMap();
    public String keyDescription;
    public int keyCode;

    /** because _303 wanted me to call it that(Caironater) */
    public boolean pressed;
    public int pressTime = 0;

    public static void onTick(int par0)
    {
        KeyBinding var1 = (KeyBinding)hash.lookup(par0);

        if (var1 != null)
        {
            ++var1.pressTime;
        }
    }

    public static void setKeyBindState(int par0, boolean par1)
    {
        KeyBinding var2 = (KeyBinding)hash.lookup(par0);

        if (var2 != null)
        {
            var2.pressed = par1;
        }
    }

    public static void unPressAllKeys()
    {
        Iterator var0 = keybindArray.iterator();

        while (var0.hasNext())
        {
            KeyBinding var1 = (KeyBinding)var0.next();
            var1.unpressKey();
        }
    }

    public static void resetKeyBindingArrayAndHash()
    {
        hash.clearMap();
        Iterator var0 = keybindArray.iterator();

        while (var0.hasNext())
        {
            KeyBinding var1 = (KeyBinding)var0.next();
            hash.addKey(var1.keyCode, var1);
        }
    }

    public KeyBinding(String par1Str, int par2)
    {
        this.keyDescription = par1Str;
        this.keyCode = par2;
        keybindArray.add(this);
        hash.addKey(par2, this);
    }

    public boolean isPressed()
    {
        if (this.pressTime == 0)
        {
            return false;
        }
        else
        {
            --this.pressTime;
            return true;
        }
    }

    private void unpressKey()
    {
        this.pressTime = 0;
        this.pressed = false;
    }
}
