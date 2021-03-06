package cn.nukkit.network.protocol;

public class GUIDataPickItemPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.GUI_DATA_PICK_ITEM_PACKET;

    public int hotbarSlot;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.hotbarSlot = this.getLInt();
    }

    @Override
    public void encode() {
    	this.reset();
        this.putLInt(this.hotbarSlot);
    }
}
