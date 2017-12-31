package net.minecraftforge.debug;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.StructureLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = StructureLoadEventTest.MOD_ID, name = "StructureLoadEvent test mod", version = "1.0.0", acceptableRemoteVersions = "*")
public class StructureLoadEventTest
{
    static final String MOD_ID = "structure_load_event_test";
    private static final boolean ENABLED = true;
    private static final ResourceLocation TEST_STRUCTURE_NAME = new ResourceLocation(MOD_ID, "test");
//    private static final ResourceLocation TEST_STRUCTURE_NAME = new ResourceLocation("igloo/igloo_top"); //Uncomment to test overriding a built-in structure

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(StructureLoadEventTest.class);
        }
    }

    @SubscribeEvent
    public static void onStructureLoadEvent(StructureLoadEvent event)
    {
        if (event.getName().equals(TEST_STRUCTURE_NAME))
        {
            Template structure = new Template();
            structure.read(createTestStructureTag());
            event.setStructure(structure);
        }
    }
    
    // Creates a structure that is one stone block
    private static NBTTagCompound createTestStructureTag()
    {
        NBTTagCompound testStructureTag = new NBTTagCompound();
        
        NBTTagList size = new NBTTagList();
        size.appendTag(new NBTTagInt(1));
        size.appendTag(new NBTTagInt(1));
        size.appendTag(new NBTTagInt(1));
        testStructureTag.setTag("size", size);
        
        NBTTagList palette = new NBTTagList();
        palette.appendTag(NBTUtil.writeBlockState(new NBTTagCompound(), Blocks.STONE.getDefaultState()));
        testStructureTag.setTag("palette", palette);
        
        NBTTagList blocks = new NBTTagList();
        NBTTagCompound blockTag = new NBTTagCompound();
        NBTTagList pos = new NBTTagList();
        pos.appendTag(new NBTTagInt(0));
        pos.appendTag(new NBTTagInt(0));
        pos.appendTag(new NBTTagInt(0));
        blockTag.setTag("pos", pos);
        blockTag.setInteger("state", 0);
        blocks.appendTag(blockTag);
        testStructureTag.setTag("blocks", blocks);
        
        return testStructureTag;
    }
}
