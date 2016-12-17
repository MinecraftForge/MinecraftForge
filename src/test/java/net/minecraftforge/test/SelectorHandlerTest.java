package net.minecraftforge.test;

import java.util.Collections;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.command.SelectorHandler;
import net.minecraftforge.common.command.SelectorHandlerManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "selectorhandlertest", name = "Selector Handler Test", version = "0.0.0")
public class SelectorHandlerTest
{
    @EventHandler
    public void init(final FMLInitializationEvent event)
    {
        SelectorHandlerManager.register("@s", new Handler());
        SelectorHandlerManager.register("@es", new Handler()); //Should produce a warning
    }

    private static class Handler extends SelectorHandler
    {
        @Override
        public <T extends Entity> List<T> matchEntities(final ICommandSender sender, final String token, final Class<? extends T> targetClass) throws CommandException
        {
            return targetClass.isAssignableFrom(sender.getCommandSenderEntity().getClass())
                ? Collections.singletonList((T) sender.getCommandSenderEntity())
                : Collections.<T> emptyList();
        }

        @Override
        public boolean matchesMultiplePlayers(final String selectorStr) throws CommandException
        {
            return false;
        }
    }
}
