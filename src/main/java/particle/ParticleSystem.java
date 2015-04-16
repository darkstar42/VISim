package particle;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class ParticleSystem {
    private List<Particle> particles = new ArrayList<>();

    private ParticleEmitter particleEmitter;

    private int spawningRate = 100;

    private Geometry geometry;

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

    public void update() {
        for (int i = 0; i < spawningRate; i++) {
            Particle particle = particleEmitter.generateParticle();
            particles.add(particle);
        }
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

            float[] colorArray = new float[getNumParticles() * 4];

            int colorIndex = 0;

            for (int i = 0; i < getNumParticles(); i++) {
                // Red value (is increased by .2 on each next vertex here)
                colorArray[colorIndex++] = 0.1f + (.01f * i);
                // Green value (is reduced by .2 on each next vertex)
                colorArray[colorIndex++] = 0.9f - (0.01f * i);
                // Blue value (remains the same in our case)
                colorArray[colorIndex++] = 0.5f;
                // Alpha value (no transparency set here)
                colorArray[colorIndex++] = 1.0f;
            }

            mesh.setBuffer(VertexBuffer.Type.Color, 4, colorArray);

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
            // Red value (is increased by .2 on each next vertex here)
            colorArray[colorIndex++] = 0.1f + (.01f * i);
            // Green value (is reduced by .2 on each next vertex)
            colorArray[colorIndex++] = 0.9f - (0.01f * i);
            // Blue value (remains the same in our case)
            colorArray[colorIndex++] = 0.5f;
            // Alpha value (no transparency set here)
            colorArray[colorIndex++] = 1.0f;
        }

        geometry.getMesh().setBuffer(VertexBuffer.Type.Color, 4, colorArray);
    }
}

