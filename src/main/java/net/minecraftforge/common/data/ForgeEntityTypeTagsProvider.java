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
        tag(Tags.EntityTypes.BEARS).add(EntityType.PANDA, EntityType.POLAR_BEAR);
        tag(Tags.EntityTypes.BOSSES).add(EntityType.ENDER_DRAGON, EntityType.WITHER);
        tag(Tags.EntityTypes.BOVINES).add(EntityType.COW, EntityType.MOOSHROOM);
        tag(Tags.EntityTypes.CAMELIDS).add(EntityType.LLAMA);
        tag(Tags.EntityTypes.CANINES).add(EntityType.FOX, EntityType.WOLF);
        tag(Tags.EntityTypes.CAPRINES).add(EntityType.GOAT);
        tag(Tags.EntityTypes.CREEPERS).add(EntityType.CREEPER);
        tag(Tags.EntityTypes.DRACONIC_MOBS).add(EntityType.ENDER_DRAGON);

        tag(Tags.EntityTypes.ELEMENTAL_MOBS).addTags(Tags.EntityTypes.ELEMENTAL_MOBS_FIRE, Tags.EntityTypes.ELEMENTAL_MOBS_ICE, Tags.EntityTypes.ELEMENTAL_MOBS_METAL);
        tag(Tags.EntityTypes.ELEMENTAL_MOBS_FIRE).add(EntityType.BLAZE);
        tag(Tags.EntityTypes.ELEMENTAL_MOBS_ICE).add(EntityType.SNOW_GOLEM);
        tag(Tags.EntityTypes.ELEMENTAL_MOBS_METAL).add(EntityType.IRON_GOLEM);

        tag(Tags.EntityTypes.ELEMENTAL_ATTACKS).addTags(Tags.EntityTypes.ELEMENTAL_ATTACKS_ELECTRIC, Tags.EntityTypes.ELEMENTAL_ATTACKS_FIRE, Tags.EntityTypes.ELEMENTAL_ATTACKS_ICE);
        tag(Tags.EntityTypes.ELEMENTAL_ATTACKS_ELECTRIC).add(EntityType.LIGHTNING_BOLT);
        tag(Tags.EntityTypes.ELEMENTAL_ATTACKS_FIRE).add(EntityType.FIREBALL, EntityType.SMALL_FIREBALL);
        tag(Tags.EntityTypes.ELEMENTAL_ATTACKS_ICE).add(EntityType.SNOWBALL);

        tag(Tags.EntityTypes.EQUINES).add(EntityType.DONKEY, EntityType.HORSE, EntityType.MULE);
        tag(Tags.EntityTypes.FELINES).add(EntityType.CAT, EntityType.OCELOT);
        tag(Tags.EntityTypes.FISH).add(EntityType.COD, EntityType.PUFFERFISH, EntityType.SALMON, EntityType.TROPICAL_FISH);
        tag(Tags.EntityTypes.FOWLS).addTag(Tags.EntityTypes.FOWLS_LAND);
        tag(Tags.EntityTypes.FOWLS_LAND).add(EntityType.CHICKEN);
        tag(Tags.EntityTypes.GHOSTS).add(EntityType.VEX);
        tag(Tags.EntityTypes.SWINES).add(EntityType.HOGLIN, EntityType.PIG);
    }

    @Override
    public String getName()
    {
        return "Forge EntityType Tags";
    }
}
