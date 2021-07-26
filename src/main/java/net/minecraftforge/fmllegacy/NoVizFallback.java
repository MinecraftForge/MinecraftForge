/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.fmllegacy;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.MemoryStack;

import java.util.EnumSet;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memUTF8;

public final class NoVizFallback {
    public static LongSupplier fallback(IntSupplier width, IntSupplier height, Supplier<String> title, LongSupplier monitor) {
        return () -> createWindow(width.getAsInt(), height.getAsInt(), title.get(), monitor.getAsLong(), 0L);
    }
    
    private enum GLVersion {
        GL46(4, 6),
        GL45(4, 5),
        GL44(4, 4),
        GL43(4, 3),
        GL42(4, 2),
        GL41(4, 1),
        GL40(4, 0),
        GL33(3, 3),
        GL32(3, 2),
        ANY(0, 0),
        ;
        
        final int major;
        final int minor;
        
        GLVersion(int major, int minor) {
            this.major = major;
            this.minor = minor;
        }
        
        static GLVersion fromInt(int version) {
            return switch (version) {
                case 46 -> GL46;
                case 45 -> GL45;
                case 44 -> GL44;
                case 43 -> GL43;
                case 42 -> GL42;
                case 41 -> GL41;
                case 40 -> GL40;
                case 33 -> GL33;
                case 32 -> GL32;
                default -> ANY;
            };
        }
        
        static Iterable<GLVersion> allowedFromVersionInt(int versionInt) {
            GLVersion lowerBound = fromInt(versionInt / 100 % 100);
            GLVersion upperBound = fromInt(versionInt % 100);
            
            if (upperBound == ANY) {
                return EnumSet.range(GL46, GL32);
            }
            // only one bound set, we want that specific version
            if(lowerBound == ANY){
                return EnumSet.of(upperBound);
            }
            return EnumSet.range(upperBound, lowerBound);
        }
    }
    
    private static long createWindow(int width, int height, String title, long monitor, long share) {
        // OpenGL Core profile spec for 3.2 through 4.6 are backwards compatible
        // however, unlike with compatability profiles, asking for a core profile could give you that *exact* version
        // nvidia in particular is a culprit here, giving the exact version asked for and not a newer one
        // that's good for debugging, but not good when you want to use the latest available
        // and there is no way to query available versions besides trying to make a context/window
        // so, ask for the latest available to allow use of newer versions by mods
        // it's on them to check what's actually available
        
        // in case this search doesn't work on some system.
        boolean doGlSearch = Boolean.parseBoolean(System.getProperty("fml.glsearch", "true"));
        if (!doGlSearch) {
            return glfwCreateWindow(width, height, title, monitor, share);
        }
        
        // Version unavailable is expected to be thrown when searching for latest version, *sooo*
        GLFWErrorCallback errorCallback = glfwSetErrorCallback(null);
        
        // for debugging purposes, allows you to specify a specific OpenGL version (or range) to be asked for
        // driver may or may not respect what is asked for
        int allowedVersions = Integer.parseInt(System.getProperty("fml.glversions", "0000"));
        
        long window = NULL;
        for (GLVersion value : GLVersion.allowedFromVersionInt(allowedVersions)) {
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, value.major);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, value.minor);
            window = glfwCreateWindow(width, height, title, monitor, share);
            if (window != NULL) {
                break;
            }
        }
        
        glfwSetErrorCallback(errorCallback);
        
        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
            int GLFWError = glfwGetError(pointerBuffer);
            if (GLFWError != NULL && (window == NULL || GLFWError != GLFW_VERSION_UNAVAILABLE)) {
                long stringAddr = pointerBuffer.get(0);
                if (stringAddr != NULL) {
                    // invoke MC's normal error handler, if it exists
                    if (errorCallback != null) {
                        errorCallback.invoke(GLFWError, stringAddr);
                    } else {
                        System.err.println("GLFW error: " + GLFWError + ": " + memUTF8(stringAddr));
                    }
                }
            }
        }
        return window;
    }
}
