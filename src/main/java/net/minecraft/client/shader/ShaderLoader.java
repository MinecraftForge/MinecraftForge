package net.minecraft.client.shader;

import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

@SideOnly(Side.CLIENT)
public class ShaderLoader
{
    private final ShaderLoader.ShaderType field_148061_a;
    private final String field_148059_b;
    private int field_148060_c;
    private int field_148058_d = 0;
    private static final String __OBFID = "CL_00001043";

    private ShaderLoader(ShaderLoader.ShaderType p_i45091_1_, int p_i45091_2_, String p_i45091_3_)
    {
        this.field_148061_a = p_i45091_1_;
        this.field_148060_c = p_i45091_2_;
        this.field_148059_b = p_i45091_3_;
    }

    public void func_148056_a(ShaderManager p_148056_1_)
    {
        ++this.field_148058_d;
        GL20.glAttachShader(p_148056_1_.func_147986_h(), this.field_148060_c);
    }

    public void func_148054_b(ShaderManager p_148054_1_)
    {
        --this.field_148058_d;

        if (this.field_148058_d <= 0)
        {
            GL20.glDeleteShader(this.field_148060_c);
            this.field_148061_a.func_148064_d().remove(this.field_148059_b);
        }
    }

    public String func_148055_a()
    {
        return this.field_148059_b;
    }

    public static ShaderLoader func_148057_a(IResourceManager p_148057_0_, ShaderLoader.ShaderType p_148057_1_, String p_148057_2_) throws IOException
    {
        ShaderLoader shaderloader = (ShaderLoader)p_148057_1_.func_148064_d().get(p_148057_2_);

        if (shaderloader == null)
        {
            ResourceLocation resourcelocation = new ResourceLocation("shaders/program/" + p_148057_2_ + p_148057_1_.func_148063_b());
            BufferedInputStream bufferedinputstream = new BufferedInputStream(p_148057_0_.getResource(resourcelocation).getInputStream());
            byte[] abyte = IOUtils.toByteArray(bufferedinputstream);
            ByteBuffer bytebuffer = BufferUtils.createByteBuffer(abyte.length);
            bytebuffer.put(abyte);
            bytebuffer.position(0);
            int i = GL20.glCreateShader(p_148057_1_.func_148065_c());
            GL20.glShaderSource(i, bytebuffer);
            GL20.glCompileShader(i);

            if (GL20.glGetShaderi(i, 35713) == 0)
            {
                String s1 = StringUtils.trim(GL20.glGetShaderInfoLog(i, 32768));
                JsonException jsonexception = new JsonException("Couldn\'t compile " + p_148057_1_.func_148062_a() + " program: " + s1);
                jsonexception.func_151381_b(resourcelocation.getResourcePath());
                throw jsonexception;
            }

            shaderloader = new ShaderLoader(p_148057_1_, i, p_148057_2_);
            p_148057_1_.func_148064_d().put(p_148057_2_, shaderloader);
        }

        return shaderloader;
    }

    @SideOnly(Side.CLIENT)
    public static enum ShaderType
    {
        VERTEX("vertex", ".vsh", 35633),
        FRAGMENT("fragment", ".fsh", 35632);
        private final String field_148072_c;
        private final String field_148069_d;
        private final int field_148070_e;
        private final Map field_148067_f = Maps.newHashMap();

        private static final String __OBFID = "CL_00001044";

        private ShaderType(String p_i45090_3_, String p_i45090_4_, int p_i45090_5_)
        {
            this.field_148072_c = p_i45090_3_;
            this.field_148069_d = p_i45090_4_;
            this.field_148070_e = p_i45090_5_;
        }

        public String func_148062_a()
        {
            return this.field_148072_c;
        }

        protected String func_148063_b()
        {
            return this.field_148069_d;
        }

        protected int func_148065_c()
        {
            return this.field_148070_e;
        }

        protected Map func_148064_d()
        {
            return this.field_148067_f;
        }
    }
}