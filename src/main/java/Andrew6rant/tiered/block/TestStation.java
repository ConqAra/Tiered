package Andrew6rant.tiered.block;

import Andrew6rant.tiered.Tiered;
import Andrew6rant.tiered.api.ModifierUtils;
import Andrew6rant.tiered.api.PotentialAttribute;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.TextColor;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

public class TestStation extends BarrelBlock {

    public TestStation(Settings settings) {
        super(settings.nonOpaque());
    }
    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView view, BlockPos pos, ShapeContext context) {
        Direction dir = blockState.get(Properties.FACING);
        switch (dir) {
            case NORTH, SOUTH, UP, DOWN -> {
                VoxelShape shape = VoxelShapes.empty();
                shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(-0.0625, 0, 0, 1.0625, 0.0625, 1), BooleanBiFunction.OR);
                shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0.0625, 0.0625, 1, 1, 0.9375), BooleanBiFunction.OR);
                shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(-0.25, 0.8125, 0, 0.0625, 1.25, 1), BooleanBiFunction.OR);
                shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.9375, 0.8125, 0, 1.25, 1.25, 1), BooleanBiFunction.OR);
                shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.0625, 1, 0, 0.9375, 1.25, 1), BooleanBiFunction.OR);
                return shape;
            }
            case EAST, WEST -> {
                VoxelShape shape = VoxelShapes.empty();
                shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0, -0.0625, 1, 0.0625, 1.0625), BooleanBiFunction.OR);
                shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0.0625, 0.0625, 0, 0.9375, 1, 1), BooleanBiFunction.OR);
                shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0.8125, 0.9375, 1, 1.25, 1.25), BooleanBiFunction.OR);
                shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 0.8125, -0.25, 1, 1.25, 0.0625), BooleanBiFunction.OR);
                shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(0, 1, 0.0625, 1, 1.25, 0.9375), BooleanBiFunction.OR);
                return shape;
            }
        };
        return null;
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(FACING, OPEN);
    }
    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit){
        //((TooltipFadeAccessor) MinecraftClient.getInstance()).getHeldItemTooltipFade(0);
        //int heldItemTooltipFade = ((TooltipFadeAccessor) MinecraftClient.getInstance()).getHeldItemTooltipFade();
        ItemStack stack = player.getStackInHand(hand);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        else if(((hit.getPos().x - pos.getX()) > 0.2
            && (hit.getPos().x - pos.getX()) < 0.81
            && (hit.getPos().y - pos.getY()) > 0.17
            && (hit.getPos().y - pos.getY()) < 0.71
            && (hit.getPos().z - pos.getZ()) == 0.0625
            && ((blockState.get(Properties.FACING) == Direction.NORTH) || (blockState.get(Properties.FACING) == Direction.UP) || (blockState.get(Properties.FACING) == Direction.DOWN)))
            ||
            ((hit.getPos().x - pos.getX()) > 0.2
            &&(hit.getPos().x - pos.getX()) < 0.81
            && (hit.getPos().y - pos.getY()) > 0.17
            && (hit.getPos().y - pos.getY()) < 0.71
            && (hit.getPos().z - pos.getZ()) == 0.9375
            && (blockState.get(Properties.FACING) == Direction.SOUTH))
            ||
            ((hit.getPos().x - pos.getX()) == 0.9375
            && (hit.getPos().y - pos.getY()) > 0.17
            && (hit.getPos().y - pos.getY()) < 0.71
            && (hit.getPos().z - pos.getZ()) > 0.2
            && (hit.getPos().z - pos.getZ()) < 0.81
            && (blockState.get(Properties.FACING) == Direction.EAST))
            ||
            ((hit.getPos().x - pos.getX()) == 0.0625
            && (hit.getPos().y - pos.getY()) > 0.17
            && (hit.getPos().y - pos.getY()) < 0.71
            && (hit.getPos().z - pos.getZ()) > 0.2
            && (hit.getPos().z - pos.getZ()) < 0.81
            && (blockState.get(Properties.FACING) == Direction.WEST))) {
                player.openHandledScreen((BarrelBlockEntity)blockEntity);
                return ActionResult.CONSUME;
        }
        else if(stack.getSubNbt(Tiered.NBT_SUBTAG_KEY) != null && !player.getItemCooldownManager().isCoolingDown(stack.getItem())) {
            Identifier tier = new Identifier(stack.getSubNbt(Tiered.NBT_SUBTAG_KEY).getString(Tiered.NBT_SUBTAG_DATA_KEY));
            PotentialAttribute potentialAttribute = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier);
            //stack.removeSubNbt(Tiered.NBT_SUBTAG_KEY);
            //player.getItemCooldownManager().set(stack.getItem(), 15);
            Identifier potentialAttributeID = ModifierUtils.getWeightedAttributeIDFor(stack);
            if(potentialAttributeID != null) {
                stack.getOrCreateSubNbt(Tiered.NBT_SUBTAG_KEY).putString(Tiered.NBT_SUBTAG_DATA_KEY, potentialAttributeID.toString());
            }
            player.playSound(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);

            //((TooltipFadeAccessor) this).setHeldItemTooltipFade(40);
            //((TooltipFadeAccessor) MinecraftClient.getInstance()).setHeldItemTooltipFade(40);
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }
}
