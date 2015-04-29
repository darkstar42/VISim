package simulation;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import simulation.element.Cloth;
import simulation.element.Element;
import simulation.element.Particle;
import simulation.element.Plane;
import simulation.force.Force;

import java.util.ArrayList;
import java.util.List;

public class Simulation {
    private List<Element> elements;
    private List<ParticleSystem> particleSystems;
    private List<Force> forces;

    private float timestep;
    private float time;

    Node rootNode;

    public Simulation() {
        this(0.01f);
    }

    public Simulation(float timestep) {
        elements = new ArrayList<>();
        particleSystems = new ArrayList<>();
        forces = new ArrayList<>();

        this.timestep = timestep;
        this.time = 0.0f;
    }

    public void update() {
        // Create particles at emitter
        for (ParticleSystem particleSystem : particleSystems) {
            particleSystem.getSimulationCallback().createParticles();
        }

        // Remove particles at sinks or when they expire in time
        for (ParticleSystem particleSystem : particleSystems) {
            particleSystem.getSimulationCallback().removeParticles();
        }

        // TODO - Inter-element collision detection & Construct neighbour list

        for (ParticleSystem particleSystem : particleSystems) {
            particleSystem.findCollisionCandidates(elements);
        }

        for (Element element : elements) {
            if (element instanceof Cloth) {
                ((Cloth) element).findCollisionCandidates(elements);
            }
        }

        // TODO - Use neighbor list to compute interaction forces by accumulation

        for (Element element : elements) {
            if (element instanceof Cloth) {
                ((Cloth) element).updateInternalForces();
            }
        }

        for (Element element : elements) {
            for (Force force : forces) {
                if (element instanceof Cloth) {
                    force.applyForce(((Cloth) element).getParticles());
                } else {
                    force.applyForce(element);
                }
            }
        }

        // Accumulate external forces (e.g. gravity)
        for (ParticleSystem particleSystem : particleSystems) {
            for (Force force : forces) {
                force.applyForce(particleSystem.getParticles());
            }
        }

        // TODO - accumulate dissipative forces (e.g. drag and viscous drag)

        // TODO - find contact sets with external boundaries (e.g. a plane)

        // TODO - handle external boundary conditions by reflecting the velocities


        // Take a timestep
        time += timestep;

        // Integrate using leap frog
        for (ParticleSystem particleSystem : particleSystems) {
            updateParticles(particleSystem.getParticles());
        }

        for (Element element : elements) {
            updateElement(element);
        }

        // TODO - if there still are overlaps in the contact set with external boundaries -> project

        // Reset the accumulated forces
        for (ParticleSystem particleSystem : particleSystems) {
            particleSystem.resetForces();
            particleSystem.resetCollisionCandidates();
        }

        for (Element element : elements) {
            element.resetForce();
        }

        draw();
    }

    public void addElement(Element element) {
        elements.add(element);
    }

    public void addParticleSystem(ParticleSystem particleSystem) {
        particleSystems.add(particleSystem);
    }

    public void addForce(Force force) {
        forces.add(force);
    }

    public void render(AssetManager assetManager, Node node) {
        this.rootNode = node;

        for (Element element : elements) {
            rootNode.attachChild(element.render(assetManager));
        }

        for (ParticleSystem particleSystem : particleSystems) {
            rootNode.attachChild(particleSystem.render(assetManager));
        }
    }

    public void draw() {
        for (ParticleSystem particleSystem : particleSystems) {
            particleSystem.draw();
        }

        for (Element element : elements) {
            element.draw();
        }
    }

    protected void updateElement(Element element) {
        element.update(timestep);
    }

    protected void updateParticles(List<Particle> particles) {
        for (Particle particle : particles) {
            updateElement(particle);
        }
    }
}
