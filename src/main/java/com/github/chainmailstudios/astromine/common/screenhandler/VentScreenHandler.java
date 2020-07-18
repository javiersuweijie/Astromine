package com.github.chainmailstudios.astromine.common.screenhandler;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;

import com.github.chainmailstudios.astromine.common.screenhandler.base.DefaultedEnergyFluidScreenHandler;
import com.github.chainmailstudios.astromine.registry.AstromineScreenHandlers;

public class VentScreenHandler extends DefaultedEnergyFluidScreenHandler {
	public VentScreenHandler(int synchronizationID, PlayerInventory playerInventory, BlockPos position) {
		super(synchronizationID, playerInventory, position);
	}

	@Override
	public ScreenHandlerType<?> getType() {
		return AstromineScreenHandlers.VENT;
	}
}
