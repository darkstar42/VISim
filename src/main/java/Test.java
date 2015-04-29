import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import simulation.ParticleSystem;
import simulation.Simulation;
import simulation.element.Cloth;
import simulation.element.Plane;
import simulation.element.Sphere;
import simulation.emitter.PointParticleEmitter;
import simulation.force.AirFriction;
import simulation.force.Gravity;

import java.util.UUID;

/**
 * Sample 1 - how to get started with the most simple JME 3 application.
 * Display a blue 3D cube and view from all sides by
 * moving the mouse and pressing the WASD keys.
 */
public class Test extends SimpleApplication {
    Simulation simulation;

    public static void main(String[] args) {
        Test app = new Test();
        app.start(); // start the game
    }

    @Override
    public void simpleInitApp() {
        Plane firstPlane = new Plane(UUID.randomUUID().toString(), 10);
        firstPlane.setNormal(new Vector3f(0.0f, 1.0f, -0.5f));
        Plane secondPlane = new Plane(UUID.randomUUID().toString(), 5000);
        secondPlane.setNormal(new Vector3f(0.0f, 1.0f, 0.0f));
        secondPlane.setPosition(new Vector3f(-10, -1.0f, 10));
        simulation = new Simulation();
        simulation.addForce(new Gravity());
        simulation.addForce(new AirFriction());
        //simulation.addElement(firstPlane);
        simulation.addElement(secondPlane);
        simulation.addElement(new Sphere(UUID.randomUUID().toString(), new Vector3f(1.0f, 1.0f, 1.0f), 0.1f));

        simulation.addElement(new Cloth(UUID.randomUUID().toString()));
        //simulation.addParticleSystem(new ParticleSystem(new PointParticleEmitter()));
        /*
        simulation.addParticleSystem(new ParticleSystem(new PointParticleEmitter(
                new Vector3f(-2, 2, 0),
                new Vector3f(3.0f, 0, 3.0f),
                50,
                200,
                0.4f
        )));
        */

        simulation.addParticleSystem(new ParticleSystem(new PointParticleEmitter(
                new Vector3f(0, 0, 0),
                new Vector3f(0.0f, -2.0f, 0.0f),
                10,
                500,
                0.4f
        )));

        simulation.render(getAssetManager(), getRootNode());
    }

    @Override
    public void update() {
        super.update(); // makes sure to execute AppTasks

        simulation.update();
    }
}

