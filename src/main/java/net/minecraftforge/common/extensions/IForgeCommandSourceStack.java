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

package net.minecraftforge.common.extensions;

import net.minecraft.advancements.Advancement;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Scoreboard;

public interface IForgeCommandSourceStack
{

    private CommandSourceStack self()
    {
        return (CommandSourceStack) this;
    }

    default Scoreboard getScoreboard()
    {
        return self().getServer().getScoreboard();
    }

    default Advancement getAdvancement(ResourceLocation id)
    {
        return self().getServer().getAdvancements().getAdvancement(id);
    }

    default RecipeManager getRecipeManager()
    {
        return self().getServer().getRecipeManager();
    }

    default Level getUnsidedLevel()
    {
        return self().getLevel();
    }

}
