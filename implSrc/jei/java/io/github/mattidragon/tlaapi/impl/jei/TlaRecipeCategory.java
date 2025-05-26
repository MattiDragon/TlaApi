package io.github.mattidragon.tlaapi.impl.jei;

import io.github.mattidragon.tlaapi.api.recipe.TlaCategory;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TlaRecipeCategory implements IRecipeCategory<PreparedRecipe> {
    private final TlaCategory category;
    private final RecipeType<PreparedRecipe> type;
    private final IDrawable icon;
    private final IDrawable background;

    public TlaRecipeCategory(IJeiHelpers helpers, TlaCategory category) {
        this.category = category;
        this.type = RecipeType.create(category.getId().getNamespace(), category.getId().getPath(), PreparedRecipe.class);
        this.icon = JeiUtils.iconToDrawable(helpers, category.getIcon());
        this.background = helpers.getGuiHelper().createBlankDrawable(category.getDisplayWidth(), category.getDisplayHeight());
    }

    @Override
    public RecipeType<PreparedRecipe> getRecipeType() {
        return type;
    }

    @Override
    public Text getTitle() {
        return category.getName();
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(PreparedRecipe recipe, IRecipeSlotsView recipeSlotsView, DrawContext guiGraphics, double mouseX, double mouseY) {
        for (PreparedRecipe.DrawableEntry drawable : recipe.getDrawables()) {
            drawable.drawable().draw(guiGraphics, drawable.x(), drawable.y());
        }
    }

    @Override
    public List<Text> getTooltipStrings(PreparedRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        return recipe.getTooltips((int) mouseX, (int) mouseY);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PreparedRecipe recipe, IFocusGroup focuses) {
        recipe.addSlots(builder);
        // Make JEI think there are slots even when there aren't to force it to display the recipe
        builder.addInvisibleIngredients(RecipeIngredientRole.RENDER_ONLY);
    }

    @Override
    public @Nullable Identifier getRegistryName(PreparedRecipe recipe) {
        return recipe.getId();
    }
}
