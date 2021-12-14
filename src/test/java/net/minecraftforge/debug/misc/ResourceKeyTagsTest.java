package net.minecraftforge.debug.misc;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;

/**
 * Test ResourceKeyTags by adding a command that tells you whether you're in a swamp or not based on a biome tag
 */
@Mod(ResourceKeyTagsTest.MODID)
public class ResourceKeyTagsTest
{
    public static final String MODID = "resource_key_tags_test";
    
    // using a prefab forge tag, otherwise use ResourceKeyTags#makeKeyTagWrapper to refer to your own tag jsons
    public static final Tag<ResourceKey<Biome>> SWAMPS = Tags.Biomes.SWAMPS;
    
    public ResourceKeyTagsTest()
    {
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(this::onRegisterCommands);
    }
    
    private void onRegisterCommands(RegisterCommandsEvent event)
    {
        event.getDispatcher().register(
            Commands.literal("resource_key_tags_test_am_i_in_a_swamp")
            .executes(context ->
            {
                CommandSourceStack source = context.getSource();
                Vec3 vec = context.getSource().getPosition();
                BlockPos pos = new BlockPos(vec);
                ServerLevel level = context.getSource().getLevel();
                ResourceKey<Biome> biomeKey = level.getBiomeName(pos).get();
                String output = SWAMPS.contains(biomeKey)
                    ? "You are in a swamp."
                    : "You are not in a swamp.";
                source.sendSuccess(new TextComponent(output), false);
                return 1;
            }));
    }
}
