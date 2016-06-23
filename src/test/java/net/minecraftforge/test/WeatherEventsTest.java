package net.minecraftforge.test;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WeatherEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid="weathereventstest", name="Weather Events Test", version="0.0.0")
public class WeatherEventsTest {
    public boolean canceled=false;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void init(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandToggleWeather(this));
        System.out.println("Use /toggleWeatherCycle to toggle the natural weather cycle.");
    }

    public void toggleWeather(World w) {
        canceled=!canceled;
        WorldInfo info=w.getWorldInfo();
        System.out.println("Weather cycle is now "+(canceled?"deactivated":"activated")+".");
        System.out.println("CleanWeatherTime:"+info.getCleanWeatherTime());
        System.out.println("RainTime:"+info.getRainTime());
        System.out.println("ThunderTime:"+info.getThunderTime());
        System.out.println("Raining:"+info.isRaining());
        System.out.println("Thundering:"+info.isThundering());
    }

    @SubscribeEvent
    public void onRainingStateChange(WeatherEvent.RainingState event) {
        event.setCanceled(canceled);
    }

    @SubscribeEvent
    public void onThunderingStateChange(WeatherEvent.ThunderingState event) {
        event.setCanceled(canceled);
    }

    @SubscribeEvent
    public void onRainTimeChange(WeatherEvent.RainTime event) {
        event.setCanceled(canceled);
    }

    @SubscribeEvent
    public void onThunderTimeChange(WeatherEvent.ThunderTime event) {
        event.setCanceled(canceled);
    }

    @SubscribeEvent
    public void onCleanWeatherTimeChange(WeatherEvent.CleanWeatherTime event) {
        event.setCanceled(canceled);
    }
    
    public class CommandToggleWeather implements ICommand{
        
        public WeatherEventsTest mod;

        public CommandToggleWeather(WeatherEventsTest m){
            mod = m;
        }
        
        @Override
        public int compareTo(ICommand arg0) {
            return this.getCommandName().compareTo(arg0.getCommandName());
        }

        @Override
        public String getCommandName() {
            return "toggleWeatherCycle";
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "/toggleWeatherCycle";
        }

        @Override
        public List<String> getCommandAliases() {
            ArrayList<String> list=new ArrayList<String>();
            list.add("twc");
            return list;
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            mod.toggleWeather(sender.getEntityWorld());
        }

        @Override
        public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
            return true;
        }

        @Override
        public boolean isUsernameIndex(String[] args, int index) {
            return false;
        }

		@Override
		public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args,
				BlockPos pos) {
            return new ArrayList<String>();
		}
        
    }
}
