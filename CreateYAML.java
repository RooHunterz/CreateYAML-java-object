import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class CreateFileYaml{
	
	private Plugin plugin;
	private String foldername;
	private String filename;
	private File file;
	private YamlConfiguration yamlConfiguration;
	private boolean loaded;
	
	public CreateFileYaml(Plugin plugin, String filename){
		this(plugin, "", filename);
	}
	
	public CreateFileYaml(Plugin plugin, String foldername, String filename){
		this.plugin = plugin;
		this.foldername = (foldername == null || foldername.isEmpty()) ? "" : foldername + "/";
		this.filename = filename;
		this.loaded = false;
		loadFile();
	}
	
	public String getFileName(){
		return filename;
	}
	
	public File getFile(){
		return file;
	}

	public YamlConfiguration getFileYaml(){
		if (!loaded){
			loadFile();
		}
		return yamlConfiguration;
	}
	
	public void set(String path, Object value){
		if(!yamlConfiguration.contains(path)) {
			yamlConfiguration.set(path, value);
		}
	}
	
	public void reSetValue(String path, Object value){
		if(!yamlConfiguration.contains(path)) {
			yamlConfiguration.set(path, value);
		}
	}
	
	public void loadFile(){
		file = new File(plugin.getDataFolder() + "/" + foldername, filename + ".yml");
		if (file.exists()){
			yamlConfiguration = new YamlConfiguration();
			try{
				yamlConfiguration.load(file);
			} catch (FileNotFoundException filenotfoundexception){
				plugin.getServer().getLogger().severe("Error in [" + filename + "] - File not found!");
			} catch (IOException ioexception){
				plugin.getServer().getLogger().severe("Error in [" + filename + "] - Error while reading!");
			} catch (InvalidConfigurationException ioexception){
				plugin.getServer().getLogger().severe("Error in [" + filename + "] - Corrupted YML!");
				ioexception.printStackTrace();
			}
			loaded = true;
		} else {
			try{
				plugin.getDataFolder().mkdir();
				new File(plugin.getDataFolder() + "/" + foldername).mkdir();
				InputStream inputstream = plugin.getClass().getResourceAsStream("/" + filename + ".yml");
				if (inputstream != null){
					copyFile(inputstream, file);
					plugin.getServer().getLogger().severe("Copying '" + filename + ".yml' from the resources!");
				} else {
					file.createNewFile();
					plugin.getServer().getLogger().severe("Creating new file '" + plugin.getDataFolder() + "\\" + foldername + filename + "'!");
				}
				(yamlConfiguration = new YamlConfiguration()).load(file);
				loaded = true;
				inputstream.close();
			} catch (Exception exception){}
		}
	}
	
	public void saveFile(){
		if (loaded == true){
			try{
				yamlConfiguration.save(file);
			} catch(IOException ioexception){

			}
		} else {
			loadFile();
		}
	}

	public void reloadFile(){
		loadFile();
	}
	
	public void fileBackup(){
		File backupfile = new File(plugin.getDataFolder() + "/" + foldername, filename + "_" + new Date().getTime() + ".yml");
		file.renameTo(backupfile);
		file = null;
		loadFile();
	}
	
    private static void copyFile(final InputStream inputstream, final File file) throws Exception{
        final FileOutputStream fos = new FileOutputStream(file);
        try{
            final byte[] buf = new byte[1024];
            int i = 0;
            while ((i = inputstream.read(buf)) != -1){
                fos.write(buf, 0, i);
            }
        } catch (Exception e){
            throw e;
        } finally{
            if (inputstream != null){
            	inputstream.close();
            }
            if (fos != null){
                fos.close();
            }
        }
        if (inputstream != null){
        	inputstream.close();
        }
        if (fos != null){
            fos.close();
        }
    }
}
