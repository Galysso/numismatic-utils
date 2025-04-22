package galysso.codicraft.numismaticutils.network;

import galysso.codicraft.numismaticutils.Utils.BankerUtils;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import java.util.ArrayList;
import java.util.List;

import java.util.List;

public record AccountsListPayload(int SyncId, ArrayList<String> accountNames, ArrayList<BankerUtils.RIGHT_TYPE> accountRights, ArrayList<Integer> accountIcons) implements CustomPayload {
    public static final CustomPayload.Id<AccountsListPayload> ID = new CustomPayload.Id<>(NetworkUtil.AccountsList);

    public static final PacketCodec<RegistryByteBuf, AccountsListPayload> CODEC = PacketCodec.tuple(
        PacketCodecs.INTEGER, AccountsListPayload::SyncId,
        PacketCodecs.collection(ArrayList::new, PacketCodecs.STRING), AccountsListPayload::accountNames,
        PacketCodecs.collection(ArrayList::new, PacketCodecs.indexed(index -> BankerUtils.RIGHT_TYPE.values()[index], BankerUtils.RIGHT_TYPE::ordinal)), AccountsListPayload::accountRights,
        PacketCodecs.collection(ArrayList::new, PacketCodecs.INTEGER), AccountsListPayload::accountIcons,
        AccountsListPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
