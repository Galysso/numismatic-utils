package galysso.codicraft.numismaticutils.screen.buttons;

import galysso.codicraft.numismaticutils.screen.BankerScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class DynamicButton extends ButtonWidget {
    private int posU;
    private int posV;

    public DynamicButton(int x, int y, int width, int height, int u, int v, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.posU = u;
        this.posV = v;
    }

    public DynamicButton(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        int v = posV;
        if (!this.active)
            v = 2*height;
        else if (this.isHovered())
            v = height;

        context.drawTexture(BankerScreen.ICONS_TEXTURE, this.getX(), this.getY(), posU, v, this.width, this.height);
    }
}
