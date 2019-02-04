package com.example.examplemod.test;

import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLModLoadingContext;

@Mod("examplemodtest")
public class ExampleModTest {

    public ExampleModTest() {
        // Register the enqueueIMC method for modloading
        FMLModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // Dispatch IMC to ExampleMod
        InterModComms.sendTo("examplemod", "helloworld", (() -> "HELLO from ExampleModTest"));
    }

}
