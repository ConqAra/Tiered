package Andrew6rant.tiered.block;

import Andrew6rant.tiered.Tiered;
import Andrew6rant.tiered.api.ModifierUtils;
import Andrew6rant.tiered.api.PotentialAttribute;
import Andrew6rant.tiered.mixin.ServerPlayerEntityMixin;
import net.fabricmc.fabric.api.event.client.ItemTooltipCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.function.UnaryOperator;

public class ReforgingStation extends Block {

    public ReforgingStation(Settings settings) {
        super(settings.nonOpaque());
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.HORIZONTAL_FACING);
    }
    public BlockState getPlacementState(ItemPlacementContext ctx){
        return (BlockState)this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing().getOpposite());
    }
    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit){
        ItemStack stack = player.getStackInHand(hand);
        if(stack.getSubNbt(Tiered.NBT_SUBTAG_KEY) != null){
            stack.removeSubNbt(Tiered.NBT_SUBTAG_KEY);
            Identifier potentialAttributeID = ModifierUtils.getRandomAttributeIDFor(stack.getItem());
            if(potentialAttributeID != null) {
                stack.getOrCreateSubNbt(Tiered.NBT_SUBTAG_KEY).putString(Tiered.NBT_SUBTAG_DATA_KEY, potentialAttributeID.toString());
            }
            player.playSound(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
            player.getItemCooldownManager().set(stack.getItem(), 20);
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }
}
