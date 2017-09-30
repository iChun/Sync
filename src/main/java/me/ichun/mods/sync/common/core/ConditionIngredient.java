package me.ichun.mods.sync.common.core;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import me.ichun.mods.sync.common.Sync;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BooleanSupplier;

public class ConditionIngredient extends Ingredient //TODO move this (without the factory) to iChunUtil?
{
    private final BooleanSupplier isNormal;
    private final ItemStack normalStack;
    private final ItemStack alternativeStack;
    private IntList packedNormal;
    private IntList packedAlternative;

    public ConditionIngredient(BooleanSupplier isNormal, ItemStack normalStack, ItemStack alternativeStack)
    {
        this.isNormal = isNormal;
        this.normalStack = normalStack;
        this.alternativeStack = alternativeStack;
    }

    @Override
    public boolean apply(@Nullable ItemStack input) {
        if (input == null)
            return false;
        ItemStack itemStack = isNormal.getAsBoolean() ? normalStack : alternativeStack;
        if (itemStack.getItem() == input.getItem()) //copied from normal ingredient
        {
            int i = itemStack.getMetadata();

            if (i == 32767 || i == input.getMetadata())
            {
                return true;
            }
        }
        return false;
    }

    @Override
    @Nonnull
    public ItemStack[] getMatchingStacks() {
        return new ItemStack[]{isNormal.getAsBoolean() ? normalStack : alternativeStack};
    }

    @Override
    @Nonnull
    public IntList getValidItemStacksPacked() {
        boolean normal = isNormal.getAsBoolean();
        IntList validStacks = normal ? packedNormal : packedAlternative;
        if (validStacks == null)
        {
            validStacks = IntLists.singleton(RecipeItemHelper.pack(normal ? normalStack : alternativeStack));
            if (normal)
                packedNormal = validStacks;
            else
                packedAlternative = validStacks;
        }
        return validStacks;
    }

    @Override
    protected void invalidate() {
        super.invalidate();
        this.packedNormal = null;
        this.packedAlternative = null;
    }

    public static class Factory implements IIngredientFactory {
        private static ItemStack parseItemStack(JsonObject json, String identifier) {
            ResourceLocation normal = new ResourceLocation(JsonUtils.getString(json, identifier));
            return new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(normal)));
        }

        @Nonnull
        @Override
        public Ingredient parse(JsonContext context, JsonObject json) {
            ItemStack normalStack = parseItemStack(json, "normal");
            ItemStack alternativeStack = parseItemStack(json, "alternative");
            return new ConditionIngredient(() -> Sync.config.hardcoreMode == 1 || Sync.config.hardcoreMode == 2 && DimensionManager.getWorld(0).getWorldInfo().isHardcoreModeEnabled(), normalStack, alternativeStack);
        }
    }
}
