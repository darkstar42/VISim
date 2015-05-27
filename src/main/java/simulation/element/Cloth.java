package simulation.element;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import simulation.force.DampedSpring;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Cloth extends Element {
    public static float PARTICLE_WEIGHT = 0.2f;
    public static float SECTION_WIDTH = 0.05f;
    public static int RESOLUTION = 50;

    private Geometry geometry;

    private Particle[][] particles;

    public Cloth(String id) {
        super(id, new Vector3f(0, 3.0f, 0), new Vector3f(0, 0, 0), 0.0f);

        init();
    }

    protected void init() {
        particles = new Particle[RESOLUTION][RESOLUTION];
        Vector3f initialSpeed = new Vector3f(0, 0, 0);

        for (int y = 0; y < RESOLUTION; y++) {
            for (int x = 0; x < RESOLUTION; x++) {
                particles[y][x] = new Particle(UUID.randomUUID().toString(), getPosition().add(new Vector3f(x * SECTION_WIDTH, 0, y * SECTION_WIDTH)), initialSpeed, PARTICLE_WEIGHT);
            }
        }

        for (int y = 0; y < RESOLUTION; y++) {
            for (int x = 0; x < RESOLUTION; x++) {
                Particle particle = particles[y][x];

                for (int i = x - 1; i <= x + 1; i++) {
                    if (i < 0 || i > RESOLUTION - 1) continue;

                    for (int j = y - 1; j <= y + 1; j++) {
                        if (j < 0 || j > RESOLUTION - 1) continue;

                        Particle otherParticle = particles[j][i];
                        if (otherParticle.getId().equals(particle.getId())) continue;

                        particle.addSpringForce(new DampedSpring(particle, otherParticle, SECTION_WIDTH, 1.0f, 1000.0f));

                        particle.addStaticInteractionNeighbour(otherParticle);
                    }
                }

                // Add shearing springs
                if (y < RESOLUTION - 1) {
                    if (x > 0) {
                        Particle diagLeftParticle = particles[y + 1][x - 1];
                        particle.addSpringForce(new DampedSpring(particle, diagLeftParticle, (float) (SECTION_WIDTH * Math.sqrt(2.0)), 5f, 500.0f));
                    }

                    if (x < RESOLUTION - 1) {
                        Particle diagRightParticle = particles[y + 1][x + 1];
                        particle.addSpringForce(new DampedSpring(particle, diagRightParticle, (float) (SECTION_WIDTH * Math.sqrt(2.0)), 5f, 500.0f));
                    }
                }

                // Add bending springs
                if (y < RESOLUTION - 2) {
                    Particle upperBendingParticle = particles[y + 2][x];
                    particle.addSpringForce(new DampedSpring(particle, upperBendingParticle, 2.0f * SECTION_WIDTH, 5f, 500.0f));
                }

                if (x < RESOLUTION - 2) {
                    Particle rightBendingParticle = particles[y][x + 2];
                    particle.addSpringForce(new DampedSpring(particle, rightBendingParticle, 2.0f * SECTION_WIDTH, 5f, 500.0f));
                }
            }
        }
    }

    @Override
    public void updateInternalForces() {
        for (int x = 0; x < RESOLUTION; x++) {
            for (int y = 0; y < RESOLUTION; y++) {
                Particle particle = particles[y][x];

                particle.updateInternalForces();
            }
        }
    }

    @Override
    public Node render(AssetManager assetManager) {
        Vector3f[] vertices = getVertices();
        int[] indices = new int[2 * (RESOLUTION - 1) * (RESOLUTION - 1) * 3];

        for (int y = 0; y < RESOLUTION - 1; y++) {
            for (int x = 0; x < RESOLUTION - 1; x++) {
                indices[2 * 3 * (RESOLUTION - 1) * y + x * 6] = (y + 1) * RESOLUTION + x;
                indices[2 * 3 * (RESOLUTION - 1) * y + x * 6 + 1] = y * RESOLUTION + x;
                indices[2 * 3 * (RESOLUTION - 1) * y + x * 6 + 2] = y * RESOLUTION + (x + 1);

                indices[2 * 3 * (RESOLUTION - 1) * y + x * 6 + 3] = y * RESOLUTION + (x + 1);
                indices[2 * 3 * (RESOLUTION - 1) * y + x * 6 + 4] = (y + 1) * RESOLUTION + (x + 1);
                indices[2 * 3 * (RESOLUTION - 1) * y + x * 6 + 5] = (y + 1) * RESOLUTION + x;
            }
        }

        Vector2f[] texCoord = new Vector2f[RESOLUTION * RESOLUTION];

        for (int y = 0; y < (RESOLUTION); y++) {
            for (int x = 0; x < (RESOLUTION); x++) {
                texCoord[y * (RESOLUTION) + x
                        ] = new Vector2f(y * (1.0f / RESOLUTION), x * (1.0f / RESOLUTION));
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

        Node node = new Node("Cloth-" + getId());
        node.attachChild(geometry);

        return node;
    }

    @Override
    public void draw() {
        VertexBuffer vb = geometry.getMesh().getBuffer(VertexBuffer.Type.Position);
        FloatBuffer newBuffer = BufferUtils.createFloatBuffer(getVertices());
        vb.updateData(newBuffer);
        vb.setUpdateNeeded();
    }

    private Vector3f[] getVertices() {
        Vector3f[] vertices = new Vector3f[RESOLUTION * RESOLUTION];
        int[] indices = new int[2 * (RESOLUTION - 1) * (RESOLUTION - 1) * 3];

        for (int y = 0; y < RESOLUTION; y++) {
            for (int x = 0; x < RESOLUTION; x++) {
                Particle particle = particles[y][x];

                vertices[RESOLUTION * y + x] = particle.getPosition();
            }
        }

        return vertices;
    }

    @Override
    public List<Element> getElements() {
        List<Element> particles = new ArrayList<>();

        for (int x = 0; x < RESOLUTION; x++) {
            for (int y = 0; y < RESOLUTION; y++) {
                Particle particle = this.particles[y][x];

                particles.add(particle);
            }
        }

        return particles;
    }

    @Override
    public void update(float timestep) {
        for (int x = 0; x < RESOLUTION; x++) {
            for (int y = 0; y < RESOLUTION; y++) {
                Particle particle = this.particles[y][x];

                particle.update(timestep);

                if (y == 0) {
                    particle.setPosition(particle.getPosition().set(getPosition().add(x * SECTION_WIDTH, 0.0f, y * SECTION_WIDTH)));
                    particle.setVelocity(particle.getVelocity().set(0, 0, 0));
                }
            }
        }
    }

    public void findCollisionCandidates(List<Element> elements) {
        for (int x = 0; x < RESOLUTION; x++) {
            for (int y = 0; y < RESOLUTION; y++) {
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
        for (int x = 0; x < RESOLUTION; x++) {
            for (int y = 0; y < RESOLUTION; y++) {
                Particle particle = this.particles[y][x];

                particle.resetForce();
            }
        }
    }
}
