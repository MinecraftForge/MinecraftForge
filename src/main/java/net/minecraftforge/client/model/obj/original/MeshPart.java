package net.minecraftforge.client.model.obj.original;

import java.util.ArrayList;
import java.util.List;

public class MeshPart {
    public String name;
    public Material material;
    public List<int[]> indices;

    public MeshPart() {
        indices = new ArrayList<int[]>();
    }

    public void addTriangleFace(int[] a, int[] b, int[] c) {
        // Degenerate quad
        indices.add(a);
        indices.add(b);
        indices.add(c);
        indices.add(c);
    }

    public void addQuadFace(int[] a, int[] b, int[] c, int[] d) {
        indices.add(a);
        indices.add(b);
        indices.add(c);
        indices.add(d);
    }
}
