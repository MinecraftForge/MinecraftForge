package net.minecraftforge.fml.common.registry;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

/**
 * A class that exposes static references to all vanilla and Forge registries.
 * Created to have a central place to access the registries directly if modders need.
 * It is still advised that if you are registering things to go through {@link GameRegistry} register methods, but queries and iterations can use this.
 */
public class ForgeRegistries
{
    static { init(); } // This must be above the fields so we guarantee it's run before findRegistry is called. Yay static inializers

    public static final IForgeRegistry<Block>        BLOCKS       = GameRegistry.findRegistry(Block.class);
    public static final IForgeRegistry<Item>         ITEMS        = GameRegistry.findRegistry(Item.class);
    public static final IForgeRegistry<Potion>       POTIONS      = GameRegistry.findRegistry(Potion.class);
    public static final IForgeRegistry<Biome>        BIOMES       = GameRegistry.findRegistry(Biome.class);
    public static final IForgeRegistry<SoundEvent>   SOUND_EVENTS = GameRegistry.findRegistry(SoundEvent.class);
    public static final IForgeRegistry<PotionType>   POTION_TYPES = GameRegistry.findRegistry(PotionType.class);
    public static final IForgeRegistry<Enchantment>  ENCHANTMENTS = GameRegistry.findRegistry(Enchantment.class);
    public static final IForgeRegistry<VillagerProfession> VILLAGER_PROFESSIONS = GameRegistry.findRegistry(VillagerProfession.class);


    /**
     * This function is just to make sure static inializers in other classes have run and setup their registries before we query them.
     */
    private static void init()
    {
        GameData.getMain();
        VillagerRegistry.instance();
    }

}
