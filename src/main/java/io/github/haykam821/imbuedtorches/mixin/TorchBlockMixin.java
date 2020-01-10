package io.github.haykam821.imbuedtorches.mixin;

import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.block.TorchBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(TorchBlock.class)
public abstract class TorchBlockMixin {
	public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		// Do not apply to torches that already have redstone
		if (blockState.getBlock() instanceof RedstoneTorchBlock) return ActionResult.PASS;
		if (!(playerEntity.getMainHandStack().getItem() == Items.REDSTONE || playerEntity.getOffHandStack().getItem() == Items.REDSTONE)) return ActionResult.PASS;
		
		if (blockState.contains(Properties.HORIZONTAL_FACING)) {
			Direction facing = blockState.get(Properties.HORIZONTAL_FACING);
			
			BlockState wallRedstoneState = Blocks.REDSTONE_WALL_TORCH.getDefaultState();
			BlockState directionState = wallRedstoneState.with(Properties.HORIZONTAL_FACING, facing);

			TorchBlock.replaceBlock(blockState, directionState, world, blockPos, 0);
		} else {
			BlockState redstoneState = Blocks.REDSTONE_TORCH.getDefaultState();
			TorchBlock.replaceBlock(blockState, redstoneState, world, blockPos, 0);
		}
		
		return ActionResult.SUCCESS;
	}
}