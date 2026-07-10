package com.alchemist.world;

public class Person {
    private String name;
    private Echo echo;
    private double morality;

    public Person(String name, Echo echo, double morality) {
        this.name = name;
        this.echo = echo;
        this.morality = morality;
    }

    public String getName() {
        return name;
    }

    public Echo getEcho() {
        return echo;
    }

    public double getMorality() {
        return morality;
    }
}
