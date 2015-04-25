package simulation.element;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;

public class Plane extends Element {
    Geometry geometry;

    private int width;

    public Plane(String id) {
        this(id, 10);
    }

    public Plane(String id, int width) {
        super(id);

        this.width = width;
    }

    @Override
    public Geometry render(AssetManager assetManager) {
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", new ColorRGBA(0.3f, 0.3f, 0.3f, 0.25f));
        //material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

        geometry = new Geometry("plane", new Quad(width, width));
        geometry.setMaterial(material);
        geometry.rotate((float) (-0.5f * Math.PI), 0, 0);
        geometry.setLocalTranslation(-1.0f * width / 2.0f, 0, width / 2.0f);

        return geometry;
    }

    @Override
    public void draw() {
        // TODO - update?
    }
}
