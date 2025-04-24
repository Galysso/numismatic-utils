package galysso.codicraft.numismaticutils.network.responses;

import galysso.codicraft.numismaticutils.network.NetworkUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.UUID;

public record AccountBalancePayload(int syncId, UUID accountId, long balance) implements CustomPayload {
    public static final Id<AccountBalancePayload> ID = new Id<>(NetworkUtil.ResponseAccountBalance);

    public static final PacketCodec<RegistryByteBuf, AccountBalancePayload> CODEC = PacketCodec.tuple(
        PacketCodecs.INTEGER, AccountBalancePayload::syncId,
        net.minecraft.util.Uuids.PACKET_CODEC, AccountBalancePayload::accountId,
        PacketCodecs.VAR_LONG, AccountBalancePayload::balance,
        AccountBalancePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
