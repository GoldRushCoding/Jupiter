package cn.nukkit.network.protocol;

import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class MobEquipmentPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.MOB_EQUIPMENT_PACKET;

    public long entityRuntimeId;
    public Item item;
    public int inventorySlot;
    public int hotbarSlot;
    public int windowId;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.entityRuntimeId = this.getEntityRuntimeId(); //EntityRuntimeID
        this.item = this.getSlot();
        this.inventorySlot = this.getByte();
        this.hotbarSlot = this.getByte();
        this.windowId = this.getByte();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityRuntimeId(this.entityRuntimeId); //EntityRuntimeID
        this.putSlot(this.item);
        this.putByte((byte) this.inventorySlot);
        this.putByte((byte) this.hotbarSlot);
        this.putByte((byte) this.windowId);
    }

}
