package meteordevelopment.meteorclient.commands;

import com.mojang.brigadier.CommandDispatcher;

import java.util.Arrays;
import java.util.List;

public class Command {
    private final String name;
    private final String description;
    private final List<String> aliases;

    public Command(String name, String description, String... aliases) {
        this.name = name;
        this.description = description;
        this.aliases = Arrays.asList(aliases == null ? new String[0] : aliases);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getAliases() {
        return aliases;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void registerTo(CommandDispatcher dispatcher) {
    }

    public void info(String message, Object... args) {
    }

    public void warning(String message, Object... args) {
    }

    public void error(String message, Object... args) {
    }
}
