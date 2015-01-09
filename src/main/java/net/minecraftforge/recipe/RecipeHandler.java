package net.minecraftforge.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * This class has a static lookup table containing every machine that uses this API,
 * allowing each mod to see the machines and recipes from other mods.
 * 
 * The instances of this class stores all of the recipes for an associated machine.
 * Recipes are stored in the prefix trees, shapedTrie and shapelessTrie.
 * 
 * Recipe lookup is using a filtered depth first search. For better effieciency, order
 * the crafting ingredients in a way such that more recipes are filtered out early on.
 * For example, an ingredient that represents an item should come before an ingredient
 * that represents an amount of energy, since knowing the former can filter out a lot
 * more recipes than knowing the latter. Filtering more recipes early on reduces the
 * number of recursive calls that the depth first search has to make.
 * 
 * Reverse recipe lookup is done using reverse lookup tables, shapedReverseLookupTable
 * and shapelessReverseLookupTable. Note that the contents of the reverse lookup tables
 * should NOT be modified in any way. This means that you cannot append the shaped recipe
 * list to the shapeless recipe list directly. Instead, create a new list and copy the
 * elements over. An example of appending is in the lookupAllRecipes() method.
 * 
 * @author Nephroid
 */
public class RecipeHandler
{
    public static HashMap<String, IRecipeMachine> machineLookupTable = new HashMap<String, IRecipeMachine>();
    
    private IRecipeMachine machineInstance;
    private RecipeNode shapedTrie;
    private RecipeNode shapelessTrie;
    private IdentityHashMap<Item, List<Pair<AbstractIngredient[], Float>>> shapedReverseLookupTable;
    private IdentityHashMap<Item, List<Pair<AbstractIngredient[], Float>>> shapelessReverseLookupTable;

    public RecipeHandler(IRecipeMachine machineInstance)
    {
        this.machineInstance = machineInstance;
        
        String uniqueName = machineInstance.getUniqueMachineName();
        if (machineLookupTable.containsKey(uniqueName))
        {
            throw new RuntimeException("Another machine named " + uniqueName + " has already been registered.");
        }
        
        machineLookupTable.put(uniqueName, machineInstance);
        
        shapedTrie = new RecipeNode(null);
        shapelessTrie = new RecipeNode(null);
        shapedReverseLookupTable = new IdentityHashMap<Item, List<Pair<AbstractIngredient[], Float>>>();
        shapelessReverseLookupTable = new IdentityHashMap<Item, List<Pair<AbstractIngredient[], Float>>>();
    }
    
    public static IRecipeMachine getRecipeMachine(IRecipeMachine machine)
    {
        return machineLookupTable.get(machine.getUniqueMachineName());
    }
    
    public static IRecipeMachine getRecipeMachine(String machineName)
    {
        return machineLookupTable.get(machineName);
    }
    
    public static Collection<IRecipeMachine> getMachines() {
        return machineLookupTable.values();
    }

    public List<Pair<AbstractIngredient[], Float>> lookupShapedRecipes(Item i)
    {
        return shapedReverseLookupTable.get(i);
    }

    public List<Pair<AbstractIngredient[], Float>> lookupShapelessRecipes(Item i)
    {
        return shapelessReverseLookupTable.get(i);
    }
    
    public List<Pair<AbstractIngredient[], Float>> lookupAllRecipes(Item i)
    {
        List<Pair<AbstractIngredient[], Float>> result = new ArrayList<Pair<AbstractIngredient[], Float>>();
        
        List<Pair<AbstractIngredient[], Float>> shapedResults = lookupShapedRecipes(i);
        List<Pair<AbstractIngredient[], Float>> shapelessResults = lookupShapelessRecipes(i);
        
        for (Pair p : shapedResults) result.add(p);
        for (Pair p : shapelessResults) result.add(p);
        
        return result;
    }
    
    public ItemStack lookupResult(AbstractIngredient[] ingredientList)
    {
        Pair<ItemStack, List<Pair<ItemStack, Float>>> result = lookupResultStochastic(ingredientList);
        
        if (result != null)
            return result.getLeft();
        
        return null;
    }

    public Pair<ItemStack, List<Pair<ItemStack, Float>>> lookupResultStochastic(AbstractIngredient[] ingredientList)
    {
        Pair<ItemStack, List<Pair<ItemStack, Float>>> result = recursiveTrieLookup(ingredientList, shapedTrie, 0);

        if (result != null) return result;

        AbstractIngredient[] sortedCopy = (AbstractIngredient[])Arrays.copyOf(ingredientList, ingredientList.length);
        Arrays.sort(sortedCopy);
        
        result = recursiveTrieLookup(sortedCopy, shapelessTrie, 0);

        return result;
    }
    
    public void addShapedRecipe(AbstractIngredient[] ingredientList, ItemStack result)
    {
        addShapedRecipe(ingredientList, result, null);
    }
    
    public void addShapedRecipe(AbstractIngredient[] ingredientList, ArrayList<Pair<ItemStack, Float>> stochasticResults)
    {
        addShapedRecipe(ingredientList, null, stochasticResults);
    }
    
    public void addShapedRecipe(AbstractIngredient[] ingredientList, ItemStack result, List<Pair<ItemStack, Float>> stochasticResults)
    {
        addRecipeToTrie(shapedTrie, ingredientList, result, stochasticResults);
        if (result != null)
            addReverseLookup(shapedReverseLookupTable, ingredientList, 1, result);
        if (stochasticResults != null)
            for (Pair<ItemStack, Float> p : stochasticResults)
                addReverseLookup(shapedReverseLookupTable, ingredientList, p.getRight(), p.getLeft());
    }
    
