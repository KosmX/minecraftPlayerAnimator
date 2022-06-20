package net.examplemod;



public class ExampleMod {

    public static void init() {

        System.out.println(ExampleExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());
    }
}
