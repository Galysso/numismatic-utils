package galysso.codicraft.numismaticutils.utils;

import com.glisco.numismaticoverhaul.item.NumismaticOverhaulItems;
import galysso.codicraft.numismaticutils.Utils.NumismaticUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import static java.lang.Math.max;

public class NumismaticDraw {
    private static final int costDigitsHorizontalOffset = 11;
    private static final int costDigitsVerticalOffset = 9;
    private static final int coinHeight = 16;
    private static final int coinWidth = 16;
    private static final int digitWidth = 6;
    private static final int maxDigitsNumber = 4;

    public static void renderBalanceHorizontal(DrawContext context, TextRenderer textRenderer, int x, int y, long balance, int digitsColor, boolean withShadow, int padding, boolean allowEmptySpaces, boolean alignedRight) {
        if (balance == 0)
            return;

        // Get coins
        NumismaticUtils.CoinsTuple coins = NumismaticUtils.convertCostToCoins(balance);

        // Initialize offset
        int horizontalOffset = 0;
        if (alignedRight) {
            horizontalOffset = 2*(coinWidth + padding) + ((maxDigitsNumber-1)*digitWidth - costDigitsHorizontalOffset);
        }

        // Initialize coins to draw
        ItemStack coinsStack;
        if (alignedRight) {
            coinsStack = new ItemStack(NumismaticOverhaulItems.BRONZE_COIN, (int) coins.bronzeCoins);
        } else {
            coinsStack = new ItemStack(NumismaticOverhaulItems.GOLD_COIN, (int) coins.goldCoins);
        }

        // Draw first coins
        renderCoins(context, textRenderer, x + horizontalOffset, y, coinsStack, digitsColor, withShadow);

        // Shift accordingly
        if (allowEmptySpaces || coinsStack.getCount() > 0) {
            if (alignedRight) {
                horizontalOffset -= coinWidth + padding;
            } else {
                horizontalOffset += coinWidth + padding;
            }
        }

        // Select second coins
        coinsStack = new ItemStack(NumismaticOverhaulItems.SILVER_COIN, (int) coins.silverCoins);

        // Draw second coins
        renderCoins(context, textRenderer, x + horizontalOffset, y, coinsStack, digitsColor, withShadow);

        // Shift accordingly
        if (allowEmptySpaces || coinsStack.getCount() > 0) {
            if (alignedRight) {
                horizontalOffset -= coinWidth + padding;
            } else {
                horizontalOffset += coinWidth + padding;
            }
        }

        // Select third coins
        if (alignedRight) {
            coinsStack = new ItemStack(NumismaticOverhaulItems.GOLD_COIN, (int) coins.goldCoins);
        } else {
            coinsStack = new ItemStack(NumismaticOverhaulItems.BRONZE_COIN, (int) coins.bronzeCoins);
        }

        // Draw third coins
        renderCoins(context, textRenderer, x + horizontalOffset, y, coinsStack, digitsColor, withShadow);
    }

    public static void renderBalanceVertical(DrawContext context, TextRenderer textRenderer, int x, int y, long balance, int digitsColor, boolean withShadow, int padding, boolean allowEmptySpaces) {
        if (balance == 0)
            return;

        // Initialize offset
        int verticalOffset = 0;

        // Get coins
        NumismaticUtils.CoinsTuple coins = NumismaticUtils.convertCostToCoins(balance);

        // Draw gold
        ItemStack coinsStack = new ItemStack(NumismaticOverhaulItems.GOLD_COIN, 1);
        context.drawItem(coinsStack, x, y);
        context.drawText(textRenderer, Text.literal(Integer.toString((int) coins.goldCoins)), x + coinWidth, y + 5, digitsColor, withShadow);

        // Shift accordingly
        if (allowEmptySpaces || coinsStack.getCount() > 0) {
            verticalOffset += coinHeight + padding;
        }

        // Draw silver
        coinsStack = new ItemStack(NumismaticOverhaulItems.SILVER_COIN, 1);
        context.drawItem(coinsStack, x, y + verticalOffset);
        context.drawText(textRenderer, Text.literal(Integer.toString((int) coins.silverCoins)), x + coinWidth, y + verticalOffset + 5, digitsColor, withShadow);

        // Shift accordingly
        if (allowEmptySpaces || coinsStack.getCount() > 0) {
            verticalOffset += coinHeight + padding;
        }

        // Draw bronze
        coinsStack = new ItemStack(NumismaticOverhaulItems.BRONZE_COIN, 1);
        context.drawItem(coinsStack, x, y + verticalOffset);
        context.drawText(textRenderer, Text.literal(Integer.toString((int) coins.bronzeCoins)), x + coinWidth, y + verticalOffset + 5, digitsColor, withShadow);
    }

