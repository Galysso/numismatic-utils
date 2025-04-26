package galysso.codicraft.numismaticutils.network.requests;

import galysso.codicraft.numismaticutils.network.NetworkUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record RequestPlayersListPayload(int syncId) implements CustomPayload {
    public static final Id<RequestPlayersListPayload> ID = new Id<>(NetworkUtil.RequestPlayersList);

    public static final PacketCodec<ByteBuf, RequestPlayersListPayload> CODEC = PacketCodec.tuple(
        PacketCodecs.INTEGER, RequestPlayersListPayload::syncId,
        RequestPlayersListPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {return ID;}
}
