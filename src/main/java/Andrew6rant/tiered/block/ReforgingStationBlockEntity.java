package Andrew6rant.tiered.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class ReforgingStationBlockEntity extends BarrelBlockEntity {
    public ReforgingStationBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.reforging_station");
    }
}
