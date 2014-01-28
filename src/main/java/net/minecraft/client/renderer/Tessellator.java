package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.PriorityQueue;
import net.minecraft.client.shader.TesselatorVertexState;
import net.minecraft.client.util.QuadComparator;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class Tessellator
{
    private static int nativeBufferSize = 0x200000;
    private static int trivertsInBuffer = (nativeBufferSize / 48) * 6;
    public static boolean renderingWorldRenderer = false;
    public boolean defaultTexture = false;
    private int rawBufferSize = 0;
    public int textureID = 0;

    // JAVADOC FIELD $$ field_78394_d
    private static ByteBuffer byteBuffer = GLAllocation.createDirectByteBuffer(nativeBufferSize * 4);
    private static IntBuffer field_147568_c = byteBuffer.asIntBuffer();
    private static FloatBuffer field_147566_d = byteBuffer.asFloatBuffer();
    private static ShortBuffer field_147567_e = byteBuffer.asShortBuffer();
    // JAVADOC FIELD $$ field_78405_h
    private int[] rawBuffer;
    // JAVADOC FIELD $$ field_78406_i
    private int vertexCount;
    // JAVADOC FIELD $$ field_78403_j
    private double textureU;
    // JAVADOC FIELD $$ field_78404_k
    private double textureV;
    private int brightness;
    // JAVADOC FIELD $$ field_78402_m
    private int color;
    // JAVADOC FIELD $$ field_78399_n
    private boolean hasColor;
    // JAVADOC FIELD $$ field_78400_o
    private boolean hasTexture;
    private boolean hasBrightness;
    // JAVADOC FIELD $$ field_78413_q
    private boolean hasNormals;
    private int field_147569_p;
    // JAVADOC FIELD $$ field_78411_s
    private int addedVertices;
    // JAVADOC FIELD $$ field_78410_t
    private boolean isColorDisabled;
    // JAVADOC FIELD $$ field_78409_u
    private int drawMode;
    // JAVADOC FIELD $$ field_78408_v
    private double xOffset;
    // JAVADOC FIELD $$ field_78407_w
    private double yOffset;
    // JAVADOC FIELD $$ field_78417_x
    private double zOffset;
    // JAVADOC FIELD $$ field_78416_y
    private int normal;
    // JAVADOC FIELD $$ field_78398_a
    public static final Tessellator instance = new Tessellator(2097152);
    // JAVADOC FIELD $$ field_78415_z
    private boolean isDrawing;
    // JAVADOC FIELD $$ field_78388_E
    private int bufferSize;
    private static final String __OBFID = "CL_00000960";

    private Tessellator(int par1)
    {
    }

    public Tessellator()
    {
    }

    static
    {
        instance.defaultTexture = true;
    }

    // JAVADOC METHOD $$ func_78381_a
    public int draw()
    {
        if (!this.isDrawing)
        {
            throw new IllegalStateException("Not tesselating!");
        }
        else
        {
            this.isDrawing = false;

            int offs = 0;
            while (offs < vertexCount)
            {
                int vtc = Math.min(vertexCount - offs, nativeBufferSize >> 5);
                this.field_147568_c.clear();
                this.field_147568_c.put(this.rawBuffer, offs * 8, vtc * 8);
                this.byteBuffer.position(0);
                this.byteBuffer.limit(vtc * 32);
                offs += vtc;

                if (this.hasTexture)
                {
                    this.field_147566_d.position(3);
                    GL11.glTexCoordPointer(2, 32, this.field_147566_d);
                    GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                }

                if (this.hasBrightness)
                {
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
                    this.field_147567_e.position(14);
                    GL11.glTexCoordPointer(2, 32, this.field_147567_e);
                    GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                }

                if (this.hasColor)
                {
                    this.byteBuffer.position(20);
                    GL11.glColorPointer(4, true, 32, this.byteBuffer);
                    GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
                }

                if (this.hasNormals)
                {
                    this.byteBuffer.position(24);
                    GL11.glNormalPointer(32, this.byteBuffer);
                    GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
                }

                this.field_147566_d.position(0);
                GL11.glVertexPointer(3, 32, this.field_147566_d);
                GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                GL11.glDrawArrays(this.drawMode, 0, vtc);
                GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);

                if (this.hasTexture)
                {
                    GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                }

                if (this.hasBrightness)
                {
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
                    GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                    OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                }

                if (this.hasColor)
                {
                    GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
                }

                if (this.hasNormals)
                {
                    GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
                }
            }

            if (rawBufferSize > 0x20000 && field_147569_p < (rawBufferSize << 3))
            {
                rawBufferSize = 0x10000;
                rawBuffer = new int[rawBufferSize];
            }

            int i = this.field_147569_p * 4;
            this.reset();
            return i;
        }
    }

    public TesselatorVertexState func_147564_a(float p_147564_1_, float p_147564_2_, float p_147564_3_)
    {
        int[] aint = new int[this.field_147569_p];
        PriorityQueue priorityqueue = new PriorityQueue(this.field_147569_p, new QuadComparator(this.rawBuffer, p_147564_1_ + (float)this.xOffset, p_147564_2_ + (float)this.yOffset, p_147564_3_ + (float)this.zOffset));
        byte b0 = 32;
        int i;

        for (i = 0; i < this.field_147569_p; i += b0)
        {
            priorityqueue.add(Integer.valueOf(i));
        }

        for (i = 0; !priorityqueue.isEmpty(); i += b0)
        {
            int j = ((Integer)priorityqueue.remove()).intValue();

            for (int k = 0; k < b0; ++k)
            {
                aint[i + k] = this.rawBuffer[j + k];
            }
        }

        System.arraycopy(aint, 0, this.rawBuffer, 0, aint.length);
        return new TesselatorVertexState(aint, this.field_147569_p, this.vertexCount, this.hasTexture, this.hasBrightness, this.hasNormals, this.hasColor);
    }

    public void func_147565_a(TesselatorVertexState p_147565_1_)
    {
        System.arraycopy(p_147565_1_.func_147572_a(), 0, this.rawBuffer, 0, p_147565_1_.func_147572_a().length);
        this.field_147569_p = p_147565_1_.func_147576_b();
        this.vertexCount = p_147565_1_.func_147575_c();
        this.hasTexture = p_147565_1_.func_147573_d();
        this.hasBrightness = p_147565_1_.func_147571_e();
        this.hasColor = p_147565_1_.func_147574_g();
        this.hasNormals = p_147565_1_.func_147570_f();
    }

    // JAVADOC METHOD $$ func_78379_d
    private void reset()
    {
        this.vertexCount = 0;
        this.byteBuffer.clear();
        this.field_147569_p = 0;
        this.addedVertices = 0;
    }

    // JAVADOC METHOD $$ func_78382_b
    public void startDrawingQuads()
    {
        this.startDrawing(7);
    }

    // JAVADOC METHOD $$ func_78371_b
    public void startDrawing(int par1)
    {
        if (this.isDrawing)
        {
            throw new IllegalStateException("Already tesselating!");
        }
        else
        {
            this.isDrawing = true;
            this.reset();
            this.drawMode = par1;
            this.hasNormals = false;
            this.hasColor = false;
            this.hasTexture = false;
            this.hasBrightness = false;
            this.isColorDisabled = false;
        }
    }

    // JAVADOC METHOD $$ func_78385_a
    public void setTextureUV(double par1, double par3)
    {
        this.hasTexture = true;
        this.textureU = par1;
        this.textureV = par3;
    }

    public void setBrightness(int par1)
    {
        this.hasBrightness = true;
        this.brightness = par1;
    }

    // JAVADOC METHOD $$ func_78386_a
    public void setColorOpaque_F(float par1, float par2, float par3)
    {
        this.setColorOpaque((int)(par1 * 255.0F), (int)(par2 * 255.0F), (int)(par3 * 255.0F));
    }

    // JAVADOC METHOD $$ func_78369_a
    public void setColorRGBA_F(float par1, float par2, float par3, float par4)
    {
        this.setColorRGBA((int)(par1 * 255.0F), (int)(par2 * 255.0F), (int)(par3 * 255.0F), (int)(par4 * 255.0F));
    }

    // JAVADOC METHOD $$ func_78376_a
    public void setColorOpaque(int par1, int par2, int par3)
    {
        this.setColorRGBA(par1, par2, par3, 255);
    }

    // JAVADOC METHOD $$ func_78370_a
    public void setColorRGBA(int par1, int par2, int par3, int par4)
    {
        if (!this.isColorDisabled)
        {
            if (par1 > 255)
            {
                par1 = 255;
            }

            if (par2 > 255)
            {
                par2 = 255;
            }

            if (par3 > 255)
            {
                par3 = 255;
            }

            if (par4 > 255)
            {
                par4 = 255;
            }

            if (par1 < 0)
            {
                par1 = 0;
            }

            if (par2 < 0)
            {
                par2 = 0;
            }

            if (par3 < 0)
            {
                par3 = 0;
            }

            if (par4 < 0)
            {
                par4 = 0;
            }

            this.hasColor = true;

            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
            {
                this.color = par4 << 24 | par3 << 16 | par2 << 8 | par1;
            }
            else
            {
                this.color = par1 << 24 | par2 << 16 | par3 << 8 | par4;
            }
        }
    }

    // JAVADOC METHOD $$ func_78374_a
    public void addVertexWithUV(double par1, double par3, double par5, double par7, double par9)
    {
        this.setTextureUV(par7, par9);
        this.addVertex(par1, par3, par5);
    }

    // JAVADOC METHOD $$ func_78377_a
    public void addVertex(double par1, double par3, double par5)
    {
        if (field_147569_p >= rawBufferSize - 32) 
        {
            if (rawBufferSize == 0)
            {
                rawBufferSize = 0x10000;
                rawBuffer = new int[rawBufferSize];
            }
            else
            {
                rawBufferSize *= 2;
                rawBuffer = Arrays.copyOf(rawBuffer, rawBufferSize);
            }
        }
        ++this.addedVertices;

        if (this.hasTexture)
        {
            this.rawBuffer[this.field_147569_p + 3] = Float.floatToRawIntBits((float)this.textureU);
            this.rawBuffer[this.field_147569_p + 4] = Float.floatToRawIntBits((float)this.textureV);
        }

        if (this.hasBrightness)
        {
            this.rawBuffer[this.field_147569_p + 7] = this.brightness;
        }

        if (this.hasColor)
        {
            this.rawBuffer[this.field_147569_p + 5] = this.color;
        }

        if (this.hasNormals)
        {
            this.rawBuffer[this.field_147569_p + 6] = this.normal;
        }

        this.rawBuffer[this.field_147569_p + 0] = Float.floatToRawIntBits((float)(par1 + this.xOffset));
        this.rawBuffer[this.field_147569_p + 1] = Float.floatToRawIntBits((float)(par3 + this.yOffset));
        this.rawBuffer[this.field_147569_p + 2] = Float.floatToRawIntBits((float)(par5 + this.zOffset));
        this.field_147569_p += 8;
        ++this.vertexCount;
    }

    // JAVADOC METHOD $$ func_78378_d
    public void setColorOpaque_I(int par1)
    {
        int j = par1 >> 16 & 255;
        int k = par1 >> 8 & 255;
        int l = par1 & 255;
        this.setColorOpaque(j, k, l);
    }

    // JAVADOC METHOD $$ func_78384_a
    public void setColorRGBA_I(int par1, int par2)
    {
        int k = par1 >> 16 & 255;
        int l = par1 >> 8 & 255;
        int i1 = par1 & 255;
        this.setColorRGBA(k, l, i1, par2);
    }

    // JAVADOC METHOD $$ func_78383_c
    public void disableColor()
    {
        this.isColorDisabled = true;
    }

    // JAVADOC METHOD $$ func_78375_b
    public void setNormal(float par1, float par2, float par3)
    {
        this.hasNormals = true;
        byte b0 = (byte)((int)(par1 * 127.0F));
        byte b1 = (byte)((int)(par2 * 127.0F));
        byte b2 = (byte)((int)(par3 * 127.0F));
        this.normal = b0 & 255 | (b1 & 255) << 8 | (b2 & 255) << 16;
    }

    // JAVADOC METHOD $$ func_78373_b
    public void setTranslation(double par1, double par3, double par5)
    {
        this.xOffset = par1;
        this.yOffset = par3;
        this.zOffset = par5;
    }

    // JAVADOC METHOD $$ func_78372_c
    public void addTranslation(float par1, float par2, float par3)
    {
        this.xOffset += (double)par1;
        this.yOffset += (double)par2;
        this.zOffset += (double)par3;
    }
}