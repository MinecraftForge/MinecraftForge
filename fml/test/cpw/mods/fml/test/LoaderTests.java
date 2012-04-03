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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.minecraft.src.BaseMod;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.toposort.ModSorter;
import cpw.mods.fml.server.ModLoaderModContainer;


public class LoaderTests {

  private ModContainer mc1;
  private ModContainer mc2;
  private ModContainer mc3;
  private ModContainer mc4;
  private ModContainer mc5;
  private ModContainer mc6;
  private ModContainer mc7;
  private ModContainer mc8;

  @Before
  public void setUp() throws Exception {
    mc1 = new ModLoaderModContainer(Mod1.class, "Test1");
    mc1.preInit();
    mc2 = new ModLoaderModContainer(Mod2.class, "Test2");
    mc2.preInit();
    mc3 = new ModLoaderModContainer(Mod3.class, "Test3");
    mc3.preInit();
    mc4 = new ModLoaderModContainer(Mod4.class, "Test4");
    mc4.preInit();
    mc5 = new ModLoaderModContainer(Mod5.class, "Test5");
    mc5.preInit();
    mc6 = new ModLoaderModContainer(Mod5.class, "Test6");
    mc6.preInit();
    mc7 = new ModLoaderModContainer(Mod5.class, "Test7");
    mc7.preInit();
    mc8 = new ModLoaderModContainer(Mod5.class, "Test8");
    mc8.preInit();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testModSorting() {
    HashMap<String,ModContainer> modList=new HashMap<String,ModContainer>();
    modList.put("Mod1",mc1);
    modList.put("Mod2",mc2);
    modList.put("Mod3",mc3);
    modList.put("Mod4",mc4);
    modList.put("Mod5",mc5);
    modList.put("Mod6",mc6);
    modList.put("Mod7",mc7);
    modList.put("Mod8",mc8);
    ModSorter ms=new ModSorter(Collections.list(Collections.enumeration(modList.values())),modList);
    List<ModContainer> mods=ms.sort();
    assertEquals("Eight mods",8,mods.size());
    System.out.printf("%s\n",mods);
  }
  
  @Test
  public void testModPrioritiesParsing() {
    // Mod 1
    assertTrue("Empty hard dependencies for Mod1", mc1.getDependencies().isEmpty());
    assertTrue("Mod1 predepends on *",mc1.getPreDepends().contains("*"));
    
    // Mod 2
    assertTrue("Empty hard dependencies for Mod2", mc2.getDependencies().isEmpty());
    assertTrue("Mod2 postdepends on *",mc1.getPostDepends().contains("*"));
    
    // Mod 3
    assertEquals("Two hard dependencies for Mod3", 2, mc1.getDependencies().size());
    assertTrue("Hard dependencies for Mod3 contains Mod4", mc1.getDependencies().contains("Mod4"));
    assertTrue("Hard dependencies for Mod3 contains Mod2", mc1.getDependencies().contains("Mod2"));
    assertEquals("One pre depends for Mod3", 1, mc1.getPreDepends().size());
    assertEquals("One post depends for Mod3", 1, mc1.getPostDepends().size());
    
    // Mod 4
    assertTrue("Empty hard dependencies for Mod1", mc1.getDependencies().isEmpty());
    assertTrue("Mod1 predepends on *",mc1.getPreDepends().contains("*"));
    
    // Mod 5
    assertTrue("Empty hard dependencies for Mod1", mc1.getDependencies().isEmpty());
    assertTrue("Mod1 predepends on *",mc1.getPreDepends().contains("*"));
  }
  
  public static class Mod1 extends BaseMod {
    @Override
    public String getVersion() {
      return null;
    }
    @Override
    public void load() {
    }
    @Override
    public String getPriorities() {
      return "before:*";
    }
  }
  public static class Mod2 extends BaseMod {
    @Override
    public String getVersion() {
      return null;
    }
    @Override
    public void load() {
    }
    @Override
    public String getPriorities() {
      return "after:*";
    }
  }
  public static class Mod3 extends BaseMod {
    @Override
    public String getVersion() {
      return null;
    }
    @Override
    public void load() {
    }
    @Override
    public String getPriorities() {
      return "required-before:Mod4;required-after:Mod5";
    }
  }
  public static class Mod4 extends BaseMod {
    @Override
    public String getVersion() {
      return null;
    }
    @Override
    public void load() {
    }
    @Override
    public String getPriorities() {
      return "required-before:Mod2;after:Mod3;required-after:Mod1";
    }
  }
  public static class Mod5 extends BaseMod {
    @Override
    public String getVersion() {
      return null;
    }
    @Override
    public void load() {
    }
    @Override
    public String getPriorities() {
      return "";
    }
  }
  public static class Mod6 extends BaseMod {
    @Override
    public String getVersion() {
      return null;
    }
    @Override
    public void load() {
    }
    @Override
    public String getPriorities() {
      return "";
    }
  }
  public static class Mod7 extends BaseMod {
    @Override
    public String getVersion() {
      return null;
    }
    @Override
    public void load() {
    }
    @Override
    public String getPriorities() {
      return "";
    }
  }
  public static class Mod8 extends BaseMod {
    @Override
    public String getVersion() {
      return null;
    }
    @Override
    public void load() {
    }
    @Override
    public String getPriorities() {
      return "before:Mod7";
    }
  }
}
