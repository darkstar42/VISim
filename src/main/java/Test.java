import com.jme3.app.SimpleApplication;
import simulation.ParticleSystem;
import simulation.Simulation;
import simulation.emitter.PointParticleEmitter;
import simulation.force.Gravity;

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
        simulation = new Simulation();
        simulation.addForce(new Gravity());
        simulation.addParticleSystem(new ParticleSystem(new PointParticleEmitter()));
        simulation.render(getAssetManager(), getRootNode());

        /*
        ParticleEmitter particleEmitter = new PointParticleEmitter(new Vector3f(0, 0, 0));
        particleSystem = new ParticleSystem(particleEmitter);

        Geometry geometry = particleSystem.getGeometry(assetManager);

        rootNode.attachChild(geometry);
        */
    }

    @Override
    public void update() {
        super.update(); // makes sure to execute AppTasks

        simulation.update();
    }
}

