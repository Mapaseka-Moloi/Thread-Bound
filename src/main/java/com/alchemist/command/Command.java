package com.alchemist.command;

public abstract class Command {
    private final String name;
    private String arguments;

    public Command(String name, String arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public String getArguments() {
        return arguments;
    }

    public abstract Command execute(String name, String argument);
}
