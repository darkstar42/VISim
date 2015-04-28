package simulation.element;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import simulation.force.DampedSpring;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Cloth extends Element {
    public static float SECTION_WIDTH = 0.1f;

    private Geometry geometry;

    private Particle[][] particles;

    public Cloth(String id) {
        super(id);

        init();
    }

    protected void init() {
        particles = new Particle[100][100];
        Vector3f initialSpeed = new Vector3f(0, 0, 0);

        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                particles[y][x] = new Particle(UUID.randomUUID().toString(), new Vector3f(x * SECTION_WIDTH, 10, y * SECTION_WIDTH), initialSpeed, 0.1f);
            }
        }

        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                Particle particle = particles[y][x];

                for (int i = x - 1; i <= x + 1; i++) {
                    if (i < 0 || i > 99) continue;

                    for (int j = y - 1; j <= y + 1; j++) {
                        if (j < 0 || j > 99) continue;

                        Particle otherParticle = particles[j][i];
                        if (otherParticle.getId().equals(particle.getId())) continue;

                        particle.addSpringForce(new DampedSpring(particle, otherParticle, SECTION_WIDTH));

                        particle.addStaticInteractionNeighbour(otherParticle);
                    }
                }

                if (y < 99) {
                    if (x > 0) {
                        Particle diagLeftParticle = particles[y + 1][x - 1];
                        particle.addSpringForce(new DampedSpring(particle, diagLeftParticle, (float) (SECTION_WIDTH * Math.sqrt(2.0))));
                    }

                    if (x < 99) {
                        Particle diagRightParticle = particles[y + 1][x + 1];
                        particle.addSpringForce(new DampedSpring(particle, diagRightParticle, (float) (SECTION_WIDTH * Math.sqrt(2.0))));
                    }
                }
            }
        }
    }

    @Override
    public void updateInternalForces() {
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                Particle particle = particles[y][x];

                particle.updateInternalForces();
            }
        }
    }

    @Override
    public Geometry render(AssetManager assetManager) {
        Vector3f[] vertices = getVertices();
        int[] indices = new int[2 * 99 * 99 * 3];

        for (int y = 0; y < 99; y++) {
            for (int x = 0; x < 99; x++) {
                indices[2 * 3 * 99 * y + x * 6] = (y + 1) * 100 + x;
                indices[2 * 3 * 99 * y + x * 6 + 1] = y * 100 + x;
                indices[2 * 3 * 99 * y + x * 6 + 2] = y * 100 + (x + 1);

                indices[2 * 3 * 99 * y + x * 6 + 3] = y * 100 + (x + 1);
                indices[2 * 3 * 99 * y + x * 6 + 4] = (y + 1) * 100 + (x + 1);
                indices[2 * 3 * 99 * y + x * 6 + 5] = (y + 1) * 100 + x;
            }
        }

        Vector2f[] texCoord = new Vector2f[99 * 99];

        for (int y = 0; y < 99; y++) {
            for (int x = 0; x < 99; x++) {
                texCoord[y * 99 + x] = new Vector2f(x * 0.01f, y * 0.01f);
            }
        }

        Mesh mesh = new Mesh();
        mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        mesh.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indices));
        mesh.updateBound();

        Texture texture = assetManager.loadTexture("Textures/Cloth/Monkey.jpg");

        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //material.setColor("Color", new ColorRGBA(0.0f, 0.0f, 1.0f, 1.0f));
        material.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        material.setTexture("ColorMap", texture);

        geometry = new Geometry(getId(), mesh);
        geometry.setMaterial(material);

        return geometry;
    }

    @Override
    public void draw() {
        VertexBuffer vb = geometry.getMesh().getBuffer(VertexBuffer.Type.Position);
        FloatBuffer newBuffer = BufferUtils.createFloatBuffer(getVertices());
        vb.updateData(newBuffer);
        vb.setUpdateNeeded();
    }

    private Vector3f[] getVertices() {
        Vector3f[] vertices = new Vector3f[100 * 100];
        int[] indices = new int[2 * 99 * 99 * 3];

        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                Particle particle = particles[y][x];

                vertices[100 * y + x] = particle.getPosition();
            }
        }

        return vertices;
    }

    public List<Particle> getParticles() {
        List<Particle> particles = new ArrayList<>();

        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                Particle particle = this.particles[y][x];

                particles.add(particle);
            }
        }

        return particles;
    }

    @Override
    public void update(float timestep) {
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                Particle particle = this.particles[y][x];

                particle.update(timestep);

                // TODO - remove this hack
                if (y == 0) {
                    particle.setPosition(particle.getPosition().set(x * SECTION_WIDTH, 10.0f, y * SECTION_WIDTH));
                    particle.setVelocity(particle.getVelocity().set(0, 0, 0));
                }
            }
        }
    }

    public void findCollisionCandidates(List<Element> elements) {
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                Particle particle = this.particles[y][x];

                for (Element element : elements) {
                    if (element instanceof Plane) {
                        float distance = element.getDistance(particle);

                        particle.addCollisionCandidate(element);
                    }
                }
            }
        }
    }

    @Override
    public void resetForce() {
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                Particle particle = this.particles[y][x];

                particle.resetForce();
            }
        }
    }
}
