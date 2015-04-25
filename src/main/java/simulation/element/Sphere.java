package simulation.element;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;

import java.util.UUID;

public class Sphere extends Element {
    Geometry geometry;

    private float radius;

    public Sphere(String id) {
        this(id, 0.1f);
    }

    public Sphere(String id, float radius) {
        this(id, new Vector3f(0, 0, 0), radius);
    }

    public Sphere(String id, Vector3f position, float radius) {
        super(id, position, new Vector3f(0, 0, 0), 0.0f);

        this.radius = radius;
    }

    @Override
    public Geometry render(AssetManager assetManager) {
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", new ColorRGBA(0, 0.5f, 0, 0.8f));
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

        geometry = new Geometry(getId(), new com.jme3.scene.shape.Sphere(16, 16, radius));
        geometry.setMaterial(material);
        geometry.setLocalTranslation(getPosition());
        //geometry.rotate((float) (-0.5f * Math.PI), 0, 0);
        //geometry.setLocalTranslation(-1.0f * radius / 2.0f, 0, radius / 2.0f);

        return geometry;
    }

    @Override
    public void draw() {
        // TODO - update?
    }
}
