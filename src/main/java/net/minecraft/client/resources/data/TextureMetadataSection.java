package net.minecraft.client.resources.data;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
public class TextureMetadataSection implements IMetadataSection
{
    private final boolean textureBlur;
    private final boolean textureClamp;
    private final List field_148536_c;
    private static final String __OBFID = "CL_00001114";

    public TextureMetadataSection(boolean p_i45102_1_, boolean p_i45102_2_, List p_i45102_3_)
    {
        this.textureBlur = p_i45102_1_;
        this.textureClamp = p_i45102_2_;
        this.field_148536_c = p_i45102_3_;
    }

    public boolean getTextureBlur()
    {
        return this.textureBlur;
    }

    public boolean getTextureClamp()
    {
        return this.textureClamp;
    }

    public List func_148535_c()
    {
        return Collections.unmodifiableList(this.field_148536_c);
    }
}