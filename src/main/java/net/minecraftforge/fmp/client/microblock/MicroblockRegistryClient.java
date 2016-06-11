package net.minecraftforge.fmp.client.microblock;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import net.minecraftforge.fmp.microblock.IMicroMaterial;

/**
 * The clientside microblock registry. Allows you to register a custom {@link IMicroModelProvider} for a multipart.
 */
public class MicroblockRegistryClient
{
    
    private static final Map<IMicroMaterial, IMicroModelProvider> materialModelProviders = new HashMap<IMicroMaterial, IMicroModelProvider>();

    public static void registerMaterialModelProvider(IMicroMaterial material, IMicroModelProvider provider)
    {
        Preconditions.checkNotNull(material, "Attempting to assign a microblock model provider to a null material!");
        Preconditions.checkNotNull(provider, "Attempting to register a null microblock model provider!");
        materialModelProviders.put(material, provider);
    }

    public static IMicroModelProvider getModelProviderFor(IMicroMaterial material)
    {
        return materialModelProviders.get(material);
    }

}
