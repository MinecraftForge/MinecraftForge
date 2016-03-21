package net.minecraftforge.client.model.obj;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class OBJCustomData
{
	public enum Keys
	{
		//single keys
		FULL_ATLAS("useFullAtlas"),
		//uv keys
		NORMALIZE_UVS("normalizeUVs"),
		UNITIZE_UVS("unitizeUVs"),
		/*WRAP_UVS("wrapUVs"),
		CLAMP_UVS("clampUVs"),
		TILE_UVS("tileUVs"),*/
		FLIP_UVS("flipUVs"),
		@Deprecated
		FLIP_V("flip-v"),
		//group configuration keys
		GROUP_CONFIGURATIONS("groupConfigs"),
		SHOW("show"),
		HIDE("hide"),
		FLAGS("flags"),
		//group configuration flags
		ALWAYS_ACTIVE("alwaysActive");
		
		public final String key;
		
		Keys(String key)
		{
			this.key = key;
		}
		
		public static List<Keys> getCustomDataKeys()
		{
			return Lists.newArrayList(FULL_ATLAS, GROUP_CONFIGURATIONS);
		}
		
		public static List<Keys> getProcessUVKeys()
		{
			return Lists.newArrayList(NORMALIZE_UVS, UNITIZE_UVS, /*WRAP_UVS, CLAMP_UVS, TILE_UVS,*/ FLIP_UVS);
		}
		
		public static List<Keys> getGroupConfigurationKeys()
		{
			return Lists.newArrayList(SHOW, HIDE, FLAGS);
		}
		
		public static List<Keys> getGroupConfigurationFlags()
		{
			return Lists.newArrayList(ALWAYS_ACTIVE);
		}
	}
	
	protected static BiMap<String, Keys> keyNameMap = HashBiMap.create(Keys.values().length);
	protected EnumMap<Keys, Pair<Boolean, Boolean>> processUVData = Maps.<Keys, Pair<Boolean, Boolean>>newEnumMap(Keys.class);
	protected GroupConfigHandler groupConfigHandler = new GroupConfigHandler();
	protected ImmutableList<String> groupNames;
	protected boolean useFullAtlas = false;
	protected boolean hasProcessed = false;
	protected float[][] parsedUVBounds = new float[][] {{0.0f, 0.0f}, {1.0f, 1.0f}};
	
	public OBJCustomData(float[][] parsedUVBounds)
	{
		this.parsedUVBounds = parsedUVBounds != null ? parsedUVBounds : this.parsedUVBounds;
		initKeyNameMap();
	}
	
	public OBJCustomData(ImmutableMap<String, String> customData)
	{
		this.process(customData);
		initKeyNameMap();
	}
	
	public OBJCustomData duplicate()
	{
		OBJCustomData ret = new OBJCustomData(this.parsedUVBounds);
		ret.useFullAtlas = this.useFullAtlas;
		ret.processUVData = Maps.newEnumMap(this.processUVData);
		ret.groupConfigHandler = this.groupConfigHandler;
//		ret.groupNames = ImmutableList.copyOf(this.groupNames);
		ret.hasProcessed = this.hasProcessed;
		return ret;
	}
	
	private static void initKeyNameMap()
	{
		for (Keys k : Keys.values())
		{
			keyNameMap.put(k.key, k);
		}
	}
	
	public static ImmutableBiMap<String, Keys> getKeyNameMap()
	{
		return ImmutableBiMap.copyOf(keyNameMap);
	}
	
	public boolean hasUVsOutOfBounds()
	{
		return this.parsedUVBounds[0][0] < 0.0f || this.parsedUVBounds[0][1] < 0.0f || this.parsedUVBounds[1][0] > 1.0f || this.parsedUVBounds[1][1] > 1.0f;
	}
	
	public ImmutableMap<Keys, Pair<Boolean, Boolean>> getProcessUVData()
	{
		return ImmutableMap.copyOf(this.processUVData);
	}
	
	public void setGroupNameList(ImmutableList<String> groupNames)
	{
		this.groupConfigHandler.setGroupNameList(groupNames);
	}
	
	public GroupConfigHandler getConfigHandler()
	{
		return this.groupConfigHandler;
	}
	
	public GroupConfig getConfig(String configName)
	{
		return this.groupConfigHandler.getConfig(configName);
	}

    public OBJCustomData setUseFullAtlas(boolean useFullAtlas)
    {
        this.useFullAtlas = useFullAtlas;
        return this;
    }
	
	public void process(ImmutableMap<String, String> customData)
	{
		this.hasProcessed = true;
		JsonParser parser = new JsonParser();
		
		if (customData.containsKey(Keys.FULL_ATLAS.key))
		{
			this.useFullAtlas = parser.parse(customData.get(Keys.FULL_ATLAS.key)).getAsBoolean();
		}
		
		this.processUVKeys(parser, customData);
		
		if (customData.containsKey(Keys.GROUP_CONFIGURATIONS.key))
		{
			this.groupConfigHandler.process(parser.parse(customData.get(Keys.GROUP_CONFIGURATIONS.key)).getAsJsonObject());
		}
	}
	
	public void processUVKeys(JsonParser parser, ImmutableMap<String, String> customData)
	{
		for (Keys k : Keys.getProcessUVKeys())
		{
			if (customData.containsKey(k.key))
			{
				JsonPrimitive value = parser.parse(customData.get(k.key)).getAsJsonPrimitive();
				if (value.isBoolean()) this.processUVData.put(k, Pair.of(value.getAsBoolean(), value.getAsBoolean()));
				else if (value.isString())
				{
					if (value.getAsString().equals("u")) this.processUVData.put(k, Pair.of(true, false));
					else if (value.getAsString().equals("v")) this.processUVData.put(k, Pair.of(false, true));
				}
			}
		}
		
		if (customData.containsKey(Keys.FLIP_V.key))
		{
			if (this.processUVData.containsKey(Keys.FLIP_UVS))
			{
				Pair<Boolean, Boolean> uvFlags = this.processUVData.get(Keys.FLIP_UVS);
				this.processUVData.put(Keys.FLIP_UVS, Pair.of(uvFlags.getLeft(), uvFlags.getRight() || parser.parse(customData.get(Keys.FLIP_V.key)).getAsBoolean()));
			}
			else
			{
				this.processUVData.put(Keys.FLIP_UVS, Pair.of(false, parser.parse(customData.get(Keys.FLIP_V.key)).getAsBoolean()));
			}
		}
	}
	
	public boolean allProcessUVValuesFalse()
	{
		for (Map.Entry<Keys, Pair<Boolean, Boolean>> e : this.processUVData.entrySet())
		{
			if (e.getValue().getLeft() || e.getValue().getRight())
			{
				return false;
			}
		}
		return true;
	}
	
	public static class GroupConfigHandler
	{
		private Map<String, GroupConfig> configMap = Maps.newHashMap();
		private Map<String, GroupConfig> combinedConfigs = Maps.newHashMap();
		private ImmutableList<String> groupNames;
		
		private GroupConfigHandler() {}
		
		public GroupConfigHandler duplicate()
		{
			GroupConfigHandler ret = new GroupConfigHandler();
			ret.configMap = Maps.newHashMap(this.configMap);
			ret.combinedConfigs = Maps.newHashMap(this.combinedConfigs);
			ret.groupNames = ImmutableList.copyOf(this.groupNames);
			return ret;
		}
		
		public GroupConfigBuilder getConfigBuilder()
		{
			return new GroupConfigBuilder(this.groupNames);
		}
		
		protected void setGroupNameList(ImmutableList<String> groupNames)
		{
			this.groupNames = groupNames;
		}
		
		public ImmutableList<String> getGroupNameList()
		{
			return this.groupNames;
		}
		
		public GroupConfig getConfig(String configName)
		{
			return this.configMap.get(configName);
		}
		
		public void addConfig(GroupConfig config)
		{
			this.configMap.put(config.name, config);
		}
		
		public ImmutableMap<String, GroupConfig> getConfigMap()
		{
			return ImmutableMap.copyOf(this.configMap);
		}
		
		public GroupConfig getCombinedConfig(List<String> configsToMerge, boolean ignoreHidden)
		{
			if (configsToMerge == null || configsToMerge.isEmpty()) return null;
			if (configsToMerge.size() == 1) return this.configMap.get(configsToMerge.get(0));
			String combinedName = GroupConfigBuilder.getCombinedName(configsToMerge);
			if (this.combinedConfigs.containsKey(combinedName))
			{
				return this.combinedConfigs.get(combinedName);
			}
			else
			{
				GroupConfigBuilder builder = new GroupConfigBuilder(this.groupNames).startCombination(combinedName);
				GroupConfig combined = builder.combine(configsToMerge, this.configMap, ignoreHidden);
				this.combinedConfigs.put(combinedName, combined);
				return combined;
			}
		}
		
		public ImmutableMap<String, GroupConfig> getCombinedConfigMap()
		{
			return ImmutableMap.copyOf(this.combinedConfigs);
		}
		
		public void process(JsonObject jsonObject)
		{
			GroupConfigBuilder builder = new GroupConfigBuilder(this.groupNames);
			for (Map.Entry<String, JsonElement> e : jsonObject.entrySet())
			{
				builder.startNew(e.getKey());
				
				JsonObject configObj = e.getValue().getAsJsonObject();
				String[] show = new Gson().fromJson(configObj.getAsJsonArray(Keys.SHOW.key), String[].class);
				String[] hide = new Gson().fromJson(configObj.getAsJsonArray(Keys.HIDE.key), String[].class);
				String[] flags = new Gson().fromJson(configObj.getAsJsonArray(Keys.FLAGS.key), String[].class);
				
				if (hide == null)
				{
					builder.hideAll(ArrayUtils.nullToEmpty(show));
				}
				else if (show == null)
				{
					builder.showAll(hide);
				}
				else
				{
					ArrayUtils.removeElements(show, hide);
					builder.hideAll(show);
				}
				
				if (flags != null)
				{
					builder.alwaysActive = Arrays.asList(flags).contains(Keys.ALWAYS_ACTIVE.key);
				}
				
				this.configMap.put(builder.name, builder.build());
			}
		}
		
		@Override
		public String toString()
		{
			StringBuilder builder = new StringBuilder();
			for (Map.Entry<String, GroupConfig> e : this.configMap.entrySet())
			{
				builder.append(String.format("%n%s%n", e.getValue()));
			}
			return builder.toString();
		}
	}
	
	public static class GroupConfigBuilder
	{
		private String name;
		private Map<String, Boolean> visMap = Maps.newHashMap();
		private boolean alwaysActive = false;
		
		private GroupConfigBuilder(ImmutableList<String> groupNames)
		{
			this.initMap(groupNames);
		}
		
		private void initMap(ImmutableList<String> groupNames)
		{
			for (String s : groupNames)
			{
				this.visMap.put(s, true);
			}
		}
		
		private void resetMap()
		{
			for (Map.Entry<String, Boolean> e : this.visMap.entrySet())
			{
				e.setValue(true);
			}
		}
		
		public GroupConfigBuilder startNew(String configName)
		{
			this.resetMap();
			this.name = configName;
			this.alwaysActive = false;
			return this;
		}
		
		public GroupConfigBuilder startCombination(String combinedName)
		{
			return this.startNew(combinedName);
		}
		
		public GroupConfigBuilder startCombination(List<String> activeConfigs)
		{
			return this.startNew(getCombinedName(activeConfigs));
		}
		
		public GroupConfig combine(List<String> activeConfigs, Map<String, GroupConfig> configMap, boolean ignoreHidden)
		{
			if (this.name == null || !this.name.startsWith("Combined_")) this.startCombination(activeConfigs);
			for (int i = 0; i < activeConfigs.size(); i++)
			{
				String configName = activeConfigs.get(i);
				GroupConfig config = configMap.get(configName);
				
				String[] show = config.getShownGroupNames().toArray(new String[0]);
				String[] hide = config.getHiddenGroupNames().toArray(new String[0]);
				
				if (i == 0)
				{
					this.hideAll(show);
				}
				else
				{
					this.show(show).hide(ignoreHidden ? null : hide);
				}
				
				if (config.getAlwaysActive())
				{
					this.visMap.put(configName, true);
				}
			}
			return new GroupConfig(this.name, this.visMap);
		}
		
		public static String getCombinedName(List<String> activeConfigs)
		{
			StringBuilder builder = new StringBuilder("Combined_[");
			for (String s : activeConfigs)
			{
				builder.append(String.format("%s,", s));
			}
			builder.deleteCharAt(builder.lastIndexOf(","));
			builder.append("]");
			return builder.toString();
		}
		
		public GroupConfigBuilder show(String... names)
		{
			if (names == null || /*names.length == 0 ||*/ names[0] == null) return this;
			for (String name : names)
			{
				if (this.visMap.containsKey(name))
				{
					this.visMap.put(name, true);
				}
			}
			return this;
		}
		
		public GroupConfigBuilder showAll(String... except)
		{
			for (Map.Entry<String, Boolean> e : this.visMap.entrySet())
			{
				e.setValue(!Arrays.asList(except).contains(e.getKey()));
			}
			return this;
		}
		
		public GroupConfigBuilder hide(String... names)
		{
			if (names == null || names.length == 0 || names[0] == null) return this;
			for (String name : names)
			{
				if (this.visMap.containsKey(name))
				{
					this.visMap.put(name, false);
				}
			}
			return this;
		}
		
		public GroupConfigBuilder hideAll(String... except)
		{
			for (Map.Entry<String, Boolean> e : this.visMap.entrySet())
			{
				e.setValue(Arrays.asList(except).contains(e.getKey()));
			}
			return this;
		}
		
		public GroupConfig build()
		{
			GroupConfig ret = new GroupConfig(this.name, Maps.newHashMap(this.visMap));
			ret.setAlwaysActive(this.alwaysActive);
			return ret;
		}
	}
	
	public static class GroupConfig
	{
		public static final String DEFAULT_CONFIG_NAME = "OBJModel.Default.Config.Key";
		private String name = DEFAULT_CONFIG_NAME;
		private Map<String, Boolean> visMap;
		private boolean alwaysActive = false;
		
		protected GroupConfig(String name, Map<String, Boolean> visMap)
		{
			this.name = name;
			this.visMap = visMap;
		}
		
		public String getName()
		{
			return this.name;
		}
		
		public ImmutableMap<String, Boolean> getVisMap()
		{
			return ImmutableMap.copyOf(this.visMap);
		}
		
		public List<String> getShownGroupNames()
		{
			List<String> shownNames = Lists.newArrayList();
			for (Map.Entry<String, Boolean> e : this.visMap.entrySet())
			{
				if (e.getValue())
				{
					shownNames.add(e.getKey());
				}
			}
			return shownNames;
		}
		
		public List<String> getHiddenGroupNames()
		{
			List<String> hiddenNames = Lists.newArrayList();
			for (Map.Entry<String, Boolean> e : this.visMap.entrySet())
			{
				if (!e.getValue())
				{
					hiddenNames.add(e.getKey());
				}
			}
			return hiddenNames;
		}
		
		public boolean getAlwaysActive()
		{
			return this.alwaysActive;
		}
		
		protected void setAlwaysActive(boolean alwaysActive)
		{
			this.alwaysActive = alwaysActive;
		}
		
		@Override
		public String toString()
		{
			StringBuilder builder = new StringBuilder(String.format("%nName: %s%n", this.name));
			builder.append("[ ");
			for (Iterator<Map.Entry<String, Boolean>> iterator = this.visMap.entrySet().iterator(); iterator.hasNext();)
			{
				Map.Entry<String, Boolean> e = iterator.next();
				builder.append(String.format("%s: %b%s", e.getKey(), e.getValue(), iterator.hasNext() ? ", " : " ]"));
			}
			return builder.toString();
		}
	}
}
