package galysso.codicraft.numismaticutils.network.responses;

import galysso.codicraft.numismaticutils.network.NetworkUtil;
import galysso.codicraft.numismaticutils.utils.BankerUtils;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.ArrayList;
import java.util.UUID;

public record AccountInfoPayload(int syncId, UUID accountId, ArrayList<UUID> playersId, ArrayList<BankerUtils.RIGHT_TYPE> playersRightTypes, ArrayList<String> playersNames, ArrayList<Long> playersRelativeBalance) implements CustomPayload {
    public static final Id<AccountInfoPayload> ID = new Id<>(NetworkUtil.ResponseAccountInfo);

    public static final PacketCodec<RegistryByteBuf, AccountInfoPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, AccountInfoPayload::syncId,
            net.minecraft.util.Uuids.PACKET_CODEC, AccountInfoPayload::accountId,
            PacketCodecs.collection(ArrayList::new, net.minecraft.util.Uuids.PACKET_CODEC), AccountInfoPayload::playersId,
            PacketCodecs.collection(ArrayList::new, PacketCodecs.indexed(index -> BankerUtils.RIGHT_TYPE.values()[index], BankerUtils.RIGHT_TYPE::ordinal)), AccountInfoPayload::playersRightTypes,
            PacketCodecs.collection(ArrayList::new, PacketCodecs.STRING), AccountInfoPayload::playersNames,
            PacketCodecs.collection(ArrayList::new, PacketCodecs.VAR_LONG), AccountInfoPayload::playersRelativeBalance,
            AccountInfoPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {return ID;}
}
