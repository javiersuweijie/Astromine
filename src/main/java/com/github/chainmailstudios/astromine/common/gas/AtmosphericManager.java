package com.github.chainmailstudios.astromine.common.gas;

import com.github.chainmailstudios.astromine.common.fraction.Fraction;
import com.github.chainmailstudios.astromine.common.volume.fluid.FluidVolume;
import com.github.chainmailstudios.astromine.registry.AstromineFluids;
import com.google.common.collect.Lists;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AtmosphericManager {
	private static final ExecutorService pool = Executors.newCachedThreadPool();

	private static final Map<BlockView, Map<BlockPos, FluidVolume>> LEVELS = new ConcurrentHashMap<>();

	public static void add(BlockView world, BlockPos position, FluidVolume fluidVolume) {
		LEVELS.computeIfAbsent(world, (key) -> new ConcurrentHashMap<>());

		LEVELS.get(world).put(position, fluidVolume);
	}

	public static void remove(BlockView world, BlockPos position) {
		LEVELS.computeIfAbsent(world, (key) -> new ConcurrentHashMap<>());

		LEVELS.get(world).remove(position);
	}

	public static FluidVolume get(BlockView world, BlockPos position) {
		LEVELS.computeIfAbsent(world, (key) -> new ConcurrentHashMap<>());

		if (world instanceof World) {
			RegistryKey<DimensionType> key = ((World) world).getDimensionRegistryKey();
			boolean isVanilla = (key == DimensionType.OVERWORLD_REGISTRY_KEY || key == DimensionType.OVERWORLD_CAVES_REGISTRY_KEY || key == DimensionType.THE_NETHER_REGISTRY_KEY || key == DimensionType.THE_END_REGISTRY_KEY);

			if (isVanilla) {
				return new FluidVolume(AstromineFluids.OXYGEN, Fraction.BUCKET);
			} else {
				return LEVELS.get(world).getOrDefault(position, new FluidVolume(Fluids.EMPTY, Fraction.BUCKET));
			}
		} else {
			return LEVELS.get(world).getOrDefault(position, new FluidVolume(Fluids.EMPTY, Fraction.BUCKET));
		}
	}

	public static void simulate(BlockView world) {
		pool.execute(() -> {
			List<Direction> directions = Lists.newArrayList(Direction.values());

			Map<BlockPos, FluidVolume> map = LEVELS.get(world);

			if (map == null) return;

			List<Map.Entry<BlockPos, FluidVolume>> list = new ArrayList<>(map.entrySet());

			list.sort((o1, o2) -> new CompareToBuilder()
					.append(o1.getKey().getX(), o2.getKey().getX())
					.append(o1.getKey().getY(), o2.getKey().getY())
					.append(o1.getKey().getZ(), o2.getKey().getZ()).build());

			for (Map.Entry<BlockPos, FluidVolume> pair : list) {
				Collections.shuffle(directions);

				BlockPos position = pair.getKey();

				FluidVolume fluidVolume = get(world, position);

				Direction direction = directions.get(((World) world).random.nextInt(6));

				BlockPos offsetPosition = position.offset(direction);

				BlockState offsetBlockState = world.getBlockState(offsetPosition);
				Block offsetBlock = offsetBlockState.getBlock();

				if (offsetBlock instanceof AirBlock) {
					FluidVolume offsetFluidVolume = get(world, offsetPosition);

					if (!offsetFluidVolume.isFull() && !fluidVolume.isEmpty() && offsetFluidVolume.getFluid() == fluidVolume.getFluid()) {
						fluidVolume.push(offsetFluidVolume, Fraction.BUCKET);
						add(world, offsetPosition, offsetFluidVolume);
					} else if (offsetFluidVolume.isEmpty() && !fluidVolume.isEmpty()) {
						offsetFluidVolume = new FluidVolume(fluidVolume.getFluid(), Fraction.EMPTY);
						fluidVolume.push(offsetFluidVolume, Fraction.BUCKET);
						add(world, offsetPosition, offsetFluidVolume);
					} else if (offsetFluidVolume.isEmpty() && fluidVolume.isEmpty()) {
						AtmosphericManager.remove(world, offsetPosition);
						AtmosphericManager.remove(world, position);
					}
				} else {
					AtmosphericManager.remove(world, offsetPosition);
				}
			}
		});
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		pool.shutdown();
	}
}