package fml.server;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ModClassLoader extends URLClassLoader {

  public ModClassLoader() {
    super(new URL[0]);
  }

  public void addFile(File modFile) throws MalformedURLException {
    URL url=modFile.toURI().toURL();
    super.addURL(url);
  }
}
