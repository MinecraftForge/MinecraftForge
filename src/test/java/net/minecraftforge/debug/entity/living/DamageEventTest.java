/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.debug.entity.living;

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = DamageEventTest.MODID, name = "ForgeDebugLivingDamage", version = "1.0", acceptableRemoteVersions = "*")
public class DamageEventTest
{

    private static Logger logger;

    public static final String MODID = "forgedebuglivingdamage";

    private static final Map<String, DamageSource> DAMAGE_SOURCES = ImmutableMap.<String, DamageSource> builder()
            .put("fire", DamageSource.IN_FIRE)
            .put("lightning_bolt", DamageSource.LIGHTNING_BOLT)
            .put("on_fire", DamageSource.ON_FIRE)
            .put("lava", DamageSource.LAVA)
            .put("hot_floor", DamageSource.HOT_FLOOR)
            .put("in_wall", DamageSource.IN_WALL)
            .put("cramming", DamageSource.CRAMMING)
            .put("drown", DamageSource.DROWN)
            .put("starve", DamageSource.STARVE)
            .put("cactus", DamageSource.CACTUS)
            .put("fall", DamageSource.FALL)
            .put("fly_into_wall", DamageSource.FLY_INTO_WALL)
            .put("out_of_world", DamageSource.OUT_OF_WORLD)
            .put("generic", DamageSource.GENERIC)
            .put("magic", DamageSource.MAGIC)
            .put("wither", DamageSource.WITHER)
            .put("anvil", DamageSource.ANVIL)
            .put("falling_block", DamageSource.FALLING_BLOCK)
            .put("dragon_breath", DamageSource.DRAGON_BREATH)
            .put("fireworks", DamageSource.FIREWORKS)
            .build();

    private static class CommandDamage extends CommandBase
    {

        private static final String USAGE = "damage <selector> <source> <amount>";

        @Override
        public String getName()
        {
            return "damage";
        }

        @Override
        public String getUsage(ICommandSender sender)
        {
            return USAGE;
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
        {
            if (args.length < 3)
            {
                throw new WrongUsageException(USAGE);
            }

            EntityLivingBase target = getEntity(server, sender, args[0], EntityLivingBase.class);
            DamageSource damageSource = DAMAGE_SOURCES.get(args[1].toLowerCase(Locale.ROOT));
            if (target == null || damageSource == null)
            {
                throw new WrongUsageException(USAGE);
            }

            float amount;
            try
            {
                amount = Float.parseFloat(args[2]);
            } catch (NumberFormatException e)
            {
                throw new WrongUsageException(USAGE);
            }

            target.attackEntityFrom(damageSource, amount);
        }

        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
        {
            if (args.length == 1)
            {
                return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
            } else if (args.length == 2)
            {
                return getListOfStringsMatchingLastWord(args, DAMAGE_SOURCES.keySet());
            }

            return Collections.emptyList();
        }

        @Override
        public boolean isUsernameIndex(String[] args, int index)
        {
            return index == 0;
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    void serverStarting(FMLServerStartingEvent evt)
    {
        evt.registerServerCommand(new CommandDamage());
    }

    @EventBusSubscriber
    public static class TestEventHandler
    {

        @SubscribeEvent
        public static void livingHurtPre(LivingHurtEvent evt)
        {
            logger.info("Entity {} damage from {} (pre-reduction): {}", evt.getEntity(), evt.getSource().getDamageType(), evt.getAmount());
        }

        @SubscribeEvent
        public static void livingHurtPost(LivingDamageEvent evt)
        {
            logger.info("Entity {} damage from {} (post-reduction): {}", evt.getEntity(), evt.getSource().getDamageType(), evt.getAmount());
        }

    }
}
