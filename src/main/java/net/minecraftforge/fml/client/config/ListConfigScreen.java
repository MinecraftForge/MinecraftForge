/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.fml.client.config;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.entry.ConfigListEntry;
import net.minecraftforge.fml.client.config.entry.ListConfigListEntry;
import net.minecraftforge.fml.client.config.entry.widget.IConfigListEntryWidget;
import net.minecraftforge.fml.client.config.entry.widget.InfoText;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * A ConfigScreen that displays an editable visual representation of a {@link List}.
 *
 * @author Cadiboo
 */
public class ListConfigScreen<T extends List<?>> extends ConfigScreen {

	/**
	 * Used as a backup for getting an element to add if the current list is empty.
	 * Lists are mutable so care must be taken not to modify the original list so that
	 * resetToDefault and undoChanges work properly.
	 */
	protected final T original;
	private final IConfigListEntryWidget.Callback<T> callback;
	private final boolean isFixedSize;
	@Nullable
	protected Object addObj;

	public ListConfigScreen(final ConfigScreen owningScreen, final IConfigListEntryWidget.Callback<T> callback) {
		super(owningScreen.getTitle(), owningScreen);
		this.callback = callback;
		this.original = callback.get();
		this.callback.set(this.cloneList(original));
		this.isFixedSize = isFixedSize(callback.get());
		this.addObj = getInsertValue(0);
	}

	/**
	 * Counts expanding around a number.
	 * Examples (i counts up from 0 to maxInclusive + 1):
	 * (0, 20, 7, i) = [7, 8, 6, 9, 5, 10, 4, 11, 3, 12, 2, 13, 1, 14, 0, 15, 16, 17, 18, 19, 20]
	 * (0, 20, 15, i) = [15, 16, 14, 17, 13, 18, 12, 19, 11, 20, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0]
	 *
	 * @param minInclusive Minimum value, inclusive
	 * @param maxExclusive Maximum value, exclusive
	 * @see "https://discordapp.com/channels/176780432371744769/179315645005955072/664845850954039306"
	 * @see "https://gist.github.com/Cadiboo/df386a2688edd4ee893ae3f79139d02a"
	 */
	public static int getCountIndex(int minInclusive, int maxExclusive, int start, int index) {
		int minIndex = (start - minInclusive) * 2;
		int maxIndex = (maxExclusive - start) * 2 - 1;
		if (index > minIndex) {
			int r = start + (minIndex / 2) + (index - minIndex);
			if (r >= maxExclusive)
				throw new IndexOutOfBoundsException("" + index);
			return r;
		} else if (index >= maxIndex) {
			int r = start - (maxIndex / 2) - (index - maxIndex) - 1;
			if (r < minInclusive)
				throw new IndexOutOfBoundsException("" + index);
			return r;
		} else {
			return start + (index % 2 == 1 ? (index + 1) / 2 : -index / 2);
		}
	}

	/**
	 * Clones the list.
	 * Lists are mutable so care must be taken not to modify the original list so that
	 * resetToDefault and undoChanges work properly.
	 * Does NOT return the same object.
	 */
	public T cloneList(T listToClone) {
		if (listToClone instanceof ArrayList)
			return (T) ((ArrayList<?>) listToClone).clone();
			// From before ForgeConfigSpec was fixed to work with "ArrayList"s created by Arrays.asList().
			// Left commented out so it exists in case it needs to be used in the future.
//		else if (listToClone.getClass().getName().equals("java.util.Arrays$ArrayList"))
//			this.clone = (T) Arrays.asList(listToClone.toArray());
		else
			return (T) new ArrayList<>(listToClone);
	}

	/**
	 * Called from inside the constructor.
	 * Return true if the list is fixed size.
	 */
	protected boolean isFixedSize(@Nonnull final List<?> list) {
		return false;
	}

	public boolean isFixedSize() {
		return isFixedSize;
	}

	protected Object getInsertValue(final int startIndex) {
		if (isFixedSize())
			return addObj = null;

		addObj = tryGet(callback.get(), startIndex);

		if (addObj == null)
			addObj = tryGet(original, startIndex);

		if (addObj == null) {
			final T aDefault = callback.getDefault();
			if (!aDefault.isEmpty())
				addObj = tryGet(aDefault, startIndex);
		}

		return addObj;
	}

	/**
	 * Gets the first non null entry of the list that is closest to the start index
	 */
	@Nullable
	protected Object tryGet(final T list, int startIdx) {
		final int maxExclusive = list.size();
		for (int i = 0; i < maxExclusive; ++i) {
			final Object o = list.get(getCountIndex(0, maxExclusive, startIdx, i));
			if (o != null)
				return o;
		}
		return null;
	}

