package simulation.element;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import org.jblas.FloatMatrix;

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
        super(id, position, new Vector3f(0, 0, 0), 0.1f);

        this.radius = radius;
    }

    @Override
    public Geometry render(AssetManager assetManager) {
        Material material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        material.setBoolean("UseMaterialColors", false);
        //material.setColor("GlowColor",ColorRGBA.White);
        material.setColor("Diffuse",ColorRGBA.Green);
        material.setColor("Specular",ColorRGBA.White);
        material.setColor("Ambient",ColorRGBA.Green);
        //material.setColor("Color", new ColorRGBA(0, 0.5f, 0, 0.8f));
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
        geometry.setLocalTranslation(getPosition());
    }

    public void update(float timestep) {
        super.update(timestep);

        for (Element element : getCollisionCandidates()) {
            if (element instanceof Plane) {
                Vector3f closestPointOnPlane = ((Plane) element).getClosestPoint(this);
                float direction = ((Plane) element).getNormal().dot(getVelocity());

                //System.out.println(direction);
                //System.out.println(((Plane) element).testCollision(this));

                if (((Plane) element).testCollision(this)) {
                    if (direction < 0.0f) {
                        Vector3f normalVelocity = ((Plane) element).getNormal().mult(((Plane) element).getNormal().dot(getVelocity()));
                        Vector3f tangentialVelocity = getVelocity().subtract(normalVelocity);

                        Vector3f newVelocity = tangentialVelocity.subtract(normalVelocity.mult(0.7f));
                        setVelocity(newVelocity);
                    }
                }
            }
        }
    }

    public float getRadius() {
        return radius;
    }

    @Override
    public FloatMatrix getInertiaTensor() {
        float val = (2.0f / 5.0f) * getMass() * getRadius() * getRadius();

        return new FloatMatrix(new float[][]{
                {val, 0.0f, 0.0f},
                {0.0f, val, 0.0f},
                {0.0f, 0.0f, val}
        });
    }

    public boolean testCollision(Sphere sphere) {
        Vector3f d = getPosition().subtract(sphere.getPosition());
        float dist = d.dot(d);
        float radiusSum = getRadius() + sphere.getRadius();

        return dist <= radiusSum * radiusSum;
    }
}
