package net.minecraftforge.debug.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * run /testClickEvent to create a clickable chat message to print player locations.
 * run /playerPos to run the client command to print player location
 */
@Mod(ClientCommandTest.MODID)
public class ClientCommandTest
{
    private static final boolean ENABLED = true;

    public static final String MODID = "client_command_test";

    public ClientCommandTest() {
        if (ENABLED) {
            RegisterClientCommandsEvent.BUS.addListener(this::addCommand);
        }
    }

    private void addCommand(RegisterClientCommandsEvent event) {
        event.getDispatcher().
                register(Commands.literal("playerPos")
                        .executes(ctx -> {
                                    this.printLoc();
                                    return 1;
                                }
                        ));
        event.getDispatcher().
                register(Commands.literal("testClickEvent")
                        .executes(ctx -> {
                                    this.testClickEvent();
                                    return 1;
                                }
                        ));
    }

    private void testClickEvent() {
        MutableComponent clickable = Component.literal("Click §b§nHere§r to print your location");
        clickable.withStyle(style -> style.withClickEvent(new ClickEvent.RunCommand("/playerPos")));
        MutableComponent hover = Component.literal("Click Here to print out your location");
        hover.withStyle(style -> style.withColor(net.minecraft.network.chat.TextColor.fromLegacyFormat(ChatFormatting.YELLOW)));
        clickable.withStyle(style -> style.withHoverEvent(new HoverEvent.ShowText(hover)));
        Minecraft.getInstance().gui.getChat().addMessage(clickable);
    }

    private void printLoc() {
        var player = Minecraft.getInstance().player;
        Minecraft.getInstance().gui.getChat().addMessage(Component.literal(player.position().toString()));
    }
}
