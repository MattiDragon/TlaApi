package io.github.mattidragon.tlaapi.impl.jei;

import io.github.mattidragon.tlaapi.api.gui.*;
import io.github.mattidragon.tlaapi.api.recipe.TlaCategory;
import io.github.mattidragon.tlaapi.api.recipe.TlaIngredient;
import io.github.mattidragon.tlaapi.api.recipe.TlaRecipe;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.common.gui.elements.DrawableAnimated;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.DoubleSupplier;

public class PreparedRecipe {
    private final IJeiHelpers helpers;
    private final TlaRecipe recipe;
    private final List<SlotBuilder> slots = new ArrayList<>();
    private final List<DrawableEntry> drawables = new ArrayList<>();
    private final List<TooltipEntry> tooltips = new ArrayList<>();

    public PreparedRecipe(IJeiHelpers helpers, TlaRecipe recipe) {
        this.helpers = helpers;
        this.recipe = recipe;
        var category = recipe.getCategory();
        var bounds = new TlaBounds(0, 0, category.getDisplayWidth(), category.getDisplayHeight());
        recipe.buildGui(new GuiBuilderImpl(bounds));
    }

    public @Nullable Identifier getId() {
        return recipe.getId();
    }

    public TlaCategory getCategory() {
        return recipe.getCategory();
    }

    public void addSlots(IRecipeLayoutBuilder builder) {
        for (var slotBuilder : slots) {
            var slot = builder.addSlot(slotBuilder.role, slotBuilder.getX(), slotBuilder.getY());
            if (slotBuilder.background) {
                if (slotBuilder.large) {
                    slot.setBackground(helpers.getGuiHelper().createDrawable(JeiUtils.VANILLA_GUI_TEXTURE, 90, 74, 26, 26), -5, -5);
                } else {
                    slot.setBackground(helpers.getGuiHelper().getSlotDrawable(), -1, -1);
                }
            }
            for (var jeiStack : slotBuilder.ingredient) {
                addStackToSlot(jeiStack, slot);
            }
            // TODO: hack something together to show chances
        }
    }
    
    public Collection<DrawableEntry> getDrawables() {
        return drawables;
    }
    
    public List<Text> getTooltips(int mouseX, int mouseY) {
        for (var entry : tooltips) {
            if (entry.bounds.contains(mouseX, mouseY)) {
                return entry.tooltip;
            }
        }
        return List.of();
    }

    @Override
    public String toString() {
        return "PreparedRecipe[%s]".formatted(recipe);
    }

    private <T> void addStackToSlot(ITypedIngredient<T> stack, IRecipeSlotBuilder slot) {
        slot.addIngredient(stack.getType(), stack.getIngredient());
    }

    private class GuiBuilderImpl implements GuiBuilder {
        private final TlaBounds bounds;

        public GuiBuilderImpl(TlaBounds bounds) {
            this.bounds = bounds;
        }

        @Override
        public SlotConfig addSlot(TlaIngredient ingredient, int x, int y) {
            var builder = new SlotBuilder(ingredient, x, y);
            slots.add(builder);
            return builder;
        }

        @Override
        public WidgetConfig addTexture(TextureConfig config, int x, int y) {
            var entry = new DrawableEntry(new TextureDrawable(config), x, y);
            drawables.add(entry);
            return new WidgetBuilder(entry);
        }

        @Override
        public WidgetConfig addAnimatedTexture(TextureConfig config, int x, int y, int duration, boolean horizontal, boolean endToStart, boolean fullToEmpty) {
            var direction = horizontal 
                    ? (endToStart ? IDrawableAnimated.StartDirection.RIGHT : IDrawableAnimated.StartDirection.LEFT) 
                    : (endToStart ? IDrawableAnimated.StartDirection.BOTTOM : IDrawableAnimated.StartDirection.TOP);
            if (fullToEmpty) {
                direction = switch (direction) {
                    case TOP -> IDrawableAnimated.StartDirection.BOTTOM;
                    case BOTTOM -> IDrawableAnimated.StartDirection.TOP;
                    case LEFT -> IDrawableAnimated.StartDirection.RIGHT;
                    case RIGHT -> IDrawableAnimated.StartDirection.LEFT;
                };
            }
            var drawable = helpers.getGuiHelper().createAnimatedDrawable(new TextureDrawable(config), duration / 50, direction, fullToEmpty);
            var entry = new DrawableEntry(drawable, x, y);
            drawables.add(entry);
            return new WidgetBuilder(entry);
        }

