/**
 * Copyright (c) SpaceToad, 2011
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package net.minecraftforge.liquids;

import net.minecraft.client.renderer.RenderEngine;
import net.minecraftforge.client.ForgeHooksClient;
import cpw.mods.fml.client.FMLTextureFX;

public class TextureLiquidsFX extends FMLTextureFX {

  private final int redMin, redMax, greenMin, greenMax, blueMin, blueMax;
	private final String texture;

	public TextureLiquidsFX(int redMin, int redMax, int greenMin, int greenMax, int blueMin, int blueMax, int spriteIndex, String texture) {
		super(spriteIndex);

		this.redMin = redMin;
		this.redMax = redMax;
		this.greenMin = greenMin;
		this.greenMax = greenMax;
		this.blueMin = blueMin;
		this.blueMax = blueMax;
		this.texture = texture;
		setup();
	}

	@Override
	public void setup() {
		super.setup();

		red = new float[tileSizeSquare];
		green = new float[tileSizeSquare];
		blue = new float[tileSizeSquare];
		alpha = new float[tileSizeSquare];
	}

	@Override
	public void bindImage(RenderEngine renderengine) {
		ForgeHooksClient.bindTexture(texture, 0);
	}

	@Override
	public void onTick() {

		for (int i = 0; i < tileSizeBase; ++i) {
			for (int j = 0; j < tileSizeBase; ++j) {
				float var3 = 0.0F;

				for (int k = i - 1; k <= i + 1; ++k) {
					int r = k & tileSizeMask;
					int g = j & tileSizeMask;
					var3 += this.red[r + g * tileSizeBase];
				}

				this.green[i + j * tileSizeBase] = var3 / 3.3F + this.blue[i + j * tileSizeBase] * 0.8F;
			}
		}

		for (int i = 0; i < tileSizeBase; ++i) {
			for (int j = 0; j < tileSizeBase; ++j) {
				this.blue[i + j * tileSizeBase] += this.alpha[i + j * tileSizeBase] * 0.05F;

				if (this.blue[i + j * tileSizeBase] < 0.0F) {
					this.blue[i + j * tileSizeBase] = 0.0F;
				}

				this.alpha[i + j * tileSizeBase] -= 0.1F;

				if (Math.random() < 0.05D) {
					this.alpha[i + j * tileSizeBase] = 0.5F;
				}
			}
		}

		float af[] = green;
		green = red;
		red = af;
		for (int i1 = 0; i1 < tileSizeSquare; i1++) {
			float f1 = red[i1];
			if (f1 > 1.0F) {
				f1 = 1.0F;
			}
			if (f1 < 0.0F) {
				f1 = 0.0F;
			}
			float f2 = f1 * f1;
			int r = (int) (redMin + f2 * (redMax - redMin));
			int g = (int) (greenMin + f2 * (greenMax - greenMin));
			int b = (int) (blueMin + f2 * (blueMax - blueMin));
			if (anaglyphEnabled) {
				int i3 = (r * 30 + g * 59 + b * 11) / 100;
				int j3 = (r * 30 + g * 70) / 100;
				int k3 = (r * 30 + b * 70) / 100;
				r = i3;
				g = j3;
				b = k3;
			}

			imageData[i1 * 4 + 0] = (byte) r;
			imageData[i1 * 4 + 1] = (byte) g;
			imageData[i1 * 4 + 2] = (byte) b;
			imageData[i1 * 4 + 3] = (byte) 255;
		}

	}

	protected float red[];
	protected float green[];
	protected float blue[];
	protected float alpha[];
}
