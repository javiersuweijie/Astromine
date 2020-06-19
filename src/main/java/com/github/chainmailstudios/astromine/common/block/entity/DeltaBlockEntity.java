package com.github.chainmailstudios.astromine.common.block.entity;

import com.github.chainmailstudios.astromine.common.volume.BaseVolume;
import com.github.chainmailstudios.astromine.common.volume.collection.AgnosticIndexedVolumeCollection;
import com.github.chainmailstudios.astromine.common.volume.energy.EnergyVolume;
import com.github.chainmailstudios.astromine.common.volume.fluid.FluidVolume;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;

public abstract class DeltaBlockEntity extends BlockEntity implements AgnosticIndexedVolumeCollection, BlockEntityClientSerializable {
	protected EnergyVolume energyVolume = new EnergyVolume();

	public DeltaBlockEntity(BlockEntityType<?> type) {
		super(type);
	}

	@Override
	public boolean contains(int volumeType) {
		return volumeType == FluidVolume.TYPE;
	}

	@Override
	public <T extends BaseVolume> T get(int volumeType) {
		return volumeType == FluidVolume.TYPE ? (T) energyVolume : null;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.put("fluid", energyVolume.toTag(new CompoundTag()));

		return super.toTag(tag);
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		this.energyVolume = EnergyVolume.fromTag(tag.getCompound("fluid"));;

		super.fromTag(state, tag);
	}

	@Override
	public CompoundTag toClientTag(CompoundTag tag) {
		return toTag(tag);
	}

	@Override
	public void fromClientTag(CompoundTag tag) {
		fromTag(null, tag);
	}
}