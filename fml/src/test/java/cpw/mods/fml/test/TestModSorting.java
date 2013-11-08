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
package cpw.mods.fml.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import cpw.mods.fml.common.FMLModContainer;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.toposort.ModSorter;

/*
2012-06-09 00:21:06 [FINE]      mod_CodeChickenCore: CodeChickenCore-Server 0.5.2.zip (before:*)
2012-06-09 00:21:06 [FINE]      mod_IC2: industrialcraft-2-server_1.95b.jar ()
2012-06-09 00:21:06 [FINE]      mod_IC2AdvancedMachines: AdvancedMachines_4.1_server.zip (after:mod_IC2)
2012-06-09 00:21:06 [FINE]      mod_EE: EE2ServerV1.4.6.0.jar ()
2012-06-09 00:21:06 [FINE]      mod_EnderStorage: EnderStorage-Server 1.1.1.zip ()
2012-06-09 00:21:06 [FINE]      mod_IC2NuclearControl: IC2NuclearControl_server_v1.1.8.zip ()
2012-06-09 00:21:06 [FINE]      mod_InventoryStocker: InventoryStocker-1.2.5.b43-server.zip ()
2012-06-09 00:21:06 [FINE]      mod_NotEnoughItems: NotEnoughItems-Server 1.2.2.3.zip ()
2012-06-09 00:21:06 [FINE]      mod_Railcraft: Railcraft_Server_5.3.2.zip ()
2012-06-09 00:21:06 [FINE]      mod_RedPowerControl: RedPowerControl-Server-2.0pr5b2.zip ()
2012-06-09 00:21:06 [FINE]      mod_RedPowerCore: RedPowerCore-Server-2.0pr5b2.zip ()
2012-06-09 00:21:06 [FINE]      mod_RedPowerLighting: RedPowerLighting-Server-2.0pr5b2.zip ()
2012-06-09 00:21:06 [FINE]      mod_RedPowerLogic: RedPowerLogic-Server-2.0pr5b2.zip ()
2012-06-09 00:21:06 [FINE]      mod_RedPowerMachine: RedPowerMachine-Server-2.0pr5b2.zip ()
2012-06-09 00:21:06 [FINE]      mod_RedPowerWiring: RedPowerWiring-Server-2.0pr5b2.zip ()
2012-06-09 00:21:06 [FINE]      mod_RedPowerWorld: RedPowerWorld-Server-2.0pr5b2.zip ()
2012-06-09 00:21:06 [FINE]      mod_Transformers: TransformersServer v1.2.zip (required-after:mod_IC2;required-after:mod_BuildCraftBuilders;required-after:mod_BuildCraftCore;required-after:mod_BuildCraftEnergy;required-after:mod_BuildCraftFactory;required-after:mod_BuildCraftTransport)
2012-06-09 00:21:06 [FINE]      mod_WirelessRedstoneCore: WR-CBE Core-Server 1.2.2.zip ()
2012-06-09 00:21:06 [FINE]      mod_WirelessRedstoneRedPower: WR-CBE RedPower-Server 1.2.2.zip (after:mod_WirelessRedstoneCore)
2012-06-09 00:21:06 [FINE]      mod_BuildCraftCore: buildcraft-server-A-core-2.2.14.zip ()
2012-06-09 00:21:06 [FINE]      mod_BuildCraftBuilders: buildcraft-server-B-builders-2.2.14.zip ()
2012-06-09 00:21:06 [FINE]      mod_BuildCraftEnergy: buildcraft-server-B-energy-2.2.14.zip ()
2012-06-09 00:21:06 [FINE]      mod_BuildCraftFactory: buildcraft-server-B-factory-2.2.14.zip ()
2012-06-09 00:21:06 [FINE]      mod_BuildCraftTransport: buildcraft-server-B-transport-2.2.14.zip ()
2012-06-09 00:21:06 [FINE]      mod_AdditionalPipes: buildcraft-server-DA-additionalpipes-2.1.3.zip (after:mod_BuildCraftTransport)
2012-06-09 00:21:06 [FINE]      mod_Forestry: forestry-server-A-1.4.6.2_bc2.2.jar (after:mod_IC2;after:mod_BuildCraftCore;after:mod_BuildCraftEnergy;after:mod_BuildCraftFactory;after:mod_BuildCraftSilicon;after:mod_BuildCraftTransport)
2012-06-09 00:21:06 [FINE]      mod_LoginMessage: mod_LoginMessage-125-FML.zip ()
2012-06-09 00:21:06 [FINE]      mod_CompactSolars: mod_compactsolars-server-2.3.2.10.zip (after:mod_IC2)
2012-06-09 00:21:06 [FINE]      mod_SeedManager: SeedManager-2.0-server.zip (required-after:mod_IC2;after:*)
 */

