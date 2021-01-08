/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.debug.item;

import java.util.Optional;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod("item_spawner_test")
public class ItemSpawnerTest
{
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "item_spawner_test");
    private static final RegistryObject<Item> ITEM_SPAWNER_TEST = ITEMS.register("item_spawner_test", () -> new Item(new Item.Properties().group(ItemGroup.MISC))
            {
                @Override
                public Optional<MobEntity> getMobToSpawn(ItemStack stack, PlayerEntity player, MobEntity mob)
                {
                    MobEntity child = (MobEntity) mob.getType().create(mob.world);
                    if(child == null) return Optional.empty();
                    else
                    {
                        child.setChild(true);
                        if(!child.isChild()) return Optional.empty();
                        else
                        {
                            child.setLocationAndAngles(mob.getPosX(), mob.getPosY(), mob.getPosZ(), 0f, 0f);
                            ((ServerWorld) mob.world).func_242417_l(child);
                            return Optional.of(child);
                        }
                    }
                }
            }
    );

    public ItemSpawnerTest() { ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus()); }
}
