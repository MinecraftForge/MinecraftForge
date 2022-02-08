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
        tag(Tags.EntityTypes.ANIMALS).addTags(Tags.EntityTypes.ANIMALS_AMPHIBIANS, Tags.EntityTypes.ANIMALS_AQUATIC_MAMMALS, Tags.EntityTypes.ANIMALS_FISH);
        tag(Tags.EntityTypes.ANIMALS_AMPHIBIANS).add(EntityType.AXOLOTL); // TODO Add frogs and tadpoles when they come to Minecraft Java
        tag(Tags.EntityTypes.ANIMALS_ARTHROPODS);
        tag(Tags.EntityTypes.ANIMALS_AQUATIC_MAMMALS).add(EntityType.DOLPHIN);
        tag(Tags.EntityTypes.ANIMALS_AVIANS).addTags(Tags.EntityTypes.ANIMALS_AVIANS_FLYING, Tags.EntityTypes.ANIMALS_AVIANS_FOWLS);
        tag(Tags.EntityTypes.ANIMALS_AVIANS_FLYING).add(EntityType.PARROT);
        tag(Tags.EntityTypes.ANIMALS_AVIANS_FOWLS).addTag(Tags.EntityTypes.ANIMALS_AVIANS_FOWLS_LAND);
        tag(Tags.EntityTypes.ANIMALS_AVIANS_FOWLS_LAND).add(EntityType.CHICKEN);
//        tag(Tags.EntityTypes.ANIMALS_AVIANS_GROUNDED).add(EntityType.OSTRICH); // Uncomment when the ostrich is added to vanilla Minecraft Java
        tag(Tags.EntityTypes.ANIMALS_BOVINES).add(EntityType.COW, EntityType.MOOSHROOM, EntityType.SHEEP);
        tag(Tags.EntityTypes.ANIMALS_CAMELIDS).add(EntityType.LLAMA, EntityType.TRADER_LLAMA);
        tag(Tags.EntityTypes.ANIMALS_CANIDS).add(EntityType.FOX, EntityType.WOLF);
        tag(Tags.EntityTypes.ANIMALS_CAPRINES).add(EntityType.GOAT);
        tag(Tags.EntityTypes.ANIMALS_CEPHALOPODS).add(EntityType.GLOW_SQUID, EntityType.SQUID);
        tag(Tags.EntityTypes.ANIMALS_EQUINES).add(EntityType.DONKEY, EntityType.HORSE, EntityType.MULE);
        tag(Tags.EntityTypes.ANIMALS_FELIDS).addTag(Tags.EntityTypes.ANIMALS_FELIDS_SMALL);
        tag(Tags.EntityTypes.ANIMALS_FELIDS_SMALL).add(EntityType.CAT, EntityType.OCELOT);
        tag(Tags.EntityTypes.ANIMALS_FISH).add(EntityType.COD, EntityType.PUFFERFISH, EntityType.SALMON, EntityType.TROPICAL_FISH);
        tag(Tags.EntityTypes.ANIMALS_REPTILES).add(EntityType.TURTLE);
        tag(Tags.EntityTypes.ANIMALS_SWINES).add(EntityType.HOGLIN, EntityType.PIG);
        tag(Tags.EntityTypes.ANIMALS_URSIDS).add(EntityType.PANDA, EntityType.POLAR_BEAR);

        tag(Tags.EntityTypes.BOSSES).add(EntityType.ENDER_DRAGON, EntityType.WITHER);
        tag(Tags.EntityTypes.CREEPERS).add(EntityType.CREEPER);
        tag(Tags.EntityTypes.DRAGONS).add(EntityType.ENDER_DRAGON);

        tag(Tags.EntityTypes.ELEMENTALS).addTags(Tags.EntityTypes.ELEMENTALS_FIRE, Tags.EntityTypes.ELEMENTALS_ICE);
        tag(Tags.EntityTypes.ELEMENTALS_FIRE).add(EntityType.BLAZE);
        tag(Tags.EntityTypes.ELEMENTALS_ICE).add(EntityType.SNOW_GOLEM);
    }

    @Override
    public String getName()
    {
        return "Forge EntityType Tags";
    }
}
