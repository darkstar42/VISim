package simulation.element;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;

import java.util.UUID;

public class Plane extends Element {
    private Geometry geometry;

    private Vector3f normal;
    private Vector3f point;

    private int width;

    public Plane(String id) {
        this(id, 10);
    }

    public Plane(String id, int width) {
        super(id);

        this.width = width;

        normal = new Vector3f(0.0f, 1.0f, 0.0f);
        point = new Vector3f(0.0f, 0.0f, 0.0f);
    }

    @Override
    public Geometry render(AssetManager assetManager) {
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", new ColorRGBA(0.3f, 0.3f, 0.3f, 0.25f));
        //material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

        // TODO - use normal for orientation
        // TODO - use distance for translation

        geometry = new Geometry(getId(), new Quad(width, width));
        geometry.setMaterial(material);
        geometry.rotate((float) (-0.5f * Math.PI), 0, 0);
        geometry.setLocalTranslation(-1.0f * width / 2.0f, 0, width / 2.0f);

        return geometry;
    }

    @Override
    public void draw() {
        // TODO - update?
    }

    public Vector3f getNormal() {
        return normal;
    }

    public float getDistance(Vector3f position) {
        return getNormal().dot(position.subtract(point));
    }
}
