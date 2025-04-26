package galysso.codicraft.numismaticutils.network.responses;

import galysso.codicraft.numismaticutils.network.NetworkUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.ArrayList;
import java.util.UUID;

public record PlayersListPayload(int syncId, ArrayList<UUID> playersIds, ArrayList<String> playersNames) implements CustomPayload {
    public static final Id<PlayersListPayload> ID = new Id<>(NetworkUtil.RequestPlayersList);

    public static final PacketCodec<RegistryByteBuf, PlayersListPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, PlayersListPayload::syncId,
            PacketCodecs.collection(ArrayList::new, net.minecraft.util.Uuids.PACKET_CODEC), PlayersListPayload::playersIds,
            PacketCodecs.collection(ArrayList::new, PacketCodecs.STRING), PlayersListPayload::playersNames,
            PlayersListPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {return ID;}
}
