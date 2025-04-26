package galysso.codicraft.numismaticutils.screen.helper;

import galysso.codicraft.numismaticutils.utils.BankerUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;

import static galysso.codicraft.numismaticutils.screen.BankerScreen.ICONS_TEXTURE;

public class DrawTools {
    public static void fillGradientHorizontal(DrawContext context, int x1, int y1, int x2, int y2, int colorLeft, int colorRight) {
        VertexConsumer vertexConsumer = context.getVertexConsumers().getBuffer(RenderLayer.getGui());
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();

        vertexConsumer.vertex(matrix, x1, y1, 0).color(colorLeft);
        vertexConsumer.vertex(matrix, x1, y2, 0).color(colorLeft);
        vertexConsumer.vertex(matrix, x2, y2, 0).color(colorRight);
        vertexConsumer.vertex(matrix, x2, y1, 0).color(colorRight);

        context.draw(); // flush le buffer
    }

    public static void drawRightTypeIcon(DrawContext context, int x, int y, BankerUtils.RIGHT_TYPE rightType) {
        int u = 0;
        switch (rightType) {
            case BankerUtils.RIGHT_TYPE.OWNER -> u = 0;
            case BankerUtils.RIGHT_TYPE.CO_OWNER -> u = 10;
            case BankerUtils.RIGHT_TYPE.BENEFICIARY -> u = 20;
            case BankerUtils.RIGHT_TYPE.CONTRIBUTOR -> u = 30;
            case BankerUtils.RIGHT_TYPE.READ_ONLY -> u = 40;
        }
        context.drawTexture(ICONS_TEXTURE, x, y, u, 0, 10, 8);
    }
}
