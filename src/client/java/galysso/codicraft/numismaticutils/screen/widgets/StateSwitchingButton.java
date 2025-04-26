package galysso.codicraft.numismaticutils.screen.widgets;

import galysso.codicraft.numismaticutils.screen.helper.DrawTools;
import galysso.codicraft.numismaticutils.utils.BankerUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Optional;

import static galysso.codicraft.numismaticutils.screen.BankerScreen.ICONS_TEXTURE;

public class StateSwitchingButton<T extends Enum<T>> extends DynamicButton {
    private ArrayList<ButtonState> states = new ArrayList<>();
    private final Identifier texture;
    private int cursor = 0;
    private int maxState = 0;
    boolean hasNullState = false;
    boolean incrementMode = true;

    public StateSwitchingButton(int x, int y, int width, int height, int u, int v, PressAction onPress, Identifier texture) {
        super(x, y, width, height, u, v, Text.literal(""), onPress);
        this.texture = texture;
    }

    public void addNullState() {
        if (!hasNullState) {
            states.addFirst(null);
            hasNullState = true;
            maxState++;
        }
    }

    public void removeNullState() {
        if (hasNullState) {
            states.removeFirst();
            hasNullState = false;
            maxState--;
        }
    }

    public void addState(T state, int u, int v, int height, int width, int horizontalPadding, int verticalPadding) {
        states.add(new ButtonState(state, u, v, width, height, horizontalPadding, verticalPadding));
        maxState++;
    }

    public Optional<T> getState() {
        ButtonState currentState = states.get(cursor);
        if (currentState != null) {
            return Optional.of(currentState.value());
        }
        return Optional.empty();
    }

    @Override
    protected boolean isValidClickButton(int button) {
        incrementMode = button == 0;
        return button == 0 || button == 1;
    }

    @Override
    public void onPress() {
        if (incrementMode) {
            incrementCursor();
        } else {
            decrementCursor();
        }
        super.onPress();
    }

    private void incrementCursor() {
        cursor++;
        if (cursor >= maxState) {
            cursor = 0;
        }
    }

    private void decrementCursor() {
        cursor--;
        if (cursor < 0) {
            cursor = maxState - 1;
        }
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        if (maxState > 0) {
            ButtonState currentState = states.get(cursor);
            if (currentState != null) {
                currentState.draw(context, this.getX(), this.getY());
            }
        }
    }

    private class ButtonState {
        private final T value;
        private final int u, v, width, height, horizontalPadding, verticalPadding;

        ButtonState(T value, int u, int v, int width, int height, int horizontalPadding, int verticalPadding) {
            this.value = value;
            this.u = u;
            this.v = v;
            this.width = width;
            this.height = height;
            this.horizontalPadding = horizontalPadding;
            this.verticalPadding = verticalPadding;
        }

        public T value() { return value; }
        public void draw(DrawContext context, int x, int y) {
            context.drawTexture(texture, x + horizontalPadding, y + verticalPadding, u, v, width, height);
        }
    }
}
