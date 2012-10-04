package plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class PluginManager implements IPluginManager {
	private Controller core;
	private WatchDir watchDir;
	private HashMap<Path, Plugin> pathToPlugin;

	public PluginManager(Controller core) throws IOException {
		this.core = core;
		this.pathToPlugin = new HashMap<Path, Plugin>();
		watchDir = new WatchDir(this, FileSystems.getDefault().getPath("plugins"), false);
	}

	public void run() {
		try {
			Path pluginDir = FileSystems.getDefault().getPath("plugins");
			File pluginFolder = pluginDir.toFile();
			File[] files = pluginFolder.listFiles();
			if(files != null) {
				for(File f : files) {
					this.loadBundle(f.toPath());
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		watchDir.processEvents();
	}

	@SuppressWarnings("resource")
	void loadBundle(Path bundlePath) throws Exception {
		File jarBundle = bundlePath.toFile();
		JarFile jarFile = new JarFile(jarBundle);
		Manifest mf = jarFile.getManifest();
        Attributes mainAttribs = mf.getMainAttributes();
        String className = mainAttribs.getValue("Plugin-Class");
        URL[] urls = new URL[]{bundlePath.toUri().toURL()};
        ClassLoader classLoader = new URLClassLoader(urls);
        Class<?> pluginClass = classLoader.loadClass(className);
        Plugin plugin = (Plugin)pluginClass.newInstance();
        this.core.addPlugin(plugin);
        this.pathToPlugin.put(bundlePath, plugin);
        jarFile.close();
	}
	
	void unloadBundle(Path bundlePath) {
		Plugin plugin = this.pathToPlugin.remove(bundlePath);
		if(plugin != null) {
			this.core.removePlugin(plugin.getId());
		}
	}
}
