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
        tag(Tags.EntityTypes.AMPHIBIANS).add(EntityType.AXOLOTL); // TODO Add frog and tadpole tags when they come to Minecraft Java
        tag(Tags.EntityTypes.AQUATIC).add(EntityType.AXOLOTL, EntityType.COD, EntityType.DOLPHIN, EntityType.ELDER_GUARDIAN, EntityType.GLOW_SQUID, EntityType.GUARDIAN, EntityType.PUFFERFISH, EntityType.SALMON, EntityType.SQUID, EntityType.TROPICAL_FISH, EntityType.TURTLE); // TODO Add frog and tadpole tags when they come to Minecraft Java
        tag(Tags.EntityTypes.ARTHROPODS).add(EntityType.BEE, EntityType.CAVE_SPIDER, EntityType.ENDERMITE, EntityType.SILVERFISH, EntityType.SPIDER);
        tag(Tags.EntityTypes.AVIANS).addTag(Tags.EntityTypes.AVIANS_FOWLS).add(EntityType.PARROT);
        tag(Tags.EntityTypes.AVIANS_FOWLS).add(EntityType.CHICKEN);
        tag(Tags.EntityTypes.BOSSES).add(EntityType.ENDER_DRAGON, EntityType.WITHER);
        tag(Tags.EntityTypes.CEPHALOPODS).add(EntityType.GLOW_SQUID, EntityType.SQUID);
        tag(Tags.EntityTypes.CREEPERS).add(EntityType.CREEPER);
        tag(Tags.EntityTypes.DRAGONS).add(EntityType.ENDER_DRAGON);

        tag(Tags.EntityTypes.ELEMENTALS).addTags(Tags.EntityTypes.ELEMENTALS_FIRE, Tags.EntityTypes.ELEMENTALS_ICE);
        tag(Tags.EntityTypes.ELEMENTALS_FIRE).add(EntityType.BLAZE);
        tag(Tags.EntityTypes.ELEMENTALS_ICE).add(EntityType.SNOW_GOLEM);
        tag(Tags.EntityTypes.ENEMIES).addTags(Tags.EntityTypes.BOSSES, Tags.EntityTypes.CREEPERS, EntityTypeTags.RAIDERS).add(EntityType.BLAZE, EntityType.CAVE_SPIDER, EntityType.DROWNED, EntityType.ELDER_GUARDIAN, EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.GHAST, EntityType.GUARDIAN, EntityType.HOGLIN, EntityType.HUSK, EntityType.MAGMA_CUBE, EntityType.PHANTOM, EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.SHULKER, EntityType.SILVERFISH, EntityType.SKELETON, EntityType.SLIME, EntityType.SPIDER, EntityType.STRAY, EntityType.VEX, EntityType.WITHER_SKELETON, EntityType.ZOGLIN, EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIFIED_PIGLIN);
        tag(Tags.EntityTypes.FISH).add(EntityType.COD, EntityType.PUFFERFISH, EntityType.SALMON, EntityType.TROPICAL_FISH);
        tag(Tags.EntityTypes.FLYING).add(EntityType.BAT, EntityType.BEE, EntityType.ENDER_DRAGON, EntityType.GHAST, EntityType.PARROT, EntityType.PHANTOM, EntityType.VEX, EntityType.WITHER);
        tag(Tags.EntityTypes.LAND).add(EntityType.BLAZE, EntityType.CAT, EntityType.CAVE_SPIDER, EntityType.CHICKEN, EntityType.CREEPER, EntityType.DONKEY, EntityType.DROWNED, EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.EVOKER, EntityType.FOX, EntityType.GOAT, EntityType.HOGLIN, EntityType.HORSE, EntityType.HUSK, EntityType.ILLUSIONER, EntityType.IRON_GOLEM, EntityType.LLAMA, EntityType.MAGMA_CUBE, EntityType.MULE, EntityType.OCELOT, EntityType.PANDA, EntityType.PIG, EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.PILLAGER, EntityType.PLAYER, EntityType.POLAR_BEAR, EntityType.RABBIT, EntityType.RAVAGER, EntityType.SHEEP, EntityType.SHULKER, EntityType.SILVERFISH, EntityType.SKELETON, EntityType.SKELETON_HORSE, EntityType.SLIME, EntityType.SNOW_GOLEM, EntityType.SPIDER, EntityType.STRAY, EntityType.TRADER_LLAMA, EntityType.VILLAGER, EntityType.VINDICATOR, EntityType.WANDERING_TRADER, EntityType.WITCH, EntityType.WITHER_SKELETON, EntityType.WOLF, EntityType.ZOGLIN, EntityType.ZOMBIE, EntityType.ZOMBIE_HORSE, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIFIED_PIGLIN);

        tag(Tags.EntityTypes.MAMMALS).addTags(Tags.EntityTypes.MAMMALS_BOVINES, Tags.EntityTypes.MAMMALS_CAMELIDS, Tags.EntityTypes.MAMMALS_CANIDS, Tags.EntityTypes.MAMMALS_CAPRINES, Tags.EntityTypes.MAMMALS_EQUINES, Tags.EntityTypes.MAMMALS_FELIDS, Tags.EntityTypes.MAMMALS_SWINES, Tags.EntityTypes.MAMMALS_URSIDS).add(EntityType.BAT, EntityType.RABBIT);
        tag(Tags.EntityTypes.MAMMALS_BOVINES).add(EntityType.COW, EntityType.MOOSHROOM, EntityType.SHEEP);
        tag(Tags.EntityTypes.MAMMALS_CAMELIDS).add(EntityType.LLAMA, EntityType.TRADER_LLAMA);
        tag(Tags.EntityTypes.MAMMALS_CANIDS).add(EntityType.FOX, EntityType.WOLF);
        tag(Tags.EntityTypes.MAMMALS_CAPRINES).add(EntityType.GOAT);
        tag(Tags.EntityTypes.MAMMALS_EQUINES).add(EntityType.DONKEY, EntityType.HORSE, EntityType.MULE);
        tag(Tags.EntityTypes.MAMMALS_FELIDS).add(EntityType.CAT, EntityType.OCELOT);
        tag(Tags.EntityTypes.MAMMALS_SWINES).add(EntityType.HOGLIN, EntityType.PIG);
        tag(Tags.EntityTypes.MAMMALS_URSIDS).add(EntityType.PANDA, EntityType.POLAR_BEAR);

        tag(Tags.EntityTypes.NPC).add(EntityType.PIGLIN, EntityType.VILLAGER, EntityType.WANDERING_TRADER);
        tag(Tags.EntityTypes.REPTILES).add(EntityType.TURTLE);
        tag(Tags.EntityTypes.UNDEAD).addTags(Tags.EntityTypes.UNDEAD_SKELETONS, Tags.EntityTypes.UNDEAD_ZOMBIES).add(EntityType.PHANTOM, EntityType.WITHER);
        tag(Tags.EntityTypes.UNDEAD_SKELETONS).addTag(EntityTypeTags.SKELETONS).add(EntityType.SKELETON_HORSE);
        tag(Tags.EntityTypes.UNDEAD_ZOMBIES).add(EntityType.DROWNED, EntityType.HUSK, EntityType.ZOGLIN, EntityType.ZOMBIE, EntityType.ZOMBIE_HORSE, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIFIED_PIGLIN);
        tag(Tags.EntityTypes.VOLCANIC).add(EntityType.STRIDER);
    }

    @Override
    public String getName()
    {
        return "Forge EntityType Tags";
    }
}