    public void addShapelessRecipe(AbstractIngredient[] ingredientList, ItemStack result)
    {
        addShapelessRecipe(ingredientList, result, null);
    }
    
    public void addShapelessRecipe(AbstractIngredient[] ingredientList, ArrayList<Pair<ItemStack, Float>> stochasticResults)
    {
        addShapelessRecipe(ingredientList, null, stochasticResults);
    }
    
    public void addShapelessRecipe(AbstractIngredient[] ingredientList, ItemStack result, List<Pair<ItemStack, Float>> stochasticResults)
    {
        Arrays.sort(ingredientList);
        addRecipeToTrie(shapelessTrie, ingredientList, result, stochasticResults);
        if (result != null)
            addReverseLookup(shapelessReverseLookupTable, ingredientList, 1, result);
        if (stochasticResults != null)
            for (Pair<ItemStack, Float> p : stochasticResults)
                addReverseLookup(shapelessReverseLookupTable, ingredientList, p.getRight(), p.getLeft());
    }

    private Pair<ItemStack, List<Pair<ItemStack, Float>>> recursiveTrieLookup(AbstractIngredient[] ingredientList, RecipeNode trie, int index)
    {
        if (trie.possibleNextIngredients().size() == 0)
        {
            if (index < ingredientList.length) return null;
            else return Pair.of(trie.getResult(), trie.getStochasticResults());
        }
        if (index >= ingredientList.length) return null;

        AbstractIngredient nextIngredient = ingredientList[index];
        List<RecipeNode> possiblePaths = new ArrayList<RecipeNode>();

        for (RecipeNode n : trie.possibleNextIngredients()) if (n.fulfilled(nextIngredient)) possiblePaths.add(n);

        if (possiblePaths.size() == 0) return null;

        for (RecipeNode n : possiblePaths)
        {
            Pair<ItemStack, List<Pair<ItemStack, Float>>> result = recursiveTrieLookup(ingredientList, n, index + 1);
            if (result != null) return result;
        }

        return null;
    }

    private void addRecipeToTrie(RecipeNode trie, AbstractIngredient[] ingredientList, ItemStack result, List<Pair<ItemStack, Float>> stochasticResults)
    {
        RecipeNode currentNode = trie;
        for (int i = 0; i < ingredientList.length; i++)
        {
            AbstractIngredient nextIngredient = ingredientList[i];
            int nextIngredientIndex = currentNode.nextIngredientIndex(nextIngredient);
            if (nextIngredientIndex != -1) currentNode = currentNode.possibleNextIngredients().get(nextIngredientIndex);
            else {
                RecipeNode nextNode = new RecipeNode(nextIngredient);
                currentNode.addNextIngredient(nextNode);
                currentNode = nextNode;
            }
        }
        
        currentNode.setResult(result);
        currentNode.setStochasticResults(stochasticResults);
    }

    private void addReverseLookup(IdentityHashMap<Item, List<Pair<AbstractIngredient[], Float>>> reverseLookupTable, AbstractIngredient[] ingredientList, float chance, ItemStack result)
    {
        Item resultItem = result.getItem();
        if (reverseLookupTable.containsKey(resultItem)) 
        {
            System.out.println(reverseLookupTable.get(resultItem));
            reverseLookupTable.get(resultItem).add(Pair.of(ingredientList, chance));
        }
        else
        {
            List<Pair<AbstractIngredient[], Float>> newRecipeList = new ArrayList<Pair<AbstractIngredient[], Float>>();
            newRecipeList.add(Pair.of(ingredientList, chance));
            reverseLookupTable.put(resultItem, newRecipeList);
        }
    }
    
    /**
     * This internal class represents the prefix tree structure.
     */
    private class RecipeNode
    {
        private List<RecipeNode> possibleNextIngredients;
        private AbstractIngredient ingredient;
        private ItemStack result;
        private List<Pair<ItemStack, Float>> stochasticResults;

        public RecipeNode(AbstractIngredient ingredient)
        {
            possibleNextIngredients = new ArrayList<RecipeNode>();
            this.ingredient = ingredient;
        }

        public boolean fulfilled(AbstractIngredient other)
        {
            return ingredient.fulfilled(other);
        }

        public List<RecipeNode> possibleNextIngredients()
        { 
            return possibleNextIngredients;
        }

        public int nextIngredientIndex (AbstractIngredient ingredient)
        {
            return possibleNextIngredients.indexOf(ingredient);
        }

        public boolean addNextIngredient(RecipeNode ingredient)
        {
            return possibleNextIngredients.add(ingredient);
        }

        public void setResult(ItemStack result)
        {
            if (this.result != null)
                System.out.printf("[WARNING] New result (%s) has overwritten a previous recipe result (%s)\n", result, this.result);
            this.result = result;
        }
        
        public void setStochasticResults(List<Pair<ItemStack, Float>> stochasticResults)
        {
            if (this.stochasticResults != null)
                System.out.printf("[WARNING] New stochastic result (%s) has overwritten a previous recipe result (%s)\n", result, this.result);
            
            this.stochasticResults = stochasticResults;
        }

        public ItemStack getResult()
        {
            return result;
        }
        
        public List<Pair<ItemStack, Float>> getStochasticResults()
        {
            return stochasticResults;
        }
    }
}
