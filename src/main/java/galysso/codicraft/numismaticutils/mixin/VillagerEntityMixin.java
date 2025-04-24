package galysso.codicraft.numismaticutils.mixin;

import galysso.codicraft.numismaticutils.screen.BankerScreenHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.village.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.OptionalInt;

import static net.minecraft.entity.Entity.DEFAULT_MIN_FREEZE_DAMAGE_TICKS;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity {
    @Shadow
    @Final
    public VillagerData getVillagerData() {
        return new VillagerData(null, null, DEFAULT_MIN_FREEZE_DAMAGE_TICKS);
    }

    @Shadow
    @Final
    private void prepareOffersFor(PlayerEntity player) {}

    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    /** Shows repair screen if villager is a repairsmith */
    @Inject(at = @At("HEAD"), method = "beginTradeWith", cancellable = true)
    private void beginTradeWith(PlayerEntity customer, CallbackInfo info) {
        // Check if profession is repairsmith
        if (this.getVillagerData().getProfession() == VillagerProfession.LIBRARIAN) {
            this.prepareOffersFor(customer);
            this.setCustomer(customer);

            // Open screen
            OptionalInt optionalInt = customer
                    .openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, playerInventory, playerEntity) -> {
                        return new BankerScreenHandler(syncId, playerInventory, this);
                    }, this.getDisplayName()));

            // Send trade offers (only 1 that contains uses, villager xp, price multiplier)
            //TradeOfferList tradeOfferList;
            //if (optionalInt.isPresent() && !(tradeOfferList = this.getOffers()).isEmpty()) {
            //    NetworkUtil.syncTradeOffers((ServerPlayerEntity) customer, optionalInt.getAsInt(), tradeOfferList);
            //}

            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "afterUsing", cancellable = true)
    private void afterUsing(TradeOffer offer, CallbackInfo info) {
        if (this.getVillagerData().getProfession() == VillagerProfession.LIBRARIAN) {
            info.cancel();
        }
    }
}