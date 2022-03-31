package Main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import General.GeneralMethods;
import Interfaces.ParentCommand;
import Listeners.ItemListeners;
import Managers.CommandManager;
import Managers.FileManager;
import PartyCracker.PartyCrackerManager;

/**
 * Main class
 * 
 * @author ResurrectAjax
 * */
public class Main extends JavaPlugin{
	private static Main INSTANCE;
	
	private CommandManager commandManager;
	private FileManager fileManager;
	private FileConfiguration config, language;
	private PartyCrackerManager crackerManager;
	
	/**
	 * Static method to get the {@link Main} instance
	 * @return {@link Main} instance
	 * */
	public static Main getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Enable plugin and load files/commands
	 * */
	public void onEnable() {
		
		loadFiles();
		loadListeners();
		
		
		TabCompletion tabCompleter = new TabCompletion(this);
		//set the tabCompleter
		for(ParentCommand command : commandManager.getCommands()) {
			getCommand(command.getName()).setTabCompleter(tabCompleter);
		}
	}
	
	/**
	 * Load all the classes that implement {@link Listener}
	 * */
	private void loadListeners() {
		getServer().getPluginManager().registerEvents(new ItemListeners(this), this);
	}
	
	/**
	 * Handle command execution
	 * @param sender {@link CommandSender} who sent the command
	 * @param cmd {@link Command} sent command
	 * @param label {@link String} label of the command
	 * @param args {@link String}[] arguments
	 * */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player)sender;
			
			//check all the base commands in this plugin
			if(commandManager.getCommandByName(cmd.getName()) != null) {
				ParentCommand command = commandManager.getCommandByName(cmd.getName());
				runCommand(command, player, args);
			}
		}
		else {
			sender.sendMessage(GeneralMethods.format(language.getString("Command.Error.ByConsole.Message")));
		}
		return true;
	}
	
	/**
	 * Iterate over all commands and subcommands to find the right command to execute
	 * @param command {@link ParentCommand} where the method runs from
	 * @param player {@link Player} who sent the command
	 * @param args {@link String}[] arguments given with the command
	 * */
	private void runCommand(ParentCommand command, Player player, String[] args) {
		for(String arg : args) {
			String permissionNode = command.getPermissionNode();
			String noPermission = GeneralMethods.format(language.getString("Command.Error.NoPermission.Message"));
			
			if(permissionNode != null && !player.hasPermission(permissionNode)) {
				player.sendMessage(noPermission);
				return;
			}
			if(command.getSubCommands() == null || command.getSubCommands().isEmpty()) {
				command.perform(player, args);
				return;
			}
			
			for(ParentCommand subcommand : command.getSubCommands()) {
				if(subcommand.getName().equalsIgnoreCase(arg)) {
					runCommand(subcommand, player, args);
					return;
				}
			}
		}
		command.perform(player, args);
	}
	
	/**
	 * Get the command manager
	 * @return {@link CommandManager} manager
	 * */
	public CommandManager getCommandManager() {
		return commandManager;
	}

	/**
	 * Get the file manager
	 * @return {@link FileManager} manager
	 * */
	public FileManager getFileManager() {
		return fileManager;
	}

	/**
	 * Get the config file
	 * @return {@link FileConfiguration} config
	 * */
	public FileConfiguration getConfig() {
		return config;
	}

	/**
	 * Get the language file
	 * @return {@link FileConfiguration} language
	 * */
	public FileConfiguration getLanguage() {
		return language;
	}
	
	/**
	 * Reload the {@link Yaml} files
	 * */
	public void reload() {
        fileManager.loadFiles();
        config = fileManager.getConfig("config.yml");
        language = fileManager.getConfig("language.yml");
    }

	/**
	 * Load the {@link Yaml} files and classes
	 * */
	private void loadFiles() {
		INSTANCE = this;
		
		//load files
		fileManager = new FileManager(this);
        fileManager.loadFiles();
        config = fileManager.getConfig("config.yml");
        language = fileManager.getConfig("language.yml");
        //files
		
		//load classes
		commandManager = new CommandManager(this);
		crackerManager = new PartyCrackerManager(this);
		//classes
	}

	public PartyCrackerManager getCrackerManager() {
		return crackerManager;
	}
}
