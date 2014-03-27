package net.minecraftforge.client.keyboard;

import com.google.common.collect.ForwardingList;
import net.minecraft.client.settings.KeyBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * @author BalthezarOi
 * @CC-License Created under the CC BY-NC-ND license by BalthezarOi
 */
public class KeyboardSwitch extends ForwardingList<KeyBinding> {
    public static final KeyboardSwitch MINECRAFT_KEYBOARD_SWITCH = new KeyboardSwitch("Minecraft Keyboard Switch");
    public final String name;

    public KeyboardSwitch(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Keyboard Switch - " + name;
    }

    @Override
    protected List<KeyBinding> delegate() {
        return new ArrayList<KeyBinding>();
    }
}
