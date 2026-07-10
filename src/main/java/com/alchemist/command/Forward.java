package com.alchemist.command;

public class Forward extends Command {
    public Forward(String name, String argument) {
        super(name, argument);
    }

    @Override
    public Command execute(String name, String argument) {
        int arg = Integer.parseInt(argument);

        for(int i = 0; i <= arg; i++) {


        }
        return this;
    }
}
