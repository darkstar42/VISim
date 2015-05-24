package simulation;

import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import simulation.collisiondetection.SpatialHashing;
import simulation.element.*;
import simulation.force.Force;
import simulation.spook.CollisionPair;
import simulation.spook.GaussSeidelIterator;
import simulation.spook.SpherePlaneCollisionPair;
import simulation.spook.SphereSphereCollisionPair;

import java.util.ArrayList;
import java.util.List;

public class Simulation {
    private AssetManager assetManager;

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
            element.findCollisionCandidates(elements);
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

        SpatialHashing spatialHashing = new SpatialHashing(this);
        spatialHashing.hash();
        spatialHashing.findCollisionPairs();

        Plane plane = (Plane) elements.get(0);
        Sphere sphere0 = (Sphere) elements.get(1);
        Sphere sphere1 = (Sphere) elements.get(2);
        Sphere sphere2 = (Sphere) elements.get(3);
        Sphere sphere3 = (Sphere) elements.get(4);
        Sphere sphere4 = (Sphere) elements.get(5);
        Sphere sphere5 = (Sphere) elements.get(6);

        //sphere5.setVelocity(new Vector3f(-0.5f, 0, 0));

        /*
        List<CollisionPair> collisionPairs = new ArrayList<>();

        float sphereSpringConstant = 2000.0f;

        collisionPairs.add(new SphereSphereCollisionPair(sphere0, sphere1, timestep, sphereSpringConstant, 1));
        collisionPairs.add(new SphereSphereCollisionPair(sphere0, sphere2, timestep, sphereSpringConstant, 1));
        collisionPairs.add(new SphereSphereCollisionPair(sphere0, sphere3, timestep, sphereSpringConstant, 1));
        collisionPairs.add(new SphereSphereCollisionPair(sphere0, sphere4, timestep, sphereSpringConstant, 1));
        collisionPairs.add(new SphereSphereCollisionPair(sphere0, sphere5, timestep, sphereSpringConstant, 1));

        collisionPairs.add(new SphereSphereCollisionPair(sphere1, sphere2, timestep, sphereSpringConstant, 1));
        collisionPairs.add(new SphereSphereCollisionPair(sphere1, sphere3, timestep, sphereSpringConstant, 1));
        collisionPairs.add(new SphereSphereCollisionPair(sphere1, sphere4, timestep, sphereSpringConstant, 1));
        collisionPairs.add(new SphereSphereCollisionPair(sphere1, sphere5, timestep, sphereSpringConstant, 1));

        collisionPairs.add(new SphereSphereCollisionPair(sphere2, sphere3, timestep, sphereSpringConstant, 1));
        collisionPairs.add(new SphereSphereCollisionPair(sphere2, sphere4, timestep, sphereSpringConstant, 1));
        collisionPairs.add(new SphereSphereCollisionPair(sphere2, sphere5, timestep, sphereSpringConstant, 1));

        collisionPairs.add(new SphereSphereCollisionPair(sphere3, sphere4, timestep, sphereSpringConstant, 1));
        collisionPairs.add(new SphereSphereCollisionPair(sphere3, sphere5, timestep, sphereSpringConstant, 1));

        collisionPairs.add(new SphereSphereCollisionPair(sphere4, sphere5, timestep, sphereSpringConstant, 1));

        collisionPairs.add(new SpherePlaneCollisionPair(sphere0, plane, timestep, 20.0f, 1));
        collisionPairs.add(new SpherePlaneCollisionPair(sphere1, plane, timestep, 20.0f, 1));
        collisionPairs.add(new SpherePlaneCollisionPair(sphere2, plane, timestep, 20.0f, 1));
        collisionPairs.add(new SpherePlaneCollisionPair(sphere3, plane, timestep, 20.0f, 1));
        collisionPairs.add(new SpherePlaneCollisionPair(sphere4, plane, timestep, 20.0f, 1));
        collisionPairs.add(new SpherePlaneCollisionPair(sphere5, plane, timestep, 20.0f, 1));
        */


        GaussSeidelIterator gs = new GaussSeidelIterator(spatialHashing.getCollisionPairs(), timestep);
        gs.solve();

        /*
        Spook spook = new Spook(timestep, elements);

        spook.solve(plane, sphere);
        */

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

    public void addElement(Element element, boolean render) {
        rootNode.attachChild(element.render(assetManager));

        addElement(element);
    }

    public void addParticleSystem(ParticleSystem particleSystem) {
        particleSystems.add(particleSystem);
    }

    public void addForce(Force force) {
        forces.add(force);
    }

    public void render(AssetManager assetManager, Node node) {
        this.rootNode = node;
        this.assetManager = assetManager;

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

    public List<Element> getElements() {
        return elements;
    }

    public float getTimestep() {
        return timestep;
    }
}
