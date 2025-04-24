package galysso.codicraft.numismaticutils.network.requests;

import galysso.codicraft.numismaticutils.network.NetworkUtil;
import galysso.codicraft.numismaticutils.network.responses.PlayerInfoPayload;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.Optional;
import java.util.UUID;

public record RequestTransfertPayload(int syncId, Optional<UUID> originId, UUID destinationId, long value) implements CustomPayload {
    public static final Id<RequestTransfertPayload> ID = new Id<>(NetworkUtil.RequestTransfert);

    public static final PacketCodec<ByteBuf, RequestTransfertPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, RequestTransfertPayload::syncId,
            PacketCodecs.optional(net.minecraft.util.Uuids.PACKET_CODEC), RequestTransfertPayload::originId,
            net.minecraft.util.Uuids.PACKET_CODEC, RequestTransfertPayload::destinationId,
            PacketCodecs.VAR_LONG, RequestTransfertPayload::value,
            RequestTransfertPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {return ID;}
}
