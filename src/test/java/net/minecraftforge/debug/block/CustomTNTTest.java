package net.minecraftforge.debug.block;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.TNTBlock;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(CustomTNTTest.MODID)
@Mod.EventBusSubscriber(modid = CustomTNTTest.MODID, bus = Bus.MOD)
public class CustomTNTTest
{
    static final String MODID = "custom_tnt_test";
    static final String BLOCK_ID = "test_tnt";

    private static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = new DeferredRegister<>(ForgeRegistries.ENTITIES, MODID);

    public static final RegistryObject<TNTBlock> CUSTOM_TNT = BLOCKS.register(BLOCK_ID, () -> new TNTBlock(Block.Properties.from(Blocks.TNT), CustomTNTEntity::new));
    public static final RegistryObject<EntityType<CustomTNTEntity>> CUSTOM_TNT_ENTITY = ENTITIES.register(BLOCK_ID, () -> EntityType.Builder
                .create((EntityType.IFactory<CustomTNTEntity>) CustomTNTEntity::new, EntityClassification.MISC)
                .build(BLOCK_ID));

    static {
        ITEMS.register(BLOCK_ID, () -> new BlockItem(CUSTOM_TNT.get(), new Item.Properties()));
    }

    public CustomTNTTest() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        ENTITIES.register(modBus);
    }

    public static class CustomTNTEntity extends TNTEntity {

        public CustomTNTEntity(EntityType<CustomTNTEntity> type, World world) {
            super(type, world);
            setFuse(20);
        }

        public CustomTNTEntity(World world, double x, double y, double z, LivingEntity placer) {
            super(world, x, y, z, placer);
            setFuse(20);
        }

        @Nonnull
        @Override
        public EntityType<?> getType() {
            return CUSTOM_TNT_ENTITY.get();
        }

        @Override
        protected void explode() {
            this.world.createExplosion(this, this.posX, this.posY, this.posZ, 10.0F, Explosion.Mode.BREAK);
        }
    }
}