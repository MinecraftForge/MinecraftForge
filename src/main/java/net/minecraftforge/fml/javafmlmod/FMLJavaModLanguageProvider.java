/*
 * Minecraft Forge
 * Copyright (c) 2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.javafmlmod;

import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.language.IModLanguageProvider;
import net.minecraftforge.fml.language.IModInfo;
import net.minecraftforge.fml.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.Logging.SCAN;
import static net.minecraftforge.fml.Logging.fmlLog;

public class FMLJavaModLanguageProvider implements IModLanguageProvider
{
    private static class FMLModTarget implements IModLanguageProvider.IModLanguageLoader {
        private final String className;
        private final String modId;

        private FMLModTarget(String className, String modId)
        {
            this.className = className;
            this.modId = modId;
        }

        public String getModId()
        {
            return modId;
        }

        @Override
        public ModContainer loadMod(final IModInfo info, final ClassLoader modClassLoader, final ModFileScanData modFileScanResults)
        {
            return new FMLModContainer(info, className, modClassLoader, modFileScanResults);
        }
    }

    public static final Type MODANNOTATION = Type.getType("Lnet/minecraftforge/fml/common/Mod;");

    @Override
    public String name()
    {
        return "javafml";
    }

    @Override
    public Consumer<ModFileScanData> getFileVisitor() {
        return scanResult -> {
            final Map<String, FMLModTarget> modTargetMap = scanResult.getAnnotations().stream()
                    .filter(ad -> ad.getAnnotationType().equals(MODANNOTATION))
                    .peek(ad -> fmlLog.debug(SCAN, "Found @Mod class {} with id {}", ad.getClassType().getClassName(), ad.getAnnotationData().get("modid")))
                    .map(ad -> new FMLModTarget(ad.getClassType().getClassName(), (String)ad.getAnnotationData().get("modid")))
                    .collect(Collectors.toMap(FMLModTarget::getModId, Function.identity()));
            scanResult.addLanguageLoader(modTargetMap);
        };
    }
}
