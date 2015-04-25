package element;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import element.neighbor.SpatialHashing;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class ParticleSystem {
    private List<Particle> particles = new ArrayList<>();

    private ParticleEmitter particleEmitter;

    private int spawningRate = 2;

    private Geometry geometry;

    private float timestep = 0.01f;

    private long particleNumber = 0;

    private SpatialHashing neighborHelper = new SpatialHashing();

    public ParticleSystem(ParticleEmitter particleEmitter) {
        this.particleEmitter = particleEmitter;
    }

    public Particle getParticle(int index) {
        if (index < 0 || index >= getNumParticles()) return null;

        return particles.get(index);
    }

    public int getNumParticles() {
        return particles.size();
    }

    protected long getNextParticleId() {
        return particleNumber++;
    }

    public void update() {
        for (int i = 0; i < getNumParticles(); i++) {
            Particle particle = getParticle(i);

            Vector3f force = new Vector3f();

            force = force.add(0, (float) (-1.0 * particle.getMass() * 9.80665f), 0);

            Vector3f oldPosition = particle.getPosition();
            Vector3f oldVelocity = particle.getVelocity();

            Vector3f newPosition = oldPosition.add(particle.getVelocity().mult(timestep));
            Vector3f newVelocity = oldVelocity.add(force.mult(timestep));

            particle.update(newPosition, newVelocity);

            if (particle.getLifetime() != -1 && particle.getAge() > particle.getLifetime()) {
                particles.remove(i);
                i--;
            }
        }

        for (int i = 0; i < spawningRate; i++) {
            Particle particle = particleEmitter.generateParticle(getNextParticleId());
            particles.add(particle);
        }

        neighborHelper.updateNeighbors(particles);
    }

    public List<Particle> getInteractionParticles(Particle particle) {
        return null;
    }

    private Vector3f[] getVertices() {
        int particleCount = getNumParticles();

        Vector3f[] vertices=new Vector3f[particleCount];

        for (int i = 0; i < particleCount; i++) {
            Particle particle = getParticle(i);

            vertices[i] = new Vector3f(particle.getPosition());
        }

        return vertices;
    }

    public Geometry getGeometry(AssetManager assetManager) {
        if (geometry == null) {
            Mesh mesh = new Mesh();
            mesh.setMode(Mesh.Mode.Points);

            mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(getVertices()));

            mesh.updateBound();
            mesh.updateCounts();

            Geometry geo = new Geometry("particles", mesh);
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setBoolean("VertexColor", true);
            geo.setMaterial(mat);

            geometry = geo;
        }

        return geometry;
    }

    public void draw() {
        VertexBuffer vb = geometry.getMesh().getBuffer(VertexBuffer.Type.Position);
        FloatBuffer newBuffer = BufferUtils.createFloatBuffer(getVertices());
        vb.updateData(newBuffer);
        vb.setUpdateNeeded();

        float[] colorArray = new float[getNumParticles() * 4];

        int colorIndex = 0;

        for (int i = 0; i < getNumParticles(); i++) {
            Particle particle = getParticle(i);

            float ratio = 1.0f;

            if (particle.getLifetime() > 0) {
                ratio = (1.0f * particle.getAge()) / particle.getLifetime();
            }

            colorArray[colorIndex++] = ratio;
            colorArray[colorIndex++] = 1.0f - ratio;
            colorArray[colorIndex++] = 0.0f;
            colorArray[colorIndex++] = 1.0f;
        }

        geometry.getMesh().setBuffer(VertexBuffer.Type.Color, 4, colorArray);
    }
}

