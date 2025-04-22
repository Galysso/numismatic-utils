package galysso.codicraft.numismaticutils.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.village.Merchant;
import net.minecraft.village.SimpleMerchant;
import galysso.codicraft.numismaticutils.NumismaticUtilsMain;

public class BankerScreenHandler extends ScreenHandler {
    private final PlayerEntity player;
    public final Merchant merchant;

    public BankerScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleMerchant(playerInventory.player));
    }

    public BankerScreenHandler(int syncId, PlayerInventory playerInventory, Merchant merchant) {
        super(NumismaticUtilsMain.BANKER_SCREEN_HANDLER, syncId);
        player = playerInventory.player;
        this.merchant = merchant;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);

        this.merchant.setCustomer(null);
    }
}
