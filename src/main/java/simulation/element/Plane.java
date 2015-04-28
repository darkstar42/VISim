package simulation.element;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;

public class Plane extends Element {
    private Geometry geometry;

    private Vector3f normal;

    private int width;

    public Plane(String id) {
        this(id, 10);
    }

    public Plane(String id, int width) {
        super(id, new Vector3f(-1.0f * width / 2.0f, 0, width / 2.0f), new Vector3f(0, 0, 0), 0.0f);

        this.width = width;

        normal = new Vector3f(0.0f, 1.0f, 0.0f);
    }

    @Override
    public Geometry render(AssetManager assetManager) {
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", new ColorRGBA(0.3f, 0.3f, 0.3f, 0.25f));
        //material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

        // TODO - use normal for orientation
        /*
        Quaternion quaternion = new Quaternion();

        Matrix3f rotationMatrix = new Matrix3f();
        rotationMatrix.setColumn(0, new Vector3f(0, 0, 0));
        rotationMatrix.setColumn(0, new Vector3f(0, 0, 0));
        rotationMatrix.setColumn(0, new Vector3f(0, 0, 0));
        */

        geometry = new Geometry(getId(), new Quad(width, width));
        geometry.setMaterial(material);
        //geometry.rotate((float) (-0.5f * Math.PI), 0, 0);
        geometry.setLocalTranslation(getPosition());

        Vector3f currentNormal = new Vector3f(0, 0, 1);
        Vector3f rotationAxis = currentNormal.cross(normal);
        float rotationAngle = (float) Math.acos(currentNormal.dot(normal) / (currentNormal.length() * normal.length()));

        geometry.setLocalRotation(new Quaternion().fromAngleAxis(rotationAngle, rotationAxis));

        return geometry;
    }

    @Override
    public void draw() {
        // TODO - update?
    }

    public Vector3f getNormal() {
        return normal;
    }

    public void setNormal(Vector3f normal) {
        this.normal = normal;
    }

    public float getDistance(Vector3f position) {
        return getNormal().dot(position.subtract(getPosition()));
    }

    public float getWidth() {
        return width;
    }

    public Vector3f getCollisionPoint(Particle particle) {
        float time = intersect(particle);

        Vector3f collisionPoint = new Vector3f();
        collisionPoint.x = particle.getPosition().x + particle.getVelocity().x * time;
        collisionPoint.x = particle.getPosition().y + particle.getVelocity().y * time;
        collisionPoint.x = particle.getPosition().z + particle.getVelocity().z * time;

        return collisionPoint.add(getPosition());
    }

    protected float intersect(Particle particle) {
        double a = getNormal().dot(getPosition().subtract(particle.getPosition()));
        double b = getNormal().dot(particle.getVelocity());

        return (float) (a / b);


        /*
        double D = getPosition().dot(getNormal());

        double numer = getNormal().dot(particle.getPosition()) + D;
        double denom = getNormal().dot(particle.getVelocity());

        return (float) (-1.0 * (numer / denom));
        */
    }
}
