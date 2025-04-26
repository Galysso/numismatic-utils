package galysso.codicraft.numismaticutils.network.responses;

import galysso.codicraft.numismaticutils.network.NetworkUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.UUID;

public record SetSelectedAccountPayload(int syncId, UUID accountId) implements CustomPayload {
    public static final Id<SetSelectedAccountPayload> ID = new Id<>(NetworkUtil.SendSetSelectedAccountPayload);

    public static final PacketCodec<RegistryByteBuf, SetSelectedAccountPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, SetSelectedAccountPayload::syncId,
            net.minecraft.util.Uuids.PACKET_CODEC, SetSelectedAccountPayload::accountId,
            SetSelectedAccountPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
