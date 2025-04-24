package galysso.codicraft.numismaticutils.network.requests;

import galysso.codicraft.numismaticutils.network.NetworkUtil;
import galysso.codicraft.numismaticutils.utils.BankerUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.Optional;

public record RequestAccountsListPayload(int syncId, Optional<BankerUtils.RIGHT_TYPE> rightTypeFilter, Optional<Integer> iconIdFilter) implements CustomPayload {
    public static final Id<RequestAccountsListPayload> ID = new Id<>(NetworkUtil.RequestAccountList);

    public static final PacketCodec<ByteBuf, RequestAccountsListPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, RequestAccountsListPayload::syncId,
            PacketCodecs.optional(PacketCodecs.indexed(index -> BankerUtils.RIGHT_TYPE.values()[index], BankerUtils.RIGHT_TYPE::ordinal)), RequestAccountsListPayload::rightTypeFilter,
            PacketCodecs.optional(PacketCodecs.INTEGER), RequestAccountsListPayload::iconIdFilter,
            RequestAccountsListPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {return ID;}
}
