package net.minecraftforge.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.client.IKeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import org.lwjgl.input.Keyboard;

@Mod(modid = KeyBindingDebug.MODID)
public class KeyBindingDebug
{
    public static final String MODID = "ForgeKeyBindingDebug";

    @SidedProxy(serverSide = "net.minecraftforge.debug.KeyBindingDebug$CommonProxy", clientSide = "net.minecraftforge.debug.KeyBindingDebug$ClientProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void init(FMLInitializationEvent event) { proxy.init(event); }

    public static class CommonProxy
    {
        public void init(FMLInitializationEvent event) {}
    }

    public static class ClientProxy extends CommonProxy
    {
        public static final String CATEGORY_REGULAR = "forge.key.category.regular";
        public static final String KEY_TEST_CONTEXT_A = "forge.key.testContextA";
        public static final String KEY_TEST_CONTEXT_B = "forge.key.testContextB";
        public static final String CONTEXT_NAME = "test";
        public static final String KEY_REGULAR = "forge.key.regular";

        @Override
        public void init(FMLInitializationEvent event)
        {
            ClientRegistry.registerKeyBindingContext(KEY_TEST_CONTEXT_A, Keyboard.KEY_N, CONTEXT_NAME, new IKeyBinding()
            {
                @Override
                public boolean onKeyDown(Minecraft minecraft, EntityPlayer player, String context, boolean ctrl, boolean shift, boolean alt)
                {
                    ItemStack itemStack = player.getHeldItem();
                    if (itemStack != null && itemStack.getItem() == Items.stick)
                    {
                        player.addChatMessage(new ChatComponentText("Just a stick..."));
                        return true;
                    }

                    player.addChatMessage(new ChatComponentText("Not a stick..."));
                    return false;
                }
            });

            ClientRegistry.registerKeyBindingContext(KEY_TEST_CONTEXT_B, Keyboard.KEY_M, CONTEXT_NAME, new IKeyBinding()
            {
                @Override
                public boolean onKeyDown(Minecraft minecraft, EntityPlayer player, String context, boolean ctrl, boolean shift, boolean alt)
                {
                    ItemStack itemStack = player.getHeldItem();
                    if (itemStack != null && itemStack.getItem() == Items.diamond && ctrl && !shift && !alt)
                    {
                        player.addChatMessage(new ChatComponentText("DIAMONDS!"));
                        return true;
                    }

                    player.addChatMessage(new ChatComponentText("Not a diamond..."));
                    return false;
                }
            });

            ClientRegistry.registerKeyBinding(KEY_REGULAR, Keyboard.KEY_B, CATEGORY_REGULAR, new IKeyBinding()
            {
                @Override
                public boolean onKeyDown(Minecraft minecraft, EntityPlayer player, String context, boolean ctrl, boolean shift, boolean alt)
                {
                    player.addChatMessage(new ChatComponentText("Just a regular key binding."));
                    return true;
                }
            });
        }
    }
}
