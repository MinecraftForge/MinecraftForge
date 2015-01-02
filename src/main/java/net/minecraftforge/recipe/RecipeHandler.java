package net.minecraftforge.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipeHandler
{
    private IRecipeMachine machineInstance;
    private RecipeNode shapedTrie;
    private RecipeNode shapelessTrie;
    private Hashtable<Item, ArrayList<AbstractIngredient[]>> shapedReverseLookupTable;
    private Hashtable<Item, ArrayList<AbstractIngredient[]>> shapelessReverseLookupTable;

    public RecipeHandler(IRecipeMachine machineInstance)
    {
        this.machineInstance = machineInstance;
        shapedTrie = new RecipeNode(null);
        shapelessTrie = new RecipeNode(null);
        shapedReverseLookupTable = new Hashtable<Item, ArrayList<AbstractIngredient[]>>();
        shapelessReverseLookupTable = new Hashtable<Item, ArrayList<AbstractIngredient[]>>();
    }

    public ArrayList<AbstractIngredient[]> lookupShapedRecipes(Item i)
    {
        return shapedReverseLookupTable.get(i);
    }

    public ArrayList<AbstractIngredient[]> lookupShapelessRecipes(Item i)
    {
        return shapelessReverseLookupTable.get(i);
    }

    public ItemStack lookupResult(AbstractIngredient[] ingredientList)
    {
        ItemStack result = recursiveTrieLookup(ingredientList, shapedTrie, 0);

        if (result != null) return result;

        AbstractIngredient[] sortedCopy = (AbstractIngredient[])Arrays.copyOf(ingredientList, ingredientList.length);
        Arrays.sort(sortedCopy);
        
        result = recursiveTrieLookup(sortedCopy, shapelessTrie, 0);

        return result;
    }

    protected void addShapedRecipe(AbstractIngredient[] ingredientList, ItemStack result)
    {
        addRecipeToTrie(shapedTrie, ingredientList, result);
        addReverseLookup(shapedReverseLookupTable, ingredientList, result);
    }

    protected void addShapelessRecipe(AbstractIngredient[] ingredientList, ItemStack result)
    {
        Arrays.sort(ingredientList);
        addRecipeToTrie(shapelessTrie, ingredientList, result);
        addReverseLookup(shapelessReverseLookupTable, ingredientList, result);
    }

    private ItemStack recursiveTrieLookup(AbstractIngredient[] ingredientList, RecipeNode trie, int index)
    {
        if (trie.possibleNextIngredients().size() == 0)
        {
            if (index < ingredientList.length) return null;
            else return trie.getResult();
        }
        if (index >= ingredientList.length) return null;

        AbstractIngredient nextIngredient = ingredientList[index];
        ArrayList<RecipeNode> possiblePaths = new ArrayList<RecipeNode>();

        for (RecipeNode n : trie.possibleNextIngredients()) if (n.fulfilled(nextIngredient)) possiblePaths.add(n);

        if (possiblePaths.size() == 0) return null;

        for (RecipeNode n : possiblePaths)
        {
            ItemStack result = recursiveTrieLookup(ingredientList, n, index + 1);
            if (result != null) return result;
        }

        return null;
    }

    private void addRecipeToTrie(RecipeNode trie, AbstractIngredient[] ingredientList, ItemStack result)
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

        if (currentNode.setResult(result)) System.out.printf("[WARNING] New result (%s) has overwritten a previous recipe result!\n", result);
    }

    private void addReverseLookup(Hashtable<Item, ArrayList<AbstractIngredient[]>> reverseLookupTable, AbstractIngredient[] ingredientList, ItemStack result)
    {
        Item resultItem = result.getItem();
        if (reverseLookupTable.containsKey(resultItem)) 
        {
            System.out.println(reverseLookupTable.get(resultItem));
            reverseLookupTable.get(resultItem).add(ingredientList);
        }
        else
        {
            ArrayList<AbstractIngredient[]> newRecipeList = new ArrayList<AbstractIngredient[]>();
            newRecipeList.add(ingredientList);
            reverseLookupTable.put(resultItem, newRecipeList);
        }
    }

    private class RecipeNode
    {
        private ArrayList<RecipeNode> possibleNextIngredients;
        private AbstractIngredient ingredient;
        private ItemStack result;

        public RecipeNode(AbstractIngredient ingredient)
        {
            possibleNextIngredients = new ArrayList<RecipeNode>();
            this.ingredient = ingredient;
        }

        public boolean fulfilled(AbstractIngredient other)
        {
            if (!ingredient.getClass().equals(other.getClass()))
                return false;
            return ingredient.fulfilled(other);
        }

        public ArrayList<RecipeNode> possibleNextIngredients()
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

        public boolean setResult(ItemStack result)
        {
            boolean overwritingResult = this.result != null;
            this.result = result;
            return overwritingResult;
        }

        public ItemStack getResult()
        {
            return result;
        }
    }
}
