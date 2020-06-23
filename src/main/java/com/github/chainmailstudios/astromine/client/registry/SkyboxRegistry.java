package com.github.chainmailstudios.astromine.client.registry;

import com.github.chainmailstudios.astromine.client.render.skybox.AbstractSkybox;
import com.github.chainmailstudios.astromine.common.registry.AlphaRegistry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;

public class SkyboxRegistry extends AlphaRegistry<RegistryKey<DimensionType>, AbstractSkybox> {
	public static final SkyboxRegistry INSTANCE = new SkyboxRegistry();

	private SkyboxRegistry() {
		// Locked.
	}
}
