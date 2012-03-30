package fml.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fml.server.Loader;

public class LoaderTests {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testModLoading() {
    Loader.run();
  }
}