public class TestModSorting {
  private List<TestModContainer> mods = new ArrayList<TestModContainer>();

  @Before
  public void setUp() throws Exception {
    TestModContainer mc = null;
    mc = new TestModContainer("mod_IC2AdvancedMachines");
    mc.setPreDepends("mod_IC2");
    mc.setPostDepends();
    mods.add(mc);
    
    mc = new TestModContainer("mod_CodeChickenCore");
    mc.setPreDepends();
    mc.setPostDepends("*");
    mods.add(mc);
    
    mc = new TestModContainer("mod_EE");
    mc.setPreDepends();
    mc.setPostDepends();
    mods.add(mc);
    
    mc = new TestModContainer("mod_EnderStorage");
    mc.setPreDepends();
    mc.setPostDepends();
    mods.add(mc);
    
    mc = new TestModContainer("mod_IC2NuclearControl");
    mc.setPreDepends();
    mc.setPostDepends();
    mods.add(mc);
    
    mc = new TestModContainer("mod_InventoryStocker");
    mc.setPreDepends();
    mc.setPostDepends();
    mods.add(mc);
    
    mc = new TestModContainer("mod_NotEnoughItems");
    mc.setPreDepends();
    mc.setPostDepends();
    mods.add(mc);
    
    mc = new TestModContainer("mod_Railcraft");
    mc.setPreDepends();
    mc.setPostDepends();
    mods.add(mc);
    
    mc = new TestModContainer("mod_RedPowerControl");
    mc.setPreDepends();
    mc.setPostDepends();
    mods.add(mc);
    
    mc = new TestModContainer("mod_RedPowerCore");
    mc.setPreDepends();
    mc.setPostDepends();
    mods.add(mc);
    
    mc = new TestModContainer("mod_RedPowerLighting");
    mc.setPreDepends();
    mc.setPostDepends();
    mods.add(mc);
    
    mc = new TestModContainer("mod_RedPowerLogic");
    mc.setPreDepends();
    mc.setPostDepends();
    mods.add(mc);
    
    mc = new TestModContainer("mod_RedPowerMachine");
    mc.setPreDepends();
    mc.setPostDepends();
    mods.add(mc);
    
    mc = new TestModContainer("mod_RedPowerWiring");
    mc.setPreDepends();
    mc.setPostDepends();
    mods.add(mc);
    
    mc = new TestModContainer("mod_RedPowerWorld");
    mc.setPreDepends();
    mc.setPostDepends();
    mods.add(mc);
    
    mc = new TestModContainer("mod_SeedManager");
    mc.setPreDepends("mod_IC2","*");
    mc.setPostDepends();
    mods.add(mc);

    mc = new TestModContainer("mod_Transformers");
    mc.setPreDepends("mod_IC2","mod_BuildCraftBuilders","mod_BuildCraftCore","mod_BuildCraftEnergy","mod_BuildCraftFactory","mod_BuildCraftTransport");
    mc.setPostDepends();
    mods.add(mc);
    
    mc = new TestModContainer("mod_WirelessRedstoneCore");
    mc.setPreDepends();
    mc.setPostDepends();
    mods.add(mc);
    
    mc = new TestModContainer("mod_WirelessRedstoneRedPower");
    mc.setPreDepends("mod_WirelessRedstoneCore");
    mc.setPostDepends();
    mods.add(mc);
    
    mc = new TestModContainer("mod_BuildCraftCore");
    mc.setPreDepends();
    mc.setPostDepends();
    mods.add(mc);
    
    mc = new TestModContainer("mod_BuildCraftBuilders");
    mc.setPreDepends();
    mc.setPostDepends();
    mods.add(mc);
    
    mc = new TestModContainer("mod_BuildCraftEnergy");
    mc.setPreDepends();
    mc.setPostDepends();
    mods.add(mc);
    
    mc = new TestModContainer("mod_BuildCraftFactory");
    mc.setPreDepends();
    mc.setPostDepends();
    mods.add(mc);
    
    mc = new TestModContainer("mod_BuildCraftTransport");
    mc.setPreDepends();
    mc.setPostDepends();
    mods.add(mc);
    
    mc = new TestModContainer("mod_AdditionalPipes");
    mc.setPreDepends("mod_BuildCraftTransport");
    mc.setPostDepends();
    mods.add(mc);
    
    mc = new TestModContainer("mod_Forestry");
    mc.setPreDepends("mod_IC2","mod_BuildCraftCore","mod_BuildCraftEnergy","mod_BuildCraftFactory","mod_BuildCraftSilicon","mod_BuildCraftTransport");
    mc.setPostDepends();
    mods.add(mc);
    
    mc = new TestModContainer("mod_IC2");
    mc.setPreDepends();
    mc.setPostDepends();
    mods.add(mc);
    
    mc = new TestModContainer("mod_LoginMessage");
    mc.setPreDepends();
    mc.setPostDepends();
    mods.add(mc);
    
    mc = new TestModContainer("mod_CompactSolars");
    mc.setPreDepends("mod_IC2");
    mc.setPostDepends();
    mods.add(mc);

//    mc = new TestModContainer("");
//    mc.setPreDepends();
//    mc.setPostDepends();
//    mods.add(mc);
//    

    Collections.shuffle(mods);
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testModSorting() {
    HashMap<String,ModContainer> modList=new HashMap<String,ModContainer>();
    List<ModContainer> mods = new ArrayList<ModContainer>(this.mods);
    
    for (ModContainer m : mods) {
      modList.put(m.getName(), m);
    }
    
    ModSorter ms=new ModSorter(mods,modList);
    List<ModContainer> sortedMods=ms.sort();
    assertEquals("29 mods",29,sortedMods.size());
    
    assertEquals("Chicken Core is at index zero", sortedMods.indexOf(modList.get("mod_CodeChickenCore")),0);
    ModContainer forestry=modList.get("mod_Forestry");
    ModContainer ic2=modList.get("mod_IC2");
    ModContainer bccore=modList.get("mod_BuildCraftCore");
    ModContainer bcener=modList.get("mod_BuildCraftEnergy");
    ModContainer bcfact=modList.get("mod_BuildCraftFactory");
    ModContainer bcbuild=modList.get("mod_BuildCraftBuilders");
    ModContainer bctrans=modList.get("mod_BuildCraftTransport");
    ModContainer tfrmrs=modList.get("mod_Transformers");
    assertTrue("Forestry is after IC2", sortedMods.indexOf(forestry)>sortedMods.indexOf(ic2)); 
    assertTrue("Forestry is after BC core", sortedMods.indexOf(forestry)>sortedMods.indexOf(bccore)); 
    assertTrue("Forestry is after BC energy", sortedMods.indexOf(forestry)>sortedMods.indexOf(bcener)); 
    assertTrue("Forestry is after BC factory", sortedMods.indexOf(forestry)>sortedMods.indexOf(bcfact)); 
    assertTrue("Forestry is after BC trans", sortedMods.indexOf(forestry)>sortedMods.indexOf(bctrans)); 
    assertTrue("Transformers is after IC2", sortedMods.indexOf(tfrmrs)>sortedMods.indexOf(ic2)); 
    assertTrue("Transformers is after BC core", sortedMods.indexOf(tfrmrs)>sortedMods.indexOf(bccore)); 
    assertTrue("Transformers is after BC ener", sortedMods.indexOf(tfrmrs)>sortedMods.indexOf(bcener)); 
    assertTrue("Transformers is after BC fact", sortedMods.indexOf(tfrmrs)>sortedMods.indexOf(bcfact)); 
    assertTrue("Transformers is after BC build", sortedMods.indexOf(tfrmrs)>sortedMods.indexOf(bcbuild)); 
    assertTrue("Transformers is after BC trans", sortedMods.indexOf(tfrmrs)>sortedMods.indexOf(bctrans)); 
    assertTrue("AdvMach is after IC2", sortedMods.indexOf(modList.get("mod_IC2AdvancedMachines"))>sortedMods.indexOf(ic2)); 
    assertTrue("CompactSolars is after IC2", sortedMods.indexOf(modList.get("mod_CompactSolars"))>sortedMods.indexOf(ic2)); 
    assertTrue("WR RP is after WR core", sortedMods.indexOf(modList.get("mod_WirelessRedstoneRedPower"))>sortedMods.indexOf(modList.get("mod_WirelessRedstoneCore"))); 
    assertTrue("AdditionalPipes is after BC trans", sortedMods.indexOf(modList.get("mod_AdditionalPipes"))>sortedMods.indexOf(bctrans)); 
    assertTrue("SeedManager is after IC2", sortedMods.indexOf(modList.get("mod_SeedManager"))>sortedMods.indexOf(ic2)); 
    assertEquals("SeedManager is at the end", 28, sortedMods.indexOf(modList.get("mod_SeedManager")));
    
    sortedMods.remove(modList.get("mod_CodeChickenCore"));
    mods.remove(modList.get("mod_CodeChickenCore"));
    sortedMods.remove(ic2);
    mods.remove(ic2);
    sortedMods.remove(forestry);
    mods.remove(forestry);
    sortedMods.remove(bccore);
    mods.remove(bccore);
    sortedMods.remove(bcbuild);
    mods.remove(bcbuild);
    sortedMods.remove(bcener);
    mods.remove(bcener);
    sortedMods.remove(bcfact);
    mods.remove(bcfact);
    sortedMods.remove(bctrans);
    mods.remove(bctrans);
    sortedMods.remove(tfrmrs);
    mods.remove(tfrmrs);
    sortedMods.remove(modList.get("mod_IC2AdvancedMachines"));
    mods.remove(modList.get("mod_IC2AdvancedMachines"));
    sortedMods.remove(modList.get("mod_SeedManager"));
    mods.remove(modList.get("mod_SeedManager"));
    sortedMods.remove(modList.get("mod_AdditionalPipes"));
    mods.remove(modList.get("mod_AdditionalPipes"));
    sortedMods.remove(modList.get("mod_WirelessRedstoneCore"));
    mods.remove(modList.get("mod_WirelessRedstoneCore"));
    sortedMods.remove(modList.get("mod_WirelessRedstoneRedPower"));
    mods.remove(modList.get("mod_WirelessRedstoneRedPower"));
    sortedMods.remove(modList.get("mod_CompactSolars"));
    mods.remove(modList.get("mod_CompactSolars"));
    assertArrayEquals("Sort is stable", mods.toArray(),sortedMods.toArray());
  }
  
  public class TestModContainer extends FMLModContainer {
    private List<String> preDeps;
    private List<String> postDeps;
    private String name;
    
    public TestModContainer(String name) {
      super((File)null);
      this.name = name;
    }
    
    public void setPreDepends(String ... pre) {
      preDeps = Arrays.asList(pre);
    }
    
    public void setPostDepends(String ... post) {
      postDeps = Arrays.asList(post);
    }
    
    @Override
    public List<String> getPreDepends() {
      return preDeps;
    }
    
    @Override
    public List<String> getPostDepends() {
      return postDeps;
    }
    
    @Override
    public String getName() {
      return name;
    }
    
    @Override
    public String toString() {
      return name;
    }
  }
}
