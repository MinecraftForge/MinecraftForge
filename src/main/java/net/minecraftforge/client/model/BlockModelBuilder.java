package net.minecraftforge.client.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.client.renderer.model.BlockPartRotation;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemTransformVec3f;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("deprecation")
public class BlockModelBuilder {

	@Nullable
	protected ResourceLocation parent;
	protected final Map<String, String> textures = new HashMap<>();
	protected final Map<Perspective, ItemTransformVec3f> transforms = new EnumMap<>(Perspective.class);

	protected boolean ambientOcclusion = true;
	protected boolean gui3d = false;
	
	protected final List<BlockPart> elements = new ArrayList<>();
	
	public BlockModelBuilder parent(ResourceLocation parent) {
		this.parent = parent;
		return this;
	}
	
	public BlockModelBuilder texture(String key, String texture) {
		this.textures.put(key, texture);
		return this;
	}
	
	public TransformsBuilder transforms() {
		return this.new TransformsBuilder();
	}

	public BlockModelBuilder ao(boolean ao) {
		this.ambientOcclusion = ao;
		return this;
	}

	public BlockModelBuilder gui3d(boolean gui3d) {
		this.gui3d = gui3d;
		return this;
	}
	
	public ElementBuilder element() {
		return this.new ElementBuilder();
	}

	public JsonObject serialize() {
		JsonObject root = new JsonObject();
		if (this.parent != null) {
			root.addProperty("parent", this.parent.toString());
		}
		
		if (!this.ambientOcclusion) {
			root.addProperty("ambientocclusion", this.ambientOcclusion);
		}
		
		if (!this.transforms.isEmpty()) {
			JsonObject display = new JsonObject();
			for (Entry<Perspective, ItemTransformVec3f> e : this.transforms.entrySet()) {
				JsonObject transform = new JsonObject();
				transform.add("rotation", serializeVector3f(e.getValue().rotation));
				transform.add("translation", serializeVector3f(e.getValue().translation));
				transform.add("scale", serializeVector3f(e.getValue().scale));
				display.add(e.getKey().name, transform);
			}
			root.add("display", display);
		}
		
		if (!this.textures.isEmpty()) {
			JsonObject textures = new JsonObject();
			for (Entry<String, String> e : this.textures.entrySet()) {
				textures.addProperty(e.getKey(), e.getValue());
			}
			root.add("textures", textures);
		}
		
		if (!this.elements.isEmpty()) {
			JsonArray elements = new JsonArray();
			for (BlockPart part : this.elements) {
				JsonObject partObj = new JsonObject();
				partObj.add("from", serializeVector3f(part.positionFrom));
				partObj.add("to", serializeVector3f(part.positionTo));
				
				if (part.partRotation != null) {
					JsonObject rotation = new JsonObject();
					rotation.add("origin", serializeVector3f(part.partRotation.origin));
					rotation.addProperty("axis", part.partRotation.axis.getName());
					rotation.addProperty("angle", part.partRotation.angle);
					rotation.addProperty("rescale", part.partRotation.rescale);
					partObj.add("rotation", rotation);
				}
				
				if (!part.shade) {
					partObj.addProperty("shade", part.shade);
				}
				
				JsonObject faces = new JsonObject();
				for (Entry<Direction, BlockPartFace> e : part.mapFaces.entrySet()) {
					BlockPartFace face = e.getValue();
					JsonObject faceObj = new JsonObject();
					faceObj.addProperty("texture", face.texture);
					faceObj.add("uv", new Gson().toJsonTree(face.blockFaceUV.uvs));
					faceObj.addProperty("cullface", face.cullFace.getName());
					faceObj.addProperty("rotation", face.blockFaceUV.rotation);
					faceObj.addProperty("tintindex", face.tintIndex);
					faces.add(e.getKey().getName(), faceObj);
				}
				if (!part.mapFaces.isEmpty()) {
					partObj.add("faces", faces);
				}
				elements.add(partObj);
			}
			root.add("elements", elements);
		}
		
		return root;
	}
	
	private JsonArray serializeVector3f(Vector3f vec) {
		JsonArray ret = new JsonArray();
		ret.add(vec.getX());
		ret.add(vec.getY());
		ret.add(vec.getZ());
		return ret;
	}
	
	public class ElementBuilder {
		
