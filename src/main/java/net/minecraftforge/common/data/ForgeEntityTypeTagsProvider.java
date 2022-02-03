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

package net.minecraftforge.common.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.Tags;

public class ForgeEntityTypeTagsProvider extends EntityTypeTagsProvider {

    public ForgeEntityTypeTagsProvider(DataGenerator generator, ExistingFileHelper existingFileHelper)
    {
        super(generator, "forge", existingFileHelper);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addTags()
    {
        tag(Tags.EntityTypes.BOSSES).add(EntityType.ENDER_DRAGON, EntityType.WITHER);
        tag(Tags.EntityTypes.CREEPERS).add(EntityType.CREEPER);
        tag(Tags.EntityTypes.DRACONIC_MOBS).add(EntityType.ENDER_DRAGON);

        /**
         * Many of these mob types do not appear in vanilla Minecraft, and are thus left empty here
         */
        tag(Tags.EntityTypes.ELEMENTALS).addTags(Tags.EntityTypes.ELEMENTALS_AIR, Tags.EntityTypes.ELEMENTALS_DARK, Tags.EntityTypes.ELEMENTALS_EARTH, Tags.EntityTypes.ELEMENTALS_ELECTRIC, Tags.EntityTypes.ELEMENTALS_FIRE, Tags.EntityTypes.ELEMENTALS_ICE, Tags.EntityTypes.ELEMENTALS_LIGHT, Tags.EntityTypes.ELEMENTALS_METAL, Tags.EntityTypes.ELEMENTALS_PLANT, Tags.EntityTypes.ELEMENTALS_WATER);
        tag(Tags.EntityTypes.ELEMENTALS_AIR);
        tag(Tags.EntityTypes.ELEMENTALS_DARK);
        tag(Tags.EntityTypes.ELEMENTALS_EARTH);
        tag(Tags.EntityTypes.ELEMENTALS_ELECTRIC);
        tag(Tags.EntityTypes.ELEMENTALS_FIRE).add(EntityType.BLAZE);
        tag(Tags.EntityTypes.ELEMENTALS_ICE).add(EntityType.SNOW_GOLEM);
        tag(Tags.EntityTypes.ELEMENTALS_LIGHT);
        tag(Tags.EntityTypes.ELEMENTALS_METAL).add(EntityType.IRON_GOLEM);
        tag(Tags.EntityTypes.ELEMENTALS_PLANT);
        tag(Tags.EntityTypes.ELEMENTALS_WATER);

        tag(Tags.EntityTypes.FISH).add(EntityType.COD, EntityType.PUFFERFISH, EntityType.SALMON, EntityType.TROPICAL_FISH);
        tag(Tags.EntityTypes.GHOSTS).add(EntityType.VEX);
    }

    @Override
    public String getName()
    {
        return "Forge EntityType Tags";
    }
}
