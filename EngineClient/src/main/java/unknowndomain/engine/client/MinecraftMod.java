package unknowndomain.engine.client;

import unknowndomain.engine.GameContext;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockBuilder;
import unknowndomain.engine.block.BlockPrototype;
import unknowndomain.engine.client.model.GLMesh;
import unknowndomain.engine.client.model.MinecraftModelFactory;
import unknowndomain.engine.client.rendering.RendererDebug;
import unknowndomain.engine.client.rendering.gui.RendererGui;
import unknowndomain.engine.client.resource.Resource;
import unknowndomain.engine.client.resource.ResourcePath;
import unknowndomain.engine.client.shader.Shader;
import unknowndomain.engine.client.shader.ShaderType;
import unknowndomain.engine.client.texture.GLTexture;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.event.Listener;
import unknowndomain.engine.event.registry.ClientRegistryEvent;
import unknowndomain.engine.event.registry.GameReadyEvent;
import unknowndomain.engine.event.registry.RegisterEvent;
import unknowndomain.engine.event.registry.ResourceSetupEvent;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.item.ItemBuilder;
import unknowndomain.engine.item.ItemPrototype;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.world.World;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MinecraftMod {
    private GLTexture textureMap;
    private GLMesh[] meshRegistry;

    @Listener
    public void registerEvent(RegisterEvent event) {
        Block stone = BlockBuilder.create("stone").build(),
                grass = BlockBuilder.create("grass_normal").build();

        event.getRegistry().register(BlockBuilder.create("air").setNoCollision().build());
        event.getRegistry().register(stone);
        event.getRegistry().register(grass);

        event.getRegistry().register(createPlace(stone));
        event.getRegistry().register(createPlace(grass));
    }

    @Listener
    public void clientRegisterEvent(ClientRegistryEvent event) {
        event.registerRenderer((context, manager) ->
                new RendererDebug(
                        Shader.create(manager.load(new ResourcePath("", "unknowndomain/shader/common.vert")).cache(), ShaderType.VERTEX_SHADER),
                        Shader.create(manager.load(new ResourcePath("", "unknowndomain/shader/common.frag")).cache(), ShaderType.FRAGMENT_SHADER),
                        textureMap,
                        meshRegistry))
                .registerRenderer((context, manager) -> {
                    Resource resource = manager.load(new ResourcePath("", "unknowndomain/fonts/arial.ttf"));
                    byte[] cache = resource.cache();
                    return new RendererGui(
                            (ByteBuffer) ByteBuffer.allocateDirect(cache.length).put(cache).flip(),
                            Shader.create(manager.load(new ResourcePath("", "unknowndomain/shader/gui.vert")).cache(),
                                    ShaderType.VERTEX_SHADER),
                            Shader.create(manager.load(new ResourcePath("", "unknowndomain/shader/gui.frag")).cache(),
                                    ShaderType.FRAGMENT_SHADER));
                });
    }

    @Listener
    public void setupResources(ResourceSetupEvent event) {
        Registry<Block> registry = event.getContext().getRegistry().getRegistry(Block.class);
        if (registry == null) {
            return;
        }
        List<ResourcePath> pathList = new ArrayList<>();
        for (Block value : registry.getValues()) {
            if (value.getRegisteredName().equals("air"))
                continue;
            String path = "/minecraft/models/block/" + value.getRegisteredName() + ".json";
            pathList.add(new ResourcePath(path));
        }

        MinecraftModelFactory.Result feed = null;
        try {
            feed = MinecraftModelFactory.process(event.getResourceManager(), pathList);
            textureMap = feed.textureMap;
            meshRegistry = feed.meshes.stream().map(GLMesh::of).toArray(GLMesh[]::new);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Listener
    public void onGameReady(GameReadyEvent event) {
        GameContext context = event.getContext();
        Registry<Item> itemRegistry = context.getItemRegistry();
        Registry<Block> blockRegistry = context.getBlockRegistry();
        Item stone = itemRegistry.getValue("stone_placer");
//        UnknownDomain.getEngine().getController().getPlayer().getMountingEntity().getBehavior(Entity.TwoHands.class).setMainHand(stone);
//        UnknownDomain.getGame().getWorld().setBlock(new BlockPos(1, 0, 0), blockRegistry.getValue(1));
    }


    private Item createPlace(Block object) {
        class PlaceBlock implements ItemPrototype.UseBlockBehavior {
            private Block object;

            private PlaceBlock(Block object) {
                this.object = object;
            }

            @Override
            public void onUseBlockStart(World world, Entity entity, Item item, BlockPrototype.Hit hit) {
                BlockPos side = hit.face.side(hit.position);
                System.out.println("HIT: " + hit.position + " " + hit.face + " " + hit.hit + " SIDE: " + side);
                world.setBlock(side, object);
            }
        }
        return ItemBuilder.create(object.getRegisteredName() + "_placer").setUseBlockBehavior(new PlaceBlock(object))
                .build();
    }
}