        @Override
        public WidgetConfig addProgressingTexture(TextureConfig config, int x, int y, DoubleSupplier progress, boolean horizontal, boolean endToStart, boolean fullToEmpty) {
            var direction = horizontal
                    ? (endToStart ? IDrawableAnimated.StartDirection.RIGHT : IDrawableAnimated.StartDirection.LEFT)
                    : (endToStart ? IDrawableAnimated.StartDirection.BOTTOM : IDrawableAnimated.StartDirection.TOP);
            var maxValue = horizontal ? config.width() : config.height();
            // This is technically not api, but I'm not reimplementing DrawableAnimated just to use a ITickTimer with a custom drawable
            var drawable = new DrawableAnimated(new TextureDrawable(config), new ITickTimer() {
                @Override
                public int getValue() {
                    if (fullToEmpty) {
                        return (int) (progress.getAsDouble() * maxValue);
                    } else {
                        return (int) ((1 - progress.getAsDouble()) * maxValue);
                    }
                }

                @Override
                public int getMaxValue() {
                    return maxValue;
                }
            }, direction);
            var entry = new DrawableEntry(drawable, x, y);
            drawables.add(entry);
            return new WidgetBuilder(entry);
        }

        @Override
        public WidgetConfig addArrow(int x, int y, boolean full) {
            var helper = helpers.getGuiHelper();
            var drawable = helper.createDrawable(JeiUtils.VANILLA_GUI_TEXTURE, full ? 82 : 24, full ? 128 : 132, 24, 17);
            var entry = new DrawableEntry(drawable, x, y);
            drawables.add(entry);
            return new WidgetBuilder(entry);
        }

        @Override
        public WidgetConfig addAnimatedArrow(int x, int y, int duration) {
            var helper = helpers.getGuiHelper();
            drawables.add(new DrawableEntry(helper.createDrawable(JeiUtils.VANILLA_GUI_TEXTURE, 24, 132, 24, 17), x, y));
            var drawable = helper.createAnimatedDrawable(helper.createDrawable(JeiUtils.VANILLA_GUI_TEXTURE, 82, 128, 24, 17), duration / 50, IDrawableAnimated.StartDirection.LEFT, false);
            var entry = new DrawableEntry(drawable, x, y);
            drawables.add(entry);
            return new WidgetBuilder(entry);
        }

        @Override
        public WidgetConfig addFlame(int x, int y) {
            var helper = helpers.getGuiHelper();
            var drawable = helper.createDrawable(JeiUtils.VANILLA_GUI_TEXTURE, 82, 114, 14, 14);
            var entry = new DrawableEntry(drawable, x, y);
            drawables.add(entry);
            return new WidgetBuilder(entry);
        }

        @Override
        public WidgetConfig addAnimatedFlame(int x, int y, int duration) {
            var helper = helpers.getGuiHelper();
            drawables.add(new DrawableEntry(helper.createDrawable(JeiUtils.VANILLA_GUI_TEXTURE, 1, 134, 14, 14), x, y));
            var drawable = helper.createAnimatedDrawable(helper.createDrawable(JeiUtils.VANILLA_GUI_TEXTURE, 82, 114, 14, 14), duration / 50, IDrawableAnimated.StartDirection.TOP, true);
            var entry = new DrawableEntry(drawable, x, y);
            drawables.add(entry);
            return new WidgetBuilder(entry);
        }

        @Override
        public TextConfig addText(Text text, int x, int y, int color, boolean shadow) {
            var drawable = new TextDrawable(text, color, shadow);
            var entry = new DrawableEntry(drawable, x, y);
            drawables.add(entry);
            return new TextBuilder(entry, drawable);
        }

