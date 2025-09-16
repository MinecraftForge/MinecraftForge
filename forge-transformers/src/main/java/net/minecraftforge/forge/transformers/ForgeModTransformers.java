package net.minecraftforge.forge.transformers;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ForgeModTransformers implements ITransformationService {
    @Override
    public @NotNull String name() {
        return "forge";
    }

    @Override
    public void initialize(IEnvironment environment) { }

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) { }

    @Override
    @SuppressWarnings("rawtypes")
    public @NotNull List<ITransformer> transformers() {
        var transformers = new ArrayList<ITransformer>(FieldToMethodTransformer.getAll());
        transformers.add(MethodRedirector.INSTANCE);
        return transformers;
    }
}
