package galysso.codicraft.numismaticutils.network.requests;

import galysso.codicraft.numismaticutils.network.NetworkUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.UUID;

public record RequestAccountBalancePayload(int syncId, UUID accountId) implements CustomPayload {
    public static final Id<RequestAccountBalancePayload> ID = new Id<>(NetworkUtil.RequestAccountBalance);

    public static final PacketCodec<ByteBuf, RequestAccountBalancePayload> CODEC = PacketCodec.tuple(
        PacketCodecs.INTEGER, RequestAccountBalancePayload::syncId,
        net.minecraft.util.Uuids.PACKET_CODEC, RequestAccountBalancePayload::accountId,
        RequestAccountBalancePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {return ID;}
}
