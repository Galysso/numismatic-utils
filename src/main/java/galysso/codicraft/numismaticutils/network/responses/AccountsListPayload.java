package galysso.codicraft.numismaticutils.network.responses;

import galysso.codicraft.numismaticutils.utils.BankerUtils;
import galysso.codicraft.numismaticutils.network.NetworkUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import java.util.ArrayList;
import java.util.UUID;

public record AccountsListPayload(int syncId, ArrayList<UUID> accountsIds, ArrayList<String> accountsNames, ArrayList<BankerUtils.RIGHT_TYPE> accountsRights, ArrayList<Integer> accountsIcons) implements CustomPayload {
    public static final CustomPayload.Id<AccountsListPayload> ID = new CustomPayload.Id<>(NetworkUtil.ResponseAccountsList);

    public static final PacketCodec<RegistryByteBuf, AccountsListPayload> CODEC = PacketCodec.tuple(
        PacketCodecs.INTEGER, AccountsListPayload::syncId,
        PacketCodecs.collection(ArrayList::new, net.minecraft.util.Uuids.PACKET_CODEC), AccountsListPayload::accountsIds,
        PacketCodecs.collection(ArrayList::new, PacketCodecs.STRING), AccountsListPayload::accountsNames,
        PacketCodecs.collection(ArrayList::new, PacketCodecs.indexed(index -> BankerUtils.RIGHT_TYPE.values()[index], BankerUtils.RIGHT_TYPE::ordinal)), AccountsListPayload::accountsRights,
        PacketCodecs.collection(ArrayList::new, PacketCodecs.INTEGER), AccountsListPayload::accountsIcons,
        AccountsListPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}