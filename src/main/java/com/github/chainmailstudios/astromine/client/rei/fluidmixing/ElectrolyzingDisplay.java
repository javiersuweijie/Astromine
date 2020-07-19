package com.github.chainmailstudios.astromine.client.rei.fluidmixing;

import com.github.chainmailstudios.astromine.client.rei.AstromineREIPlugin;
import com.github.chainmailstudios.astromine.common.recipe.ElectrolyzingRecipe;
import com.github.chainmailstudios.astromine.common.volume.fluid.FluidVolume;
import net.minecraft.util.Identifier;

public class ElectrolyzingDisplay extends AbstractFluidRecipeDisplay {
	public ElectrolyzingDisplay(ElectrolyzingRecipe recipe) {
		super(
				recipe.getEnergyConsumed(),
				new FluidVolume(recipe.getInputFluid(), recipe.getInputAmount()),
				new FluidVolume(recipe.getFirstOutputFluid(), recipe.getFirstOutputAmount()),
				recipe.getId()
		);
	}

	@Override
	public Identifier getRecipeCategory() {
		return AstromineREIPlugin.ELECTROLYZING;
	}
}