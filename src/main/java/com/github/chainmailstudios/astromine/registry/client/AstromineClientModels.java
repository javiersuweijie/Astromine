package com.github.chainmailstudios.astromine.registry.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BuiltinBakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import com.mojang.datafixers.util.Pair;

import com.github.chainmailstudios.astromine.AstromineCommon;
import com.github.chainmailstudios.astromine.client.model.RocketEntityModel;
import com.github.chainmailstudios.astromine.client.render.entity.RocketEntityRenderer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class AstromineClientModels {
	private static final Lazy<ModelTransformation> ITEM_HANDHELD = new Lazy<>(() -> {
		try {
			Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier("minecraft:models/item/handheld.json"));
			return JsonUnbakedModel.deserialize(new BufferedReader(new InputStreamReader(resource.getInputStream()))).getTransformations();
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	});

	public static void initialize() {
		ModelIdentifier rocketItemId = new ModelIdentifier(AstromineCommon.identifier("rocket"), "inventory");
		ModelLoadingRegistry.INSTANCE.registerVariantProvider(resourceManager -> (modelIdentifier, modelProviderContext) -> {
			if (modelIdentifier.equals(rocketItemId)) {
				return new UnbakedModel() {
					@Override
					public Collection<Identifier> getModelDependencies() {
						return Collections.emptyList();
					}

					@Override
					public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
						return Collections.emptyList();
					}

					@Override
					public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
						return new BuiltinBakedModel(ITEM_HANDHELD.get(), ModelOverrideList.EMPTY, null, true);
					}
				};
			}
			return null;
		});
	}

	public static void renderRocket(RocketEntityModel rocketEntityModel, ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		matrices.push();
		if (mode == ModelTransformation.Mode.GUI) {
			matrices.translate(0.5F, 0.1F, 0F);
		}
		matrices.scale(1.0F, -1.0F, -1.0F);
		if (mode == ModelTransformation.Mode.GUI) {
			matrices.scale(0.06F, 0.06F, 0.06F);
		} else {
			matrices.scale(0.3F, 0.3F, 0.3F);
		}
		VertexConsumer vertexConsumer2 = ItemRenderer.method_29711(vertexConsumerProvider, rocketEntityModel.getLayer(RocketEntityRenderer.identifier), false, stack.hasGlint());
		rocketEntityModel.render(matrices, vertexConsumer2, i, j, 1.0F, 1.0F, 1.0F, 1.0F);
		matrices.pop();
	}
}
