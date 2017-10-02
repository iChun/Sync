package me.ichun.mods.sync.common.core;

import com.google.gson.JsonObject;
import me.ichun.mods.ichunutil.common.recipe.ConditionalIngredient;
import me.ichun.mods.sync.common.Sync;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;

import javax.annotation.Nonnull;

public class RecipeHardcoreFactory implements IIngredientFactory
{
    @Nonnull
    @Override
    public Ingredient parse(JsonContext context, JsonObject json)
    {
        return ConditionalIngredient.parseWithCondition(() -> {
            World world = DimensionManager.getWorld(0);
            return Sync.config.hardcoreMode == 1 || Sync.config.hardcoreMode == 2 && (world == null || world.getWorldInfo().isHardcoreModeEnabled());
        }, context, json);
    }
}
