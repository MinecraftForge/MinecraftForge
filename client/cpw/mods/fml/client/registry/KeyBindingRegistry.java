package cpw.mods.fml.client.registry;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.collect.Lists;

import net.minecraft.src.GameSettings;
import net.minecraft.src.KeyBinding;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.registry.TickRegistry;

public class KeyBindingRegistry
{
    public static abstract class KeyHandler implements ITickHandler
    {
        protected KeyBinding keyBinding;
        protected boolean keyDown;
        protected boolean repeating;

        public KeyHandler(KeyBinding keyBinding, boolean repeating)
        {
            this.keyBinding = keyBinding;
            this.repeating = repeating;
        }

        public KeyBinding getKeyBinding()
        {
            return this.keyBinding;
        }

        @Override
        public final void tickStart(EnumSet<TickType> type, Object... tickData)
        {
            keyTick(type, false);
        }

        @Override
        public final void tickEnd(EnumSet<TickType> type, Object... tickData)
        {
            keyTick(type, true);
        }

        private void keyTick(EnumSet<TickType> type, boolean tickEnd)
        {
            int keyCode = keyBinding.field_74512_d;
            boolean state = (keyCode < 0 ? Mouse.isButtonDown(keyCode + 100) : Keyboard.isKeyDown(keyCode));
            if (state != keyDown || (state && repeating))
            {
                if (state)
                {
                    keyDown(type, tickEnd, state!=keyDown);
                }
                else
                {
                    keyUp(type, tickEnd);
                }
                keyDown = state;
            }
        }

        public abstract void keyDown(EnumSet<TickType> types, boolean tickEnd, boolean isRepeat);
        public abstract void keyUp(EnumSet<TickType> types, boolean tickEnd);
    }

    private static List<KeyHandler> keyHandlers = Lists.newArrayList();

    public static void registerKeyBinding(KeyHandler handler) {
        keyHandlers.add(handler);
        TickRegistry.registerTickHandler(handler, Side.CLIENT);
    }

    public static void uploadKeyBindingsToGame(GameSettings settings)
    {
        ArrayList<KeyBinding> harvestedBindings = Lists.newArrayList();
        for (KeyHandler key : keyHandlers)
        {
            harvestedBindings.add(key.keyBinding);
        }
        KeyBinding[] modKeyBindings = harvestedBindings.toArray(new KeyBinding[harvestedBindings.size()]);
        KeyBinding[] allKeys = new KeyBinding[settings.field_74324_K.length + modKeyBindings.length];
        System.arraycopy(settings.field_74324_K, 0, allKeys, 0, settings.field_74324_K.length);
        System.arraycopy(modKeyBindings, 0, allKeys, settings.field_74324_K.length, modKeyBindings.length);
        settings.field_74324_K = allKeys;
        settings.func_74300_a();
    }
}
