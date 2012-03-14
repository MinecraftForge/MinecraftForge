package net.minecraft.src.core;

import net.minecraft.src.*;
import net.minecraft.src.forge.*;

public class InfiBucketIron extends ItemBucket 
	implements ITextureProvider {

	int ij;
	public InfiBucketIron(int i, int j) {
		super(i, j);
		ij = i + j;
	}

	@Override
	public String getTextureFile() {
		return "/infitools/infitems.png";
	}

}
