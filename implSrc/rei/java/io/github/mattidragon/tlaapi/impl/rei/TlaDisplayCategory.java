package io.github.mattidragon.tlaapi.impl.rei;

import io.github.mattidragon.tlaapi.api.recipe.TlaCategory;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.text.Text;

import java.util.List;

public class TlaDisplayCategory implements DisplayCategory<TlaDisplay> {
    private final CategoryIdentifier<TlaDisplay> id;
    private final TlaCategory category;

    public TlaDisplayCategory(TlaCategory category) {
        this.id = CategoryIdentifier.of(category.getId());
        this.category = category;
    }

    @Override
    public CategoryIdentifier<TlaDisplay> getCategoryIdentifier() {
        return id;
    }

    @Override
    public Text getTitle() {
        return category.getName();
    }

    @Override
    public Renderer getIcon() {
        return ReiUtil.iconToRenderer(category.getIcon());
    }

    @Override
    public List<Widget> setupDisplay(TlaDisplay display, Rectangle bounds) {
        var builder = new ReiGuiBuilder(bounds);
        display.getRecipe().buildGui(builder);
        return builder.getWidgets();
    }

    @Override
    public int getDisplayHeight() {
        return category.getDisplayHeight() + 10;
    }

    @Override
    public int getDisplayWidth(TlaDisplay display) {
        return category.getDisplayWidth() + 10;
    }
}