		private Vector3f from = new Vector3f();
		private Vector3f to = new Vector3f(16, 16, 16);
		private final Map<Direction, BlockPartFace> faces = new EnumMap<>(Direction.class);
		private BlockPartRotation rotation;
		private boolean shade = true;
		
		public ElementBuilder from(float x, float y, float z) {
			this.from = new Vector3f(x, y, z);
			return this;
		}
		
		public ElementBuilder to(float x, float y, float z) {
			this.to = new Vector3f(x, y, z);
			return this;
		}
		
		public FaceBuilder face(Direction dir) {
			return this.new FaceBuilder(dir);
		}
		
		public ElementBuilder rotation(BlockPartRotation rotation) {
			this.rotation = rotation;
			return this;
		}
		
		public ElementBuilder shade(boolean shade) {
			this.shade = shade;
			return this;
		}
		
		public BlockModelBuilder build() {
			BlockModelBuilder.this.elements.add(new BlockPart(from, to, faces, rotation, shade));
			return BlockModelBuilder.this;
		}
		
		public class FaceBuilder {
			
			private final Direction dir;
			
			private Direction cullface;
			private int tintindex;
			private String texture;
			private float[] uvs;
			private FaceRotation rotation = FaceRotation.ZERO;
			
			FaceBuilder(Direction dir) {
				this.dir = dir;
			}
			
			public FaceBuilder cullface(Direction dir) {
				this.cullface = dir;
				return this;
			}
			
			public FaceBuilder tintindex(int index) {
				this.tintindex = index;
				return this;
			}
			
			public FaceBuilder texture(String texture) {
				this.texture = texture;
				return this;
			}
			
			public FaceBuilder uvs(float u1, float v1, float u2, float v2) {
				this.uvs = new float[] { u1, v1, u2, v2 };
				return this;
			}
			
			public FaceBuilder rotation(FaceRotation rot) {
				this.rotation = rot;
				return this;
			}
			
			public ElementBuilder build() {
				ElementBuilder.this.faces.put(dir, new BlockPartFace(cullface, tintindex, texture, new BlockFaceUV(uvs, rotation.rotation)));
				return ElementBuilder.this;
			}
		}
	}
	
	public enum FaceRotation {
		ZERO(0),
		CLOCKWISE_90(90),
		UPSIDE_DOWN(180),
		COUNTERCLOCKWISE_90(270),
		;
		
		final int rotation;
		
		private FaceRotation(int rotation) {
			this.rotation = rotation;
		}
	}
	
	// Since vanilla doesn't keep the name in TransformType...
	public enum Perspective {
		
		THRIDPERSON_RIGHT("thirdperson_righthand"),
		THIRDPERSON_LEFT("thirdperson_lefthand"),
		FIRSTPERSON_RIGHT("firstperson_righthand"),
		FIRSTPERSON_LEFT("firstperson_lefthand"),
		HEAD("head"),
		GUI("gui"),
		GROUND("ground"),
		FIXED("fixed"),
		;
		
		final String name;
		
		private Perspective(String name) {
			this.name = name;
		}
	}
	
	public class TransformsBuilder {

		private final Map<Perspective, ItemTransformVec3f> transforms = new EnumMap<>(Perspective.class);
		
		public TransformVecBuilder transform(Perspective type) {
			return this.new TransformVecBuilder(type);
		}

		public BlockModelBuilder build() {
			BlockModelBuilder.this.transforms.putAll(this.transforms);
			return BlockModelBuilder.this;
		}
		
		public class TransformVecBuilder {
			
			private final Perspective type;
			
			private Vector3f rotation = new Vector3f();
			private Vector3f translation = new Vector3f();
			private Vector3f scale = new Vector3f();
			
			TransformVecBuilder(Perspective type) {
				this.type = type;
			}
			
			public TransformVecBuilder rotation(float x, float y, float z) {
				this.rotation = new Vector3f(x, y, z);
				return this;
			}
			
			public TransformVecBuilder translation(float x, float y, float z) {
				this.translation = new Vector3f(x, y, z);
				return this;
			}
			
			public TransformVecBuilder scale(float x, float y, float z) {
				this.scale = new Vector3f(x, y, z);
				return this;
			}
			
			public TransformsBuilder build() {
				TransformsBuilder.this.transforms.put(type, new ItemTransformVec3f(rotation, translation, scale));
				return TransformsBuilder.this;
			}
		}
	}
}
