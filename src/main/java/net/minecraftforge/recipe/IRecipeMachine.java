package net.minecraftforge.recipe;

/**
 * This interface provides a method for uniquely identifying a machine
 * 
 * @author Nephroid
 */
public interface IRecipeMachine {
    public String getUniqueMachineName();
    
    public RecipeHandler getHandler();
}