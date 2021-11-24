package net.minecraftforge.debug.chat;

import com.mojang.brigadier.Command;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static java.util.Calendar.MAY;

@Mod("translatable_component_formatting_test")
public class TranslatableComponentFormattingTest
{
    public TranslatableComponentFormattingTest()
    {
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
    }

    private void registerCommands(RegisterCommandsEvent evt)
    {
        evt.getDispatcher().register(Commands.literal("test_component_formatting").executes(context -> {
            // tests copied from java.util.Formatter javadoc
            context.getSource().sendSuccess(new TranslatableComponent("translatable.formatting.test1", "a", new TextComponent("b"), "c", "d"), false);
            context.getSource().sendSuccess(new TranslatableComponent("translatable.formatting.test2", Math.E), false);
            context.getSource().sendSuccess(new TranslatableComponent("translatable.formatting.test3", -6217.585657), false);
            context.getSource().sendSuccess(new TranslatableComponent("translatable.formatting.test4", Calendar.getInstance()), false);
            Calendar c = new GregorianCalendar(1995, MAY, 23);
            context.getSource().sendSuccess(new TranslatableComponent("translatable.formatting.test5", c), false);
            return Command.SINGLE_SUCCESS;
        }));
    }
}
