package unknowndomain.engine.client.game;

import org.joml.Vector3d;
import unknowndomain.engine.client.EngineClient;
import unknowndomain.engine.client.input.controller.EntityCameraController;
import unknowndomain.engine.client.input.controller.EntityController;
import unknowndomain.engine.client.input.keybinding.KeyBinding;
import unknowndomain.engine.client.input.keybinding.KeyBindingManager;
import unknowndomain.engine.client.rendering.camera.FirstPersonCamera;
import unknowndomain.engine.entity.item.EntityItem;
import unknowndomain.engine.event.engine.GameTerminationEvent;
import unknowndomain.engine.game.GameDefinition;
import unknowndomain.engine.game.GameServerFullAsync;
import unknowndomain.engine.item.ItemStack;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.player.Player;
import unknowndomain.engine.world.World;
import unknowndomain.engine.world.WorldCommon;
import unknowndomain.game.DefaultGameMode;
import unknowndomain.game.init.Blocks;
import unknowndomain.game.init.Items;

import javax.annotation.Nonnull;
import java.util.Random;

public class GameClientStandalone extends GameServerFullAsync implements GameClient {

    private final EngineClient engineClient;
    private final Player player;

    private KeyBindingManager keyBindingManager;
    private EntityController entityController;

    private boolean stopped = false;

    public GameClientStandalone(EngineClient engine, GameDefinition definition, Player player) {
        super(engine, definition);
        this.engineClient = engine;
        this.player = player;
    }

    @Override
    public EngineClient getEngine() {
        return engineClient;
    }

    /**
     * Get player client
     */
    @Nonnull
    @Override
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the client world
     */
    @Nonnull
    @Override
    public World getWorld() {
        return player.getControlledEntity().getWorld();
    }

    @Override
    public EntityController getEntityController() {
        return entityController;
    }

    @Override
    public void setEntityController(EntityController controller) {
        this.entityController = controller;
    }

    public KeyBindingManager getKeyBindingManager() {
        return keyBindingManager;
    }

    @Override
    protected void constructStage() {
        super.constructStage();

        // TODO: Move it
        eventBus.register(new DefaultGameMode());

//        player = new PlayerImpl(new Profile(UUID.randomUUID(), 12));
    }

    @Override
    protected void registerStage() {
        super.registerStage();

        var renderContext = engineClient.getRenderContext();
        var window = renderContext.getWindow();

        logger.info("Loading Client-only stuff!");

        logger.info("Initializing key binding!");
        keyBindingManager = new KeyBindingManager(this, registryManager.getRegistry(KeyBinding.class));
        keyBindingManager.reload();
        window.addKeyCallback(keyBindingManager::handleKey);
        window.addMouseCallback(keyBindingManager::handleMouse);

        renderContext.setCamera(new FirstPersonCamera(player));
    }

    @Override
    protected void resourceStage() {
        super.resourceStage();

        engineClient.getAssetManager().reload();
    }

    @Override
    protected void finishStage() {
        logger.info("Finishing Game Initialization!");

        // TODO: Remove it
        spawnWorld(null);
        var world = (WorldCommon) getWorld("default");
        world.playerJoin(player);
        player.getControlledEntity().getPosition().set(0, 5, 0);

        entityController = new EntityCameraController(player);
        engineClient.getRenderContext().getWindow().addCursorCallback((window, xpos, ypos) -> {
            if (window.getCursor().isHiddenCursor()) {
                entityController.handleCursorMove(xpos, ypos);
            }
        });

        super.finishStage();
        logger.info("Game Ready!");

        // TODO: Remove it
        Random random = new Random();
        for (int x = -16; x < 16; x++) {
            for (int z = -16; z < 16; z++) {
                for (int top = 3, y = top; y >= 0; y--) {
                    world.setBlock(BlockPos.of(x, y, z), y == top ? Blocks.GRASS : Blocks.DIRT, null);
                }
            }
        }

        world.spawnEntity(new EntityItem(world.getEntities().size(), world, new Vector3d(0, 5, 0), new ItemStack(Items.DIRT)));
//        a = Platform.getEngineClient().getSoundManager().createSoundSource("test sound").position(25,5,0).gain(1.0f).speed(dir);
//        a.setLoop(true);
//        a.assignSound(sound);
//        a.play();
    }

    public void clientTick() {
        if (isTerminated()) {
            tryTerminate();
            return;
        }
        keyBindingManager.tick();
        // TODO upload particle physics here
    }

    @Override
    protected void tryTerminate() {
        logger.info("Game terminating!");
        engine.getEventBus().post(new GameTerminationEvent.Pre(this));
        super.tryTerminate();
        engine.getEventBus().post(new GameTerminationEvent.Post(this));
        logger.info("Game terminated.");
    }
}