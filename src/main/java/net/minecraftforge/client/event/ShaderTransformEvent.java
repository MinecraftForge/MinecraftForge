package net.minecraftforge.client.event;

import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.Program;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Fired to allow multi-mods to transform shader sources together.
 * This event is fired before shader sources are passed into {@linkplain com.mojang.blaze3d.platform.GlStateManager#glShaderSource(int, List)}
 *
 * <p>This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.</p>
 *
 * <p>This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus},
 * only on the {@linkplain LogicalSide#CLIENT logical client}.</p>
 */
public class ShaderTransformEvent extends Event implements IModBusEvent
{
    private final String originShader;
    private final Program.Type type;
    private final String shaderName;
    private final String shaderSourceName;
    private final GlslPreprocessor glslPreprocessor;
    private String transformedShader;

    public ShaderTransformEvent(String originShader, Program.Type type, String shaderName, String shaderSourceName, GlslPreprocessor glslPreprocessor)
    {
        this.originShader = originShader;
        this.type = type;
        this.transformedShader = originShader;
        this.shaderName = shaderName;
        this.shaderSourceName = shaderSourceName;
        this.glslPreprocessor = glslPreprocessor;
    }

    /**
     * get the origin shader source
     *
     * @return the origin shader source
     */
    public String getOriginShader()
    {
        return originShader;
    }

    /**
     * get the shader type
     *
     * @return the shader type
     */
    public Program.Type getType()
    {
        return type;
    }

    /**
     * get the shader source which may has been transformed by mods
     *
     * @return the shader source which may has been transformed by mods
     */
    public String getTransformedShader()
    {
        return transformedShader;
    }

    /**
     * get the shaderName, without file location and extension
     *
     * @return the shaderName
     */
    public String getShaderName()
    {
        return shaderName;
    }

    /**
     * get the shader source name
     *
     * @return the shader source name
     */
    public String getShaderSourceName()
    {
        return shaderSourceName;
    }

    /**
     * get the context GlslPreprocessor instance.
     * by this you can compile and test if this shader can be compiled successfully
     * <p/>as the current GlslPreprocessor implementations under {@link net.minecraft.client.renderer.ShaderInstance#getOrCreate(ResourceProvider, Program.Type, String)}
     * will record what has been imported.
     * don't forget to change that
     * if you directly or indirectly call {@link GlslPreprocessor#applyImport(boolean, String)}<p/>
     *
     * @return the context GlslPreprocessor instance
     */
    public GlslPreprocessor getGlslPreprocessor()
    {
        return glslPreprocessor;
    }

    /**
     * set the transformed shader source
     *
     * @param transformedShader the transformed shader source
     */
    public void setTransformedShader(String transformedShader)
    {
        this.transformedShader = transformedShader;
    }

    /**
     * transform the shader source by a functional way
     *
     * @param transformFunction the transform function.
     *                          pass originShader and transformed shader
     *                          and return the transformed shader
     */
    public void transformShader(BiFunction<String, String, String> transformFunction)
    {
        this.transformedShader = transformFunction.apply(originShader, transformedShader);
    }

}
