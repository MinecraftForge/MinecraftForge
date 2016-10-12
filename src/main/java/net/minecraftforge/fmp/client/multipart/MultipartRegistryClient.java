package net.minecraftforge.fmp.client.multipart;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fmp.multipart.IMultipart;

/**
 * The clientside multipart registry. Allows you to bind a {@link MultipartSpecialRenderer} to a part.
 *
 * @see MultipartSpecialRenderer
 */
public class MultipartRegistryClient
{
    
    private static Map<ResourceLocation, IStateMapper> specialMappers = new HashMap<ResourceLocation, IStateMapper>();
    private static Map<String, MultipartSpecialRenderer<?>> specialRenderers = new IdentityHashMap<String, MultipartSpecialRenderer<?>>();

    public static void registerSpecialPartStateMapper(ResourceLocation part, IStateMapper mapper)
    {
        specialMappers.put(part, mapper);
    }

    public static void registerEmptySpecialPartStateMapper(ResourceLocation part)
    {
        registerSpecialPartStateMapper(part, new IStateMapper()
        {
            
            @Override
            public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block blockIn)
            {
                return Collections.emptyMap();
            }
        });
    }

    /**
     * Binds a {@link MultipartSpecialRenderer} to the specified part.
     *
     * @see MultipartSpecialRenderer
     */
    public static <T extends IMultipart> void bindMultipartSpecialRenderer(ResourceLocation partType, MultipartSpecialRenderer<T> renderer)
    {
        specialRenderers.put(partType.toString().intern(), renderer);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IMultipart> MultipartSpecialRenderer<T> getSpecialRenderer(IMultipart multipart)
    {
        return (MultipartSpecialRenderer<T>) specialRenderers.get(multipart.getType().toString().intern());
    }

    public static IStateMapper getSpecialPartStateMapper(ResourceLocation part)
    {
        return specialMappers.get(part);
    }
}
