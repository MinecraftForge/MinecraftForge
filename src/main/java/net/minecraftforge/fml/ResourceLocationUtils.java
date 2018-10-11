package net.minecraftforge.fml;

import com.google.common.base.Joiner;
import net.minecraft.util.ResourceLocation;

import java.nio.file.Path;

public class ResourceLocationUtils {
    public static ResourceLocation pathToResourceLocation(Path path, int maxDepth) {
        return new ResourceLocation(path.getName(0).toString(), Joiner.on("/").join(path.subpath(1, Math.min(maxDepth, path.getNameCount())).iterator()));
    }
}
