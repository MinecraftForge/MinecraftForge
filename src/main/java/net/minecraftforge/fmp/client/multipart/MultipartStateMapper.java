package net.minecraftforge.fmp.client.multipart;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fmp.multipart.MultipartRegistry;

public class MultipartStateMapper extends DefaultStateMapper
{
    
    public static MultipartStateMapper instance = new MultipartStateMapper();

    private boolean replaceNormal = true;

    @Override
    public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block blockIn)
    {
        Map<IBlockState, ModelResourceLocation> mappings = new HashMap<IBlockState, ModelResourceLocation>();
        replaceNormal = false;
        mappings.putAll(super.putStateModelLocations(blockIn));
        replaceNormal = true;

        for (ResourceLocation part : MultipartRegistry.getRegisteredParts())
        {
            IStateMapper mapper = MultipartRegistryClient.getSpecialPartStateMapper(part);
            if (mapper != null)
            {
                mappings.putAll(mapper.putStateModelLocations(blockIn));
            }
            else
            {
                BlockStateContainer state = MultipartRegistry.getDefaultState(part);
                ResourceLocation modelPath = MultipartRegistry.getStateLocation(state);
                for (IBlockState istate : state.getValidStates())
                {
                    mappings.put(istate, new ModelResourceLocation(modelPath, this.getPropertyString(istate.getProperties())));
                }
            }
        }
        return mappings;
    }

    @Override
    public String getPropertyString(Map<IProperty<?>, Comparable<?>> p_178131_1_)
    {
        String str = super.getPropertyString(p_178131_1_);
        if (replaceNormal && str.equals("normal"))
        {
            return "multipart";
        }
        return str;
    }

}
