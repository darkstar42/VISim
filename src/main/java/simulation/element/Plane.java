package simulation.element;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import org.jblas.FloatMatrix;

public class Plane extends Element {
    private static float RENDER_WIDTH = 1000.0f;

    private Geometry geometry;

    private Vector3f normal;

    private float originDistance;

    public Plane(String id) {
        super(id, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), 0.0f);

        normal = new Vector3f(0.0f, 1.0f, 0.0f);
        originDistance = 0.0f;
    }

    @Override
    public Geometry render(AssetManager assetManager) {
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", new ColorRGBA(0.3f, 0.3f, 0.3f, 0.25f));
        material.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        //material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

        // TODO - use normal for orientation
        /*
        Quaternion quaternion = new Quaternion();

        Matrix3f rotationMatrix = new Matrix3f();
        rotationMatrix.setColumn(0, new Vector3f(0, 0, 0));
        rotationMatrix.setColumn(0, new Vector3f(0, 0, 0));
        rotationMatrix.setColumn(0, new Vector3f(0, 0, 0));
        */

        geometry = new Geometry(getId(), new Quad(RENDER_WIDTH, RENDER_WIDTH));
        geometry.setMaterial(material);
        //geometry.rotate((float) (-0.5f * Math.PI), 0, 0);
        // TODO
        geometry.setLocalTranslation(new Vector3f(-0.5f * RENDER_WIDTH, 0, 0.5f * RENDER_WIDTH));

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

    @Override
    public Vector3f getPosition() {
        return getNormal().mult(getOriginDistance());
    }

    @Override
    public void setPosition(Vector3f position) {
        // Nope
    }

    @Override
    public FloatMatrix getInertiaTensor() {
        return new FloatMatrix(new float[][]{
                {0.0f, 0.0f, 0.0f},
                {0.0f, 0.0f, 0.0f},
                {0.0f, 0.0f, 0.0f}
        });
    }

    public float getOriginDistance() {
        return originDistance;
    }

    public void setNormal(Vector3f normal) {
        this.normal = normal;
    }

    public float getDistance(Vector3f position) {
        return Math.abs(getNormal().dot(position) + getOriginDistance());
    }

    public float getDistance(Sphere sphere) {
        return getDistance(sphere.getPosition()) - sphere.getRadius();
    }

    public Vector3f getClosestPoint(Sphere sphere) {
        float t = (getNormal().dot(sphere.getPosition()) - getOriginDistance()) / getNormal().dot(getNormal());

        return sphere.getPosition().subtract(getNormal().mult(t));
    }

    public Vector3f getCollisionPoint(Element element) {
        float time = intersect(element);

        Vector3f collisionPoint = new Vector3f();
        collisionPoint.x = element.getPosition().x + element.getVelocity().x * time;
        collisionPoint.x = element.getPosition().y + element.getVelocity().y * time;
        collisionPoint.x = element.getPosition().z + element.getVelocity().z * time;

        return collisionPoint.add(getPosition());
    }

    protected float intersect(Element element) {
        double a = getNormal().dot(getPosition().subtract(element.getPosition()));
        double b = getNormal().dot(element.getVelocity());

        return (float) (a / b);
    }

    public boolean isBehind(Sphere sphere) {
        float dist = sphere.getPosition().dot(getNormal()) - getOriginDistance();

        return dist < -1.0f * sphere.getRadius();
    }

    public boolean testCollision(Sphere sphere) {
        float dist = sphere.getPosition().dot(getNormal()) - getOriginDistance();

        return Math.abs(dist) <= sphere.getRadius();
    }

    @Override
    public FloatMatrix getInverseMassInertiaTensorMatrix() {
        return new FloatMatrix(6, 6);
    }
}
