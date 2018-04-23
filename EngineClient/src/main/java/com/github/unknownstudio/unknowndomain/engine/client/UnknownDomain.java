package com.github.unknownstudio.unknowndomain.engine.client;

public class UnknownDomain {
	
	public static final String NAME = "${project.name}";
	public static final String VERSION = "${project.version";
	
    public static final int WIDTH = 550, HEIGHT = 400;
    private static GameMain main;

    public static void main(String[] args) {
        main = new GameMain(WIDTH, HEIGHT);
    }
}
