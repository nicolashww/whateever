package ruby.keyboardwarrior.commands;

/**
 * Clears all the items in the Keyboard Warrior.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n" + "Clears all items in Keyboard Warrior permanently.\n\t"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "All items have been cleared!";

    public ClearCommand() {}


    @Override
    public CommandResult execute() {
        tasksList.clear();
        return new CommandResult(MESSAGE_SUCCESS);
    }
    
    @Override
    public boolean isMutating() {
    	return true;
    }
}
