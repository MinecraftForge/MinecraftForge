package net.minecraftforge.debug.client;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourcePack;
import net.minecraft.resources.ResourcePackFileNotFoundException;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

@Mod(CustomResourcePackTest.MOD_ID)
public class CustomResourcePackTest
{
    public static final String MOD_ID = "custom_resource_pack_test";

    public CustomResourcePackTest()
    {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () ->
        {
            Minecraft.getInstance().getResourcePackList().addPackFinder(DummyResourcePack::finder);
        });
    }

    public static class DummyResourcePack extends ResourcePack
    {

        private static final PackMetadataSection METADATA = new PackMetadataSection(new StringTextComponent("Dummy Test Resource Pack"), 5);
        public static final DummyResourcePack INSTANCE = new DummyResourcePack();
        private DummyResourcePack()
        {
            super(new File("dummy"));
        }

        @Override
        public <T> T getMetadata(IMetadataSectionSerializer<T> deserializer)
        {
            if (deserializer.getSectionName().equals("pack"))
            {
                return (T) METADATA;
            }
            return null;
        }

        @Override
        public String getName()
        {
            return MOD_ID;
        }

        @Override
        protected InputStream getInputStream(String resourcePath) throws IOException
        {
            throw new ResourcePackFileNotFoundException(this.file, resourcePath);
        }

        @Override
        protected boolean resourceExists(String resourcePath)
        {
            return false;
        }

        @Override
        public Collection<ResourceLocation> func_225637_a_(ResourcePackType p_225637_1_, String p_225637_2_, String p_225637_3_, int p_225637_4_, Predicate<String> p_225637_5_)
        {
            return Collections.emptyList();
        }

        @Override
        public Set<String> getResourceNamespaces(ResourcePackType type)
        {
            return Collections.emptySet();
        }

        @Override
        public void close() throws IOException {}

        public static <T extends ResourcePackInfo> void finder(Map<String, T> map, ResourcePackInfo.IFactory<T> factory)
        {
            map.put(MOD_ID, ResourcePackInfo.createResourcePack(MOD_ID, false, () -> DummyResourcePack.INSTANCE, factory, ResourcePackInfo.Priority.TOP));
        }
    }
}
