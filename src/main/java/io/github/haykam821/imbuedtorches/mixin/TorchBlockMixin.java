package io.github.haykam821.imbuedtorches.mixin;

import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.block.TorchBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(TorchBlock.class)
public abstract class TorchBlockMixin extends Block {
	public TorchBlockMixin(Settings settings) {
		super(settings);
	}

	public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		// Do not apply to torches that already have redstone or are soul torches
		if (blockState.getBlock() instanceof RedstoneTorchBlock) return ActionResult.PASS;
		if (blockState.getBlock() == Blocks.SOUL_TORCH) return ActionResult.PASS;
		if (blockState.getBlock() == Blocks.SOUL_WALL_TORCH) return ActionResult.PASS;

		// Remove one redstone dust from the non-creative player's hand, if they have any
		ItemStack handStack = playerEntity.getStackInHand(hand);
		boolean isRedstone = handStack.getItem() == Items.REDSTONE;
		if (isRedstone || handStack.getItem() == Items.SOUL_SAND || handStack.getItem() == Items.SOUL_SOIL) {
			if (!playerEntity.isCreative()) {
				handStack.decrement(1);
			}
		} else {
			return ActionResult.PASS;
		}
		
		if (blockState.contains(Properties.HORIZONTAL_FACING)) {
			Direction facing = blockState.get(Properties.HORIZONTAL_FACING);
			
			BlockState wallRedstoneState = (isRedstone ? Blocks.REDSTONE_WALL_TORCH : Blocks.SOUL_WALL_TORCH).getDefaultState();
			BlockState directionState = wallRedstoneState.with(Properties.HORIZONTAL_FACING, facing);

			TorchBlock.replaceBlock(blockState, directionState, world, blockPos, 0);
		} else {
			BlockState redstoneState = (isRedstone ? Blocks.REDSTONE_WALL_TORCH : Blocks.SOUL_TORCH).getDefaultState();
			TorchBlock.replaceBlock(blockState, redstoneState, world, blockPos, 0);
		}
		world.updateNeighbors(blockPos, this);
		
		return ActionResult.SUCCESS;
	}
}