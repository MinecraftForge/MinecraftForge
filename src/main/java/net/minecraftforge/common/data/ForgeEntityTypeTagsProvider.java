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
import net.minecraft.tags.EntityTypeTags;
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
        tag(Tags.EntityTypes.ANIMALS).addTags(Tags.EntityTypes.ANIMALS_AMPHIBIANS, Tags.EntityTypes.ANIMALS_ARTHROPODS, Tags.EntityTypes.ANIMALS_AQUATIC, Tags.EntityTypes.ANIMALS_AVIANS, Tags.EntityTypes.ANIMALS_BOVINES, Tags.EntityTypes.ANIMALS_CAMELIDS, Tags.EntityTypes.ANIMALS_CANIDS, Tags.EntityTypes.ANIMALS_CAPRINES, Tags.EntityTypes.ANIMALS_CEPHALOPODS, Tags.EntityTypes.ANIMALS_EQUINES, Tags.EntityTypes.ANIMALS_FELIDS, Tags.EntityTypes.ANIMALS_REPTILES, Tags.EntityTypes.ANIMALS_SWINES, Tags.EntityTypes.ANIMALS_URSIDS);
        tag(Tags.EntityTypes.ANIMALS_AMPHIBIANS).add(EntityType.AXOLOTL); // TODO Add frog and tadpole tags when they come to Minecraft Java
        tag(Tags.EntityTypes.ANIMALS_ARTHROPODS).add(EntityType.BEE, EntityType.CAVE_SPIDER, EntityType.ENDERMITE, EntityType.SILVERFISH, EntityType.SPIDER);

        tag(Tags.EntityTypes.ANIMALS_AQUATIC).addTags(Tags.EntityTypes.ANIMALS_AQUATIC_FISH, Tags.EntityTypes.ANIMALS_AQUATIC_MAMMALS).add(EntityType.GLOW_SQUID, EntityType.SQUID); // TODO Add tadpole tag when it comes to Minecraft Java
        tag(Tags.EntityTypes.ANIMALS_AQUATIC_FISH).add(EntityType.COD, EntityType.PUFFERFISH, EntityType.SALMON, EntityType.TROPICAL_FISH);
        tag(Tags.EntityTypes.ANIMALS_AQUATIC_MAMMALS).add(EntityType.DOLPHIN);

        tag(Tags.EntityTypes.ANIMALS_AVIANS).addTags(Tags.EntityTypes.ANIMALS_AVIANS_FLYING, Tags.EntityTypes.ANIMALS_AVIANS_FOWLS);
        tag(Tags.EntityTypes.ANIMALS_AVIANS_FLYING).add(EntityType.PARROT);
        tag(Tags.EntityTypes.ANIMALS_AVIANS_FOWLS).addTag(Tags.EntityTypes.ANIMALS_AVIANS_FOWLS_LAND);
        tag(Tags.EntityTypes.ANIMALS_AVIANS_FOWLS_LAND).add(EntityType.CHICKEN);
//        tag(Tags.EntityTypes.ANIMALS_AVIANS_GROUNDED).add(EntityType.OSTRICH); // Uncomment when the ostrich is added to vanilla Minecraft Java

        tag(Tags.EntityTypes.ANIMALS_BOVINES).addTag(Tags.EntityTypes.ANIMALS_BOVINES_CATTLE).add(EntityType.SHEEP);
        tag(Tags.EntityTypes.ANIMALS_BOVINES_CATTLE).add(EntityType.COW, EntityType.MOOSHROOM);
        tag(Tags.EntityTypes.ANIMALS_CAMELIDS).add(EntityType.LLAMA, EntityType.TRADER_LLAMA);
        tag(Tags.EntityTypes.ANIMALS_CANIDS).add(EntityType.FOX, EntityType.WOLF);
        tag(Tags.EntityTypes.ANIMALS_CAPRINES).add(EntityType.GOAT);
        tag(Tags.EntityTypes.ANIMALS_CEPHALOPODS).add(EntityType.GLOW_SQUID, EntityType.SQUID);
        tag(Tags.EntityTypes.ANIMALS_EQUINES).add(EntityType.DONKEY, EntityType.HORSE, EntityType.MULE);
        tag(Tags.EntityTypes.ANIMALS_FELIDS).addTag(Tags.EntityTypes.ANIMALS_FELIDS_SMALL);
        tag(Tags.EntityTypes.ANIMALS_FELIDS_SMALL).add(EntityType.CAT, EntityType.OCELOT);
        tag(Tags.EntityTypes.ANIMALS_REPTILES).add(EntityType.TURTLE);
        tag(Tags.EntityTypes.ANIMALS_SWINES).add(EntityType.HOGLIN, EntityType.PIG);
        tag(Tags.EntityTypes.ANIMALS_URSIDS).add(EntityType.PANDA, EntityType.POLAR_BEAR);

        tag(Tags.EntityTypes.DRAGONS).add(EntityType.ENDER_DRAGON);

        tag(Tags.EntityTypes.ELEMENTALS).addTags(Tags.EntityTypes.ELEMENTALS_FIRE, Tags.EntityTypes.ELEMENTALS_ICE);
        tag(Tags.EntityTypes.ELEMENTALS_FIRE).add(EntityType.BLAZE);
        tag(Tags.EntityTypes.ELEMENTALS_ICE).add(EntityType.SNOW_GOLEM);

        tag(Tags.EntityTypes.ENEMIES).addTags(Tags.EntityTypes.ENEMIES_ANIMALS, Tags.EntityTypes.ENEMIES_BOSSES, Tags.EntityTypes.ENEMIES_CREEPERS, EntityTypeTags.RAIDERS, Tags.EntityTypes.ENEMIES_UNDEAD).add(EntityType.BLAZE, EntityType.ELDER_GUARDIAN, EntityType.ENDERMAN, EntityType.GHAST, EntityType.GUARDIAN, EntityType.MAGMA_CUBE, EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.SHULKER, EntityType.SLIME, EntityType.VEX);
        tag(Tags.EntityTypes.ENEMIES_ANIMALS).add(EntityType.CAVE_SPIDER, EntityType.ENDERMITE, EntityType.HOGLIN, EntityType.SPIDER);
        tag(Tags.EntityTypes.ENEMIES_BOSSES).add(EntityType.ENDER_DRAGON, EntityType.WITHER);
        tag(Tags.EntityTypes.ENEMIES_CREEPERS).add(EntityType.CREEPER);
        tag(Tags.EntityTypes.ENEMIES_UNDEAD).addTags(Tags.EntityTypes.ENEMIES_UNDEAD_SKELETONS, Tags.EntityTypes.ENEMIES_UNDEAD_ZOMBIES).add(EntityType.PHANTOM, EntityType.WITHER);
        tag(Tags.EntityTypes.ENEMIES_UNDEAD_SKELETONS).addTag(EntityTypeTags.SKELETONS);
        tag(Tags.EntityTypes.ENEMIES_UNDEAD_ZOMBIES).add(EntityType.DROWNED, EntityType.HUSK, EntityType.ZOGLIN, EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIFIED_PIGLIN);
        
        tag(Tags.EntityTypes.NPC).add(EntityType.PIGLIN, EntityType.VILLAGER, EntityType.WANDERING_TRADER);
    }

    @Override
    public String getName()
    {
        return "Forge EntityType Tags";
    }
}
