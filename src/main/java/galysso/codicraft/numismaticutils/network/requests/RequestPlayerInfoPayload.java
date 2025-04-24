package galysso.codicraft.numismaticutils.network.requests;

import galysso.codicraft.numismaticutils.network.NetworkUtil;
import galysso.codicraft.numismaticutils.network.responses.AccountBalancePayload;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record RequestPlayerInfoPayload(int syncId) implements CustomPayload {
    public static final Id<RequestPlayerInfoPayload> ID = new Id<>(NetworkUtil.RequestPlayerInfo);

    public static final PacketCodec<ByteBuf, RequestPlayerInfoPayload> CODEC = PacketCodec.tuple(
        PacketCodecs.INTEGER, RequestPlayerInfoPayload::syncId,
        RequestPlayerInfoPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {return ID;}
}
