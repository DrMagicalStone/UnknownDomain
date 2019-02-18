package unknowndomain.engine.client.rendering.camera;

import org.joml.*;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.player.Player;

import java.lang.Math;

public class FirstPersonCamera implements Camera {

    private final Player player;

    private final Vector3f position = new Vector3f();
    private final Vector3f lookAt = new Vector3f();
    private final Vector3f frontVector = new Vector3f();
    private final Matrix4f viewMatrix = new Matrix4f();

    public FirstPersonCamera(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void update(float partial) {
        Entity entity = getPlayer().getControlledEntity();
        Vector3d position = entity.getPosition();
        Vector3f motion = entity.getMotion();
        Vector3f rotation = entity.getRotation();
        this.position.set((float) position.x + motion.x * partial, (float) position.y + motion.y * partial, (float) position.z + motion.z * partial);
        this.frontVector.set((float) (Math.cos(Math.toRadians(rotation.y)) * Math.cos(Math.toRadians(rotation.x))),
                (float) Math.sin(Math.toRadians(rotation.y)), (float) (Math.cos(Math.toRadians(rotation.y)) * Math.sin(Math.toRadians(rotation.x)))).normalize();
        this.lookAt.set(this.position).add(this.frontVector);
        viewMatrix.lookAt(this.position, this.lookAt, UP_VECTOR);
    }

    @Override
    public Vector3fc getPosition() {
        return position;
    }

    @Override
    public Vector3fc getLookAt() {
        return lookAt;
    }

    @Override
    public Vector3fc getFrontVector() {
        return frontVector;
    }

    @Override
    public Matrix4fc getViewMatrix() {
        return viewMatrix;
    }

}
