package Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Interfaces.ParentCommand;
import Main.Main;

public class PartyCrackerCommand extends ParentCommand{
	private List<ParentCommand> subcommands;
	
	public PartyCrackerCommand(Main main) {
		subcommands = new ArrayList<ParentCommand>(Arrays.asList(
				new GiveCracker(main),
				new Reload(main)
				));
	}
	
	@Override
	public String getPermissionNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "partycracker";
	}

	@Override
	public String getSyntax() {
		// TODO Auto-generated method stub
		return "/partycracker <subcommand>";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Runs the partycracker command";
	}

	@Override
	public List<ParentCommand> getSubCommands() {
		// TODO Auto-generated method stub
		return subcommands;
	}

	@Override
	public boolean hasGUI() {
		// TODO Auto-generated method stub
		return false;
	}

}
