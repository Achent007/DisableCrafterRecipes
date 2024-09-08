package be.achent.DisableCrafterRecipes.Events;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class DisableCrafterRecipesEvent implements Listener {

    private final Set<Material> blockedRecipes;

    public DisableCrafterRecipesEvent(Set<Material> blockedRecipes) {
        this.blockedRecipes = blockedRecipes;
    }

    @EventHandler
    public void onCrafterCraft(CrafterCraftEvent event) {
        ItemStack result = event.getResult();
        if (blockedRecipes.contains(result.getType())) {
            event.setCancelled(true);
        }
    }
}
