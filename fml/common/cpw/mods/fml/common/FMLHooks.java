/*
 * The FML Forge Mod Loader suite.
 * Copyright (C) 2012 cpw
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package cpw.mods.fml.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;



public class FMLHooks {
  private static final FMLHooks INSTANCE=new FMLHooks();
  private Map<ModContainer,Set<String>> channelList=new HashMap<ModContainer,Set<String>>();
  private Map<String,ModContainer> modChannels=new HashMap<String,ModContainer>();
  
  public void gameTickStart() {
    for (ModContainer mod : Loader.getModList()) {
      mod.tickStart();
    }
  }
  
  public void gameTickEnd() {
    for (ModContainer mod : Loader.getModList()) {
      mod.tickEnd();
    }
  }

  /**
   * @return the instance
   */
  public static FMLHooks instance() {
    return INSTANCE;
  }

  public ModContainer getModForChannel(String channel) {
    return modChannels.get(channel);
  }
  /**
   * @param modLoaderModContainer
   * @return
   */
  public Set<String> getChannelListFor(ModContainer container) {
    return channelList.get(container);
  }
  
  public void registerChannel(ModContainer container, String channelName) {
    if (modChannels.containsKey(channelName)) {
      Loader.log.severe(String.format("Mod %s tried to register network channel %s, already registered to %s", container, channelName, modChannels.get(channelName)));
      throw new LoaderException();
    }
    Set<String> list=channelList.get(container);
    if (list==null) {
      list=new HashSet<String>();
      channelList.put(container, list);
    }
    list.add(channelName);
  }
}
