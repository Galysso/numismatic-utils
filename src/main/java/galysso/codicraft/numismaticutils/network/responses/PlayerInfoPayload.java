package galysso.codicraft.numismaticutils.network.responses;

import galysso.codicraft.numismaticutils.network.NetworkUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.UUID;

public record PlayerInfoPayload(int syncId, UUID mainAccountId, boolean canCreateNewAccount) implements CustomPayload {
    public static final Id<PlayerInfoPayload> ID = new Id<>(NetworkUtil.ResponsePlayerInfo);

    public static final PacketCodec<RegistryByteBuf, PlayerInfoPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, PlayerInfoPayload::syncId,
            net.minecraft.util.Uuids.PACKET_CODEC, PlayerInfoPayload::mainAccountId,
            PacketCodecs.BOOL, PlayerInfoPayload::canCreateNewAccount,
            PlayerInfoPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }
}
