/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.client;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

public class ForgeRenderTypes extends RenderType
{
    private ForgeRenderTypes(String p_i225992_1_, VertexFormat p_i225992_2_, int p_i225992_3_, int p_i225992_4_, boolean p_i225992_5_, boolean p_i225992_6_, Runnable p_i225992_7_, Runnable p_i225992_8_)
    {
        super(p_i225992_1_, p_i225992_2_, p_i225992_3_, p_i225992_4_, p_i225992_5_, p_i225992_6_, p_i225992_7_, p_i225992_8_);
        throw new IllegalStateException("This class must not be instantiated");
    }

    public static RenderType unsortedTranslucent(ResourceLocation textureLocation, boolean outline) {
        RenderType.State rendertype$state = RenderType.State.func_228694_a_().func_228724_a_(new RenderState.TextureState(textureLocation, false, false))
                .func_228726_a_(field_228515_g_).func_228716_a_(field_228532_x_).func_228713_a_(field_228517_i_).func_228714_a_(field_228491_A_)
                .func_228719_a_(field_228528_t_).func_228722_a_(field_228530_v_).func_228728_a_(outline);
        return func_228633_a_("entity_unsorted_translucent", DefaultVertexFormats.field_227849_i_, 7, 256, true, false /* disable sorting*/, rendertype$state);
    }
    public static RenderType unsortedTranslucent(ResourceLocation p_230168_0_)
    {
        return unsortedTranslucent(p_230168_0_, true);
    }

    public static RenderType UNSORTED_TRANSLUCENT = unsortedTranslucent(AtlasTexture.LOCATION_BLOCKS_TEXTURE, true);
}
