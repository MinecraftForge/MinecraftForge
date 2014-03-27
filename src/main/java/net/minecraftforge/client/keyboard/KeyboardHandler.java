package net.minecraftforge.client.keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.util.LinkedList;

/**
 * @author BalthezarOi
 * @CC-License Created under the CC BY-NC-ND license by BalthezarOi
 */
public final class KeyboardHandler {
    /**
     * The current keyboard on the client
     */
    public KeyboardSwitch activeKeyBoard = KeyboardSwitch.MINECRAFT_KEYBOARD_SWITCH;

    /**
     * list of registered keyboards among mods
     */
    public final LinkedList<KeyboardSwitch> keyboardSwitches = new LinkedList<KeyboardSwitch>();

    /**
     * The keybindings registered to a keyboard and that are registered with minecraft
     */
    public final LinkedList<KeyBinding> keyBindings = new LinkedList<KeyBinding>();

    /**
     * Register keybinding with a keyboard switch
     *
     * @param keyBinding     the keybinding
     * @param keyboardSwitch the keyboard switch
     */
    public void registerKeybinding(KeyBinding keyBinding, KeyboardSwitch keyboardSwitch) {
        if (instance().keyboardSwitches.size() > 256) {
            System.out.println("[KeyboardHandler] Too many keyBindings registered");
            return;
        }

        if (instance().keyboardSwitches.contains(keyboardSwitch)) {
            System.out.println("[KeyboardHandler] Keybinding already registered + " + keyboardSwitch);
            return;
        }

        keyboardSwitch.add(keyBinding);
        ClientRegistry.registerKeyBinding(keyBinding);
        instance().registerKeyboardSwitch(keyboardSwitch);
    }

    /**
     * Register a keyboard switch
     *
     * @param keyboardSwitch the keyboard switch
     * @return true if the keyboard switch was registered
     */
    private boolean registerKeyboardSwitch(KeyboardSwitch keyboardSwitch) {
        if (this.keyboardSwitches.size() > 256) {
            System.out.println("[KeyboardHandler] Too many Keyboard Switches registered - " + keyboardSwitch);
            return false;
        }

        if (this.keyboardSwitches.contains(keyboardSwitch)) {
            System.out.println("[KeyboardHandler] Keyboard Switch already registered - " + keyboardSwitch);
            return false;
        }

        this.keyboardSwitches.add(keyboardSwitch);
        return true;
    }

    /**
     * Get a registered keyboard switch if the keybinding is registered to a keyboard switch
     *
     * @param keyBinding the Keybinding
     * @return KeyboardSwitch, default the Minecraft Keyboard
     */
    public KeyboardSwitch getKeyboardSwitch(KeyBinding keyBinding) {
        for (KeyboardSwitch keyboardSwitch : keyboardSwitches) {
            if (keyboardSwitch.contains(keyBinding)) {
                return keyboardSwitch;
            }
        }

        return KeyboardSwitch.MINECRAFT_KEYBOARD_SWITCH;
    }

    /**
     * Get a keybinding by name
     *
     * @param name The keybinding description
     * @return returns the first keybinding if the name doesn't match any keybindings
     */
    public KeyBinding getKeyBinding(String name) {
        for (KeyBinding keyBinding : keyBindings) {
            if (name.equals(keyBinding.getKeyDescription())) {
                return keyBinding;
            }
        }

        return keyBindings.get(0);
    }

    /**
     * Switch the keyboards
     */
    public void switchKeyboard() {
        int nextKeyboardSwitch = keyboardSwitches.indexOf(activeKeyBoard) + 1 < 0 ? keyboardSwitches.size() : (keyboardSwitches.indexOf(activeKeyBoard) + 1 > keyboardSwitches.size() ? 0 : keyboardSwitches.indexOf(activeKeyBoard) + 1);
        this.activeKeyBoard = keyboardSwitches.get(nextKeyboardSwitch);
        onKeyboardSwitch(activeKeyBoard);
    }

    /**
     * Switch the keyboard when the appropriate keys are pressed
     */
    @SubscribeEvent
    public void keyPressed(TickEvent.ClientTickEvent event) {
        if (event.side == Side.CLIENT) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) && Keyboard.isKeyDown(Keyboard.KEY_TAB)) {
                switchKeyboard();
            }
        }
    }

    /**
     * Is the keybinding registered to a keyboard switch
     *
     * @param keyBinding the keybinding
     * @return true if the keybinding is registered
     */
    public boolean isKeyBindingRegisteredToKeyboardSwitch(KeyBinding keyBinding) {
        for (KeyboardSwitch keyboardSwitch : keyboardSwitches) {
            if (keyboardSwitch.contains(keyBinding)) return true;
        }

        return false;
    }

    /**
     * Switch the keyboards, either by replacing or deregistering keybindings that are registered to a keyboard
     *
     * @param keyboardSwitch the keyboard switch that is to be the next current keyboard
     */
    private void onKeyboardSwitch(KeyboardSwitch keyboardSwitch) {
// TODO OR SOME OTHER METHOD TO CHANGE THE KEY BINDINGS BETWEEN KEYBOARDS

//        GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
//        purgeMinecraftKeyBindings();
//
//        for (KeyBinding keyBinding : keyboardSwitch) {
//            gameSettings.keyBindings = ArrayUtils.addToArray(gameSettings.keyBindings, keyBinding);
//        }
    }

    /**
     * Remove any keybindings that are registered to a keyboard
     */
    private void purgeMinecraftKeyBindings() {
// TODO method to deregister or remove keybindings registered to a keyboard

//        GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
//
//        for (KeyBinding keyBinding : gameSettings.keyBindings) {
//            if (keyBinding != null) {
//                if (!getMinecraftKeyBindings().contains(keyBinding)) {
//                    if (!isKeyBindingRegisteredToKeyboardSwitch(keyBinding)) {
//                        gameSettings.keyBindings = ArrayUtils.removeFromArray(gameSettings.keyBindings, keyBinding);
//                    }
//                }
//            }
//        }
    }

    /**
     * Get the current keybindings from Minecraft in the client's game settings
     *
     * @return list of vanilla keybindings
     */
    public LinkedList<KeyBinding> getMinecraftKeyBindings() {
        LinkedList<KeyBinding> retrn = new LinkedList<KeyBinding>();
        GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;

        retrn.add(gameSettings.keyBindAttack);
        retrn.add(gameSettings.keyBindBack);
        retrn.add(gameSettings.keyBindChat);
        retrn.add(gameSettings.keyBindCommand);
        retrn.add(gameSettings.keyBindDrop);
        retrn.add(gameSettings.keyBindForward);
        retrn.add(gameSettings.keyBindInventory);
        retrn.add(gameSettings.keyBindJump);
        retrn.add(gameSettings.keyBindLeft);
        retrn.add(gameSettings.keyBindPickBlock);
        retrn.add(gameSettings.keyBindPlayerList);
        retrn.add(gameSettings.keyBindRight);
        retrn.add(gameSettings.keyBindScreenshot);
        retrn.add(gameSettings.keyBindSmoothCamera);
        retrn.add(gameSettings.keyBindSneak);
        retrn.add(gameSettings.keyBindSprint);
        retrn.add(gameSettings.keyBindTogglePerspective);
        retrn.add(gameSettings.keyBindUseItem);

        return retrn;
    }

    private static KeyboardHandler instance;

    public static KeyboardHandler instance() {
        if (instance == null) instance = new KeyboardHandler();
        return instance;
    }
}
