package cpw.mods.fml.common.event;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;


/**
 * Simple intermod communications to receive simple messages directed at you from
 * other mods
 *
 * @author cpw
 *
 */
public class FMLInterModComms {
	private static ArrayListMultimap<String, IMCMessage> modMessages = ArrayListMultimap.create();
	/**
	 * Subscribe to this event to receive your messages (they are sent between {@link Init} and {@link PostInit})
	 *
	 * @author cpw
	 *
	 */
	public static class IMCEvent extends FMLEvent {
		@Override
		public void applyModContainer(ModContainer activeContainer) {
			currentList = ImmutableList.copyOf(modMessages.get(activeContainer.getModId()));
		}
		private ImmutableList<IMCMessage> currentList;

		public ImmutableList<IMCMessage> getMessages()
		{
			return currentList;
		}
	}

	/**
	 * You will receive an instance of this for each message sent
	 * @author cpw
	 *
	 */
	public static final class IMCMessage {
		/**
		 * This is the modid of the mod that sent you the message
		 */
		public final String sender;

		/**
		 * This field, and {@link #value} are both at the mod's discretion
		 */
		public final String key;
		/**
		 * This field, and {@link #key} are both at the mod's discretion
		 */
		public final String value;

		private IMCMessage(String sender, String key, String value)
		{
			this.key = key;
			this.value = value;
			this.sender = sender;
		}
		@Override
		public String toString() {
			return sender;
		}
	}

	public static boolean sendMessage(String modId, String key, String value)
	{
		if (Loader.instance().activeModContainer()==null)
		{
			return false;
		}
		modMessages.put(modId, new IMCMessage(Loader.instance().activeModContainer().getModId(), key, value));
		return Loader.isModLoaded(modId) && !Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION);
	}
}
