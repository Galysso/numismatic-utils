package galysso.codicraft.numismaticutils.screen.helper;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;

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
}