        @Override
        public WidgetConfig addCustomWidget(CustomTlaWidget widget) {
            var bounds = widget.getBounds();
            IDrawable drawable = new IDrawable() {
                @Override
                public int getWidth() {
                    return bounds.width();
                }

                @Override
                public int getHeight() {
                    return bounds.height();
                }

                @Override
                public void draw(DrawContext guiGraphics, int xOffset, int yOffset) {
                    var matrices = guiGraphics.getMatrices();
                    matrices.push();
                    matrices.translate(xOffset, yOffset, 0);
                    widget.render(guiGraphics, 0, 0, 0);
                    matrices.pop();
                }
            };
            var entry = new DrawableEntry(drawable, bounds.x(), bounds.y());
            drawables.add(entry);
            return new WidgetBuilder(entry);
        }

        @Override
        public void addTooltip(int x, int y, int width, int height, List<Text> tooltip) {
            tooltips.add(new TooltipEntry(new TlaBounds(x, y, width, height), tooltip));
        }

        @Override
        public TlaBounds getBounds() {
            return bounds;
        }
    }

    private class TextBuilder extends WidgetBuilder implements TextConfig {
        private final TextDrawable drawable;
        
        public TextBuilder(DrawableEntry entry, TextDrawable drawable) {
            super(entry);
            this.drawable = drawable;
        }

        @Override
        public TextConfig alignLeft() {
            drawable.setAlignment(TextDrawable.Alignment.LEFT);
            return this;
        }

        @Override
        public TextConfig alignRight() {
            drawable.setAlignment(TextDrawable.Alignment.RIGHT);
            return this;
        }

        @Override
        public TextConfig alignCenter() {
            drawable.setAlignment(TextDrawable.Alignment.CENTER);
            return this;
        }

        @Override
        public TextBuilder addTooltip(List<Text> tooltip) {
            super.addTooltip(tooltip);
            return this;
        }

        @Override
        public TextBuilder addTooltip(Text... tooltip) {
            super.addTooltip(tooltip);
            return this;
        }

        @Override
        public TlaBounds getBounds() {
            return super.getBounds();
        }
    }
    
    private class WidgetBuilder implements WidgetConfig {
        private final DrawableEntry entry;

        private WidgetBuilder(DrawableEntry entry) {
            this.entry = entry;
        }

        @Override
        public WidgetConfig addTooltip(List<Text> tooltip) {
            tooltips.add(new TooltipEntry(getBounds(), tooltip));
            return this;
        }

        @Override
        public TlaBounds getBounds() {
            return new TlaBounds(entry.x(), entry.y(), entry.drawable().getWidth(), entry.drawable().getHeight());
        }
    }
    
    private class SlotBuilder implements SlotConfig {
        private final int x;
        private final int y;
        private final List<ITypedIngredient<?>> ingredient;
        private RecipeIngredientRole role = RecipeIngredientRole.RENDER_ONLY;
        private boolean background = true;
        private boolean large = false;

        public SlotBuilder(TlaIngredient ingredient, int x, int y) {
            this.x = x;
            this.y = y;
            this.ingredient = JeiUtils.convertIngredient(helpers, ingredient);
        }

        private int getY() {
            return large ? this.y + 5 : this.y + 1;
        }

        private int getX() {
            return large ? this.x + 5 : this.x + 1;
        }

        @Override
        public SlotConfig markOutput() {
            role = RecipeIngredientRole.OUTPUT;
            return this;
        }

        @Override
        public SlotConfig markInput() {
            role = RecipeIngredientRole.INPUT;
            return this;
        }

        @Override
        public SlotConfig markCatalyst() {
            role = RecipeIngredientRole.CATALYST;
            return this;
        }

        @Override
        public SlotConfig disableBackground() {
            background = false;
            return this;
        }

        @Override
        public SlotConfig makeLarge() {
            large = true;
            return this;
        }

        @Override
        public SlotConfig addTooltip(List<Text> tooltip) {
            tooltips.add(new TooltipEntry(getBounds(), tooltip));
            return this;
        }

        @Override
        public TlaBounds getBounds() {
            var x = getX();
            var y = getY();
            
            return large ? new TlaBounds(x, y, 26, 26) : new TlaBounds(x, y, 18, 18);
        }
    }
    
    public record DrawableEntry(IDrawable drawable, int x, int y) {

    }
    public record TooltipEntry(TlaBounds bounds, List<Text> tooltip) {

    }
}