    public static void renderMinimalistBalanceVertical(DrawContext context, TextRenderer textRenderer, int x, int y, long balance, int digitsColor, boolean withShadow, int padding, boolean allowEmptySpaces, boolean alignedRight) {
        if (allowEmptySpaces && balance == 0)
            return;

        // Get coins
        NumismaticUtils.CoinsTuple coins = NumismaticUtils.convertCostToCoins(balance);

        // Initialize offset
        int horizontalOffset = 0;
        if (alignedRight) {
            horizontalOffset = 2*(coinWidth + padding) + ((maxDigitsNumber-1)*digitWidth - costDigitsHorizontalOffset);
        }

        // Initialize coins to draw
        ItemStack coinsStack;
        if (alignedRight) {
            coinsStack = new ItemStack(NumismaticOverhaulItems.BRONZE_COIN, (int) coins.bronzeCoins);
        } else {
            coinsStack = new ItemStack(NumismaticOverhaulItems.GOLD_COIN, (int) coins.goldCoins);
        }

        // Draw first coins
        renderCoinsWithTextAside(context, textRenderer, x + horizontalOffset, y, coinsStack, digitsColor, withShadow);

        // Shift accordingly
        if (allowEmptySpaces || coinsStack.getCount() > 0) {
            if (alignedRight) {
                horizontalOffset -= coinWidth + padding;
            } else {
                horizontalOffset += coinWidth + padding;
            }
        }

        // Select second coins
        coinsStack = new ItemStack(NumismaticOverhaulItems.SILVER_COIN, (int) coins.silverCoins);

        // Draw second coins
        renderCoinsWithTextAside(context, textRenderer, x + horizontalOffset, y, coinsStack, digitsColor, withShadow);

        // Shift accordingly
        if (allowEmptySpaces || coinsStack.getCount() > 0) {
            if (alignedRight) {
                horizontalOffset -= coinWidth + padding;
            } else {
                horizontalOffset += coinWidth + padding;
            }
        }

        // Select third coins
        if (alignedRight) {
            coinsStack = new ItemStack(NumismaticOverhaulItems.GOLD_COIN, (int) coins.goldCoins);
        } else {
            coinsStack = new ItemStack(NumismaticOverhaulItems.BRONZE_COIN, (int) coins.bronzeCoins);
        }

        // Draw third coins
        renderCoinsWithTextAside(context, textRenderer, x + horizontalOffset, y, coinsStack, digitsColor, withShadow);
    }

    private static void renderCoins(DrawContext context, TextRenderer textRenderer, int x, int y, ItemStack coinsStack, int digitsColor, boolean withShadow) {
        int coins = coinsStack.getCount();
        if (coins >0) {
            int digitsShift = digitWidth * (NumismaticUtils.computeNumberDigits(coins) - 1);
            context.drawItem(coinsStack, x, y);
            context.getMatrices().push();
            context.getMatrices().translate(0.0, 0.0, 200.0);
            context.drawText(textRenderer, Text.literal(Integer.toString(coins)), x + costDigitsHorizontalOffset - digitsShift, y + costDigitsVerticalOffset, digitsColor, withShadow);
            context.getMatrices().pop();
        }
    }

    private static void renderCoinsWithTextAside(DrawContext context, TextRenderer textRenderer, int x, int y, ItemStack coinsStack, int digitsColor, boolean withShadow) {
        int coins = max(1, coinsStack.getCount());
        context.drawItem(coinsStack, x, y);
        context.drawText(textRenderer, Text.literal(Integer.toString(coins)), x + coinWidth, y + 5, digitsColor, withShadow);
    }
}
