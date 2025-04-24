package galysso.codicraft.numismaticutils.network.requests;

import galysso.codicraft.numismaticutils.network.NetworkUtil;
import galysso.codicraft.numismaticutils.network.responses.PlayerInfoPayload;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record RequestAccountCreationPayload(int syncId, String accountName) implements CustomPayload {
    public static final Id<RequestAccountCreationPayload> ID = new Id<>(NetworkUtil.RequestAccountCreation);

    public static final PacketCodec<RegistryByteBuf, RequestAccountCreationPayload> CODEC = PacketCodec.tuple(
        PacketCodecs.INTEGER, RequestAccountCreationPayload::syncId,
        PacketCodecs.STRING, RequestAccountCreationPayload::accountName,
        RequestAccountCreationPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }
}
