package galysso.codicraft.numismaticutils.network.requests;

import galysso.codicraft.numismaticutils.network.NetworkUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.UUID;

public record RequestAccountInfoPayload(int syncId, UUID accountId) implements CustomPayload {
    public static final Id<RequestAccountInfoPayload> ID = new Id<>(NetworkUtil.RequestAccountInfo);

    public static final PacketCodec<ByteBuf, RequestAccountInfoPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, RequestAccountInfoPayload::syncId,
            net.minecraft.util.Uuids.PACKET_CODEC, RequestAccountInfoPayload::accountId,
            RequestAccountInfoPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {return ID;}
}
