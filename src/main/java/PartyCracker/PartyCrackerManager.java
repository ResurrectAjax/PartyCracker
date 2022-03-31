package PartyCracker;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import Main.Main;

public class PartyCrackerManager {
	private Main main;
	private List<Cracker> possibleCrackers = new ArrayList<Cracker>();
	
	public PartyCrackerManager(Main main) {
		this.main = main;
		loadPossibleCrackers();
	}
	
	public List<Cracker> getPossibleCrackers() {
		return possibleCrackers;
	}

	public void loadPossibleCrackers() {
		FileConfiguration config = main.getConfig();
		ConfigurationSection section = config.getConfigurationSection("PartyCrackers");
		
		for(String cracker : section.getKeys(false)) {
			possibleCrackers.add(new Cracker(main, cracker));
		}
	}

	
	
}
