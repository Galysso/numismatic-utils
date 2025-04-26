package galysso.codicraft.numismaticutils.screen.widgets;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.awt.*;

public class PrettyTextField extends TextFieldWidget {
    public PrettyTextField(TextRenderer textRenderer, int x, int y, int width, int height, Text text, Text placeholderText) {
        super(textRenderer, x, y, width, height, text);
        setPlaceholder(placeholderText);
        setMaxLength(16);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (isVisible()) {
            // Draw background
            context.fill(getX(), getY(), getX() + width, getY() + height, 0xFF93836b);

            // Draw widget
            super.renderWidget(context, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean drawsBackground() {
        return false;
    }
}
