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
        SelectorHandlerManager.register(Handler.name, new Handler());
    }

    private static class Handler implements SelectorHandler
    {
        protected static final String name = "@s";

        @SuppressWarnings("unchecked")
        @Override
        public <T extends Entity> List<T> matchEntities(final ICommandSender sender, final String token, final Class<? extends T> targetClass) throws CommandException
        {
            final Entity senderEntity = sender.getCommandSenderEntity();
            return senderEntity != null && targetClass.isAssignableFrom(senderEntity.getClass()) && name.equals(token)
                ? Collections.singletonList((T) sender.getCommandSenderEntity())
                : Collections.<T> emptyList();
        }

        @Override
        public boolean matchesMultiplePlayers(final String selectorStr) throws CommandException
        {
            return false;
        }

        @Override
        public boolean isSelector(final String selectorStr)
        {
            return name.equals(selectorStr);
        }
    }
}
