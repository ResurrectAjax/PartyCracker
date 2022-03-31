package Commands;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import General.GeneralMethods;
import Interfaces.ChildCommand;
import Interfaces.ParentCommand;
import Main.Main;
import PartyCracker.Cracker;
import PartyCracker.PartyCrackerManager;

public class GiveCracker extends ChildCommand{
	
	private Main main;
	public GiveCracker(Main main) {
		this.main = main;
	}
	
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "give";
	}

	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/partycracker give <partycracker>";
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return "Get a party cracker";
	}

	public String[] getArguments(UUID uuid) {
		FileConfiguration config = main.getConfig();
		ConfigurationSection section = config.getConfigurationSection("PartyCrackers");
		
		String[] args = new String[section.getKeys(false).size()];
		int count = 0;
		for(String cracker : section.getKeys(false)) {
			args[count] = cracker;
			count++;
		}
		return args;
	}

	public List<ParentCommand> getSubCommands() {
		// TODO Auto-generated method stub
		return null;
	}

	public void perform(Player player, String[] args) {
		if(args.length > 3) {
			player.sendMessage(GeneralMethods.getBadSyntaxMessage(getSyntax()));
			return;
		}
		
		FileConfiguration lang = main.getLanguage();
		if(!Arrays.asList(getArguments(player.getUniqueId())).stream().anyMatch(arg -> arg.toLowerCase().equals(args[1].toLowerCase()))) player.sendMessage(GeneralMethods.format(lang.getString("Command.PartyCracker.NotExist.Message")));
		else if(args.length == 3){
			PartyCrackerManager manager = main.getCrackerManager();
			int amount = 1;
			if(GeneralMethods.isInteger(args[2])) amount = Integer.parseInt(args[2]);
			
			for(Cracker cracker : manager.getPossibleCrackers()) {
				if(cracker.getName().equalsIgnoreCase(args[1])) {
					cracker.setAmount(amount);
					player.getInventory().addItem(cracker);
					break;
				}
			}
		}
		else if(args.length == 2){
			PartyCrackerManager manager = main.getCrackerManager();
			
			for(Cracker cracker : manager.getPossibleCrackers()) {
				if(cracker.getName().equalsIgnoreCase(args[1])) {
					player.getInventory().addItem(cracker);
					break;
				}
			}
		}
	}

	@Override
	public boolean hasGUI() {
		// TODO Auto-generated method stub
		return false;
	}

}