	@Override
	public void init() {
		super.init();
		this.postInit();
	}

	protected <W extends Widget & IConfigListEntryWidget<T>> void postInit() {
		final T list = callback.get();
		final ArrayList<W> widgets = makeWidgets(list, this, o -> callback.isValid());

		if (widgets.isEmpty()) {
			this.getEntryList().children().add(makeNoElementsListEntry());
			return;
		}

		final Object2IntMap<Object> indices = new Object2IntOpenHashMap<>();
		for (int index = 0; index < widgets.size(); ++index)
			indices.put(widgets.get(index), index);
		final List<ConfigListEntry<?>> configListEntries = this.getEntryList().children();
		widgets.forEach(widget -> configListEntries.add(makeEntry(indices, widget)));
	}

	@Nonnull
	protected <W extends Widget & IConfigListEntryWidget<T>> ListConfigListEntry<T> makeEntry(final Object2IntMap<Object> indices, final W widget) {
		final boolean isModifiable = !isFixedSize();
		// Raw use because generics be weird. IntelliJ compiles it without issue with <> but Forge's compiler throws a fit.
		return new ListConfigListEntry(ListConfigScreen.this, widget, true, isModifiable, isModifiable) {
			@Override
			public void removeEntry() {
				if (!canModify())
					return;
				try {
					callback.get().remove(indices.getInt(this.getWidget()));
				} catch (Exception e) {
					// Should never happen + No way to display this to the user. Don't know what to do here.
					e.printStackTrace();
				}
				// Don't need to worry about updating the indices, we refresh the screen
				shouldRefreshEntryList = true;
				final Minecraft minecraft = Minecraft.getInstance();
				final MainWindow mainWindow = minecraft.func_228018_at_();
				init(minecraft, mainWindow.getScaledWidth(), mainWindow.getScaledHeight());
			}

			@Override
			public void addEntryBelow() {
				if (!canModify())
					return;
				final int index = indices.getInt(getWidget());
				Object addObj = ListConfigScreen.this.getInsertValue(index);
				if (addObj == null)
					return;
				try {
					((List) ListConfigScreen.this.callback.get()).add(index + 1, addObj);
				} catch (Exception e) {
					// Should never happen + No way to display this to the user. Don't know what to do here.
					e.printStackTrace();
				}
				// Don't need to worry about updating the indices, we refresh the screen
				ListConfigScreen.this.shouldRefreshEntryList = true;
				final Minecraft minecraft = Minecraft.getInstance();
				final MainWindow mainWindow = minecraft.func_228018_at_();
				ListConfigScreen.this.init(minecraft, mainWindow.getScaledWidth(), mainWindow.getScaledHeight());
			}

			private boolean canModify() {
				if (ListConfigScreen.this.isFixedSize())
					return false;
				// DummyConfigElement (Unsupported Object) has null
				return ((IConfigListEntryWidget<T>) this.getWidget()).getCallback() != null;
			}
		};
	}

	@Nonnull
	protected ListConfigListEntry<T> makeNoElementsListEntry() {
		final boolean isModifiable = !isFixedSize();
		// Raw use because generics be weird. IntelliJ compiles it without issue with <> but Forge's compiler throws a fit.
		return new ListConfigListEntry(ListConfigScreen.this, new InfoText<>(I18n.format("fml.configgui.noElements")), false, isModifiable, false) {
			@Override
			public void removeEntry() {
				// No op
			}

			@Override
			public void addEntryBelow() {
				if (ListConfigScreen.this.isFixedSize())
					return;
				Object addObj = ListConfigScreen.this.getInsertValue(0);
				if (addObj == null)
					return;
				try {
					((List) ListConfigScreen.this.callback.get()).add(addObj);
				} catch (Exception e) {
					// Should never happen + No way to display this to the user. Don't know what to do here.
					e.printStackTrace();
				}
				// Don't need to worry about updating the indices, we refresh the screen
				ListConfigScreen.this.shouldRefreshEntryList = true;
				final Minecraft minecraft = Minecraft.getInstance();
				final MainWindow mainWindow = minecraft.func_228018_at_();
				ListConfigScreen.this.init(minecraft, mainWindow.getScaledWidth(), mainWindow.getScaledHeight());
			}
		};
	}

	protected <W extends Widget & IConfigListEntryWidget<T>> ArrayList<W> makeWidgets(final List<?> list, final ConfigScreen configScreen, final Predicate<Object> elementValidator) {
		final ArrayList<W> widgets = new ArrayList<>();
		for (int i = 0; i < list.size(); ++i)
			widgets.add(ConfigTypesManager.makeListWidget(list, i, configScreen, elementValidator, list.get(i)));
		ConfigTypesManager.sortWidgets(widgets);
		return widgets;
	}

}
