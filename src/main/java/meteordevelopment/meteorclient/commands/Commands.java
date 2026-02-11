package meteordevelopment.meteorclient.commands;

import com.mojang.brigadier.CommandDispatcher;

import java.util.ArrayList;
import java.util.List;

public final class Commands {
    public static final List<Command> COMMANDS = new ArrayList<>();

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static final CommandDispatcher DISPATCHER = new CommandDispatcher();

    private Commands() {
    }

    public static void add(Command command) {
        if (command != null) COMMANDS.add(command);
    }

    public static void reset() {
        COMMANDS.clear();
    }
}
