package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockBedrock extends BlockSolid {

    public BlockBedrock() {
        this(0);
    }

    public BlockBedrock(int meta) {
        super(meta);
    }
    
    
    @Override
    public BlockColor getColor(){
    	return new BlockColor(0, 0, 0);
    }

    @Override
    public int getId() {
        return BEDROCK;
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public double getResistance() {
        return 18000000;
    }

    @Override
    public String getName() {
        return "Bedrock";
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }
}
