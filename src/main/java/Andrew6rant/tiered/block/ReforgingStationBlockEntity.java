package Andrew6rant.tiered.block;

import Andrew6rant.tiered.Tiered;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class ReforgingStationBlockEntity extends BlockEntity {

    public ReforgingStationBlockEntity(BlockPos pos, BlockState state) {
        super(Tiered.REFORGING_STATION_BLOCK_ENTITY, pos, state);
    }
}
