package waltsai.colorfulpuff;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.ScheduleBuilder;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import waltsai.colorfulpuff.client.model.PuffEntityModel;
import waltsai.colorfulpuff.client.particle.SleepParticle;
import waltsai.colorfulpuff.client.renderer.PuffEntityRenderer;
import waltsai.colorfulpuff.server.entity.PuffEntity;
import waltsai.colorfulpuff.server.entity.ai.brain.sensor.NearestPuffDangerousLivingEntitySensor;
import waltsai.colorfulpuff.server.entity.ai.brain.sensor.PuffSpecificSensor;
import waltsai.colorfulpuff.mixin.MemoryModuleTypeInvoker;
import waltsai.colorfulpuff.mixin.SensorTypeInvoker;

import java.util.List;

public class ModProvider {
    public static final EntityType<PuffEntity> PUFF_ENTITY = EntityType.Builder.create(PuffEntity::new, SpawnGroup.MISC).setDimensions(0.6F,1.68F).maxTrackingRange(32).build("puff");
    public static Schedule PUFF_ADULT;
    public static SensorType<PuffSpecificSensor> PUFF_SPECIFIC_SENSOR;
    public static SensorType<NearestPuffDangerousLivingEntitySensor> NEAREST_DANGEROUS_ENTITIES;
    public static MemoryModuleType<List<Entity>> VISIBLE_INTERESTED_ENTITIES;
    public static MemoryModuleType<AnimalEntity> NEAREST_VISIBLE_RIDABLE_ANIMALS;

    public static final TagKey<Block> PUFF_REPELLENTS = TagKey.of(Registry.BLOCK_KEY, new Identifier("minecraft:puff_repellents"));
    public static final TagKey<Item> VIVID_PUFF_TEMPTED_ITEM = TagKey.of(Registry.ITEM_KEY, new Identifier("minecraft:vivid_puff_tempted_item"));
    public static final TagKey<Item> DIGNIFIED_PUFF_TEMPTED_ITEM = TagKey.of(Registry.ITEM_KEY, new Identifier("minecraft:dignified_puff_tempted_item"));
    public static final TagKey<Item> TIMID_PUFF_TEMPTED_ITEM = TagKey.of(Registry.ITEM_KEY, new Identifier("minecraft:timid_puff_tempted_item"));
    public static final TagKey<Item> VIVID_PUFF_TAMED_ITEM = TagKey.of(Registry.ITEM_KEY, new Identifier("minecraft:vivid_puff_tamed_item"));
    public static final TagKey<Item> DIGNIFIED_PUFF_TAMED_ITEM = TagKey.of(Registry.ITEM_KEY, new Identifier("minecraft:dignified_puff_tamed_item"));
    public static final TagKey<Item> TIMID_PUFF_TAMED_ITEM = TagKey.of(Registry.ITEM_KEY, new Identifier("minecraft:timid_puff_tamed_item"));
    public static final TagKey<Item> VIVID_PUFF_BREED_ITEM = TagKey.of(Registry.ITEM_KEY, new Identifier("minecraft:vivid_puff_breed_item"));
    public static final TagKey<Item> DIGNIFIED_PUFF_BREED_ITEM = TagKey.of(Registry.ITEM_KEY, new Identifier("minecraft:dignified_puff_breed_item"));
    public static final TagKey<Item> TIMID_PUFF_BREED_ITEM = TagKey.of(Registry.ITEM_KEY, new Identifier("minecraft:timid_puff_breed_item"));

    public static final Item PUFF_SPAWN_EGG = new SpawnEggItem(PUFF_ENTITY, -1, -1, new Item.Settings().maxCount(64).rarity(Rarity.UNCOMMON)) {
        @Override
        public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
            super.inventoryTick(stack, world, entity, slot, selected);
            stack.getOrCreateNbt().put(EntityType.ENTITY_TAG_KEY, new NbtCompound());
            stack.getOrCreateNbt().getCompound(EntityType.ENTITY_TAG_KEY).putInt("Age", 0);
            stack.getOrCreateNbt().getCompound(EntityType.ENTITY_TAG_KEY).putBoolean("CanGrow", true);
        }
    };
    public static final Item MINI_PUFF_SPAWN_EGG = new SpawnEggItem(PUFF_ENTITY, -1, -1, new Item.Settings().maxCount(64).rarity(Rarity.UNCOMMON)) {
        @Override
        public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
            super.inventoryTick(stack, world, entity, slot, selected);
            stack.getOrCreateNbt().put(EntityType.ENTITY_TAG_KEY, new NbtCompound());
            stack.getOrCreateNbt().getCompound(EntityType.ENTITY_TAG_KEY).putInt("Age", -36000);
            stack.getOrCreateNbt().getCompound(EntityType.ENTITY_TAG_KEY).putInt("ForcedAge", 0);
            stack.getOrCreateNbt().getCompound(EntityType.ENTITY_TAG_KEY).putBoolean("CanGrow", false);
        }
    };

    public static final Material PASTRY = new Material.Builder(MapColor.CLEAR).allowsMovement().build();
    private static AbstractBlock.Settings candy() {
        return AbstractBlock.Settings.of(PASTRY).mapColor(MapColor.WHITE).strength(0.5f, 0.0f).luminance((blockstate) -> 3);
    }

    private static AbstractBlock.Settings cookie() {
        return AbstractBlock.Settings.of(PASTRY).mapColor(MapColor.WHITE).strength(0.8f, 0.2f).luminance((blockstate) -> 2);
    }
    public static final Block CARAMEL_CANDY_BLOCK = new Block(candy());
    public static final Block AQUA_CANDY_BLOCK = new Block(candy());
    public static final Block LIME_CANDY_BLOCK = new Block(candy());
    public static final Block ICE_CANDY_BLOCK = new Block(candy());
    public static final Block ORANGE_CANDY_BLOCK = new Block(candy());
    public static final Block PINK_CANDY_BLOCK = new Block(candy());
    public static final Block RED_CANDY_BLOCK = new Block(candy());
    public static final Block YELLOW_CANDY_BLOCK = new Block(candy());
    public static final Block SUGAR_BLOCK = new Block(candy());
    public static final Block GOLD_COOKIE_BLOCK = new Block(cookie());
    public static final Block MINT_COOKIE_BLOCK = new Block(cookie());
    public static final Block ROSE_COOKIE_BLOCK = new Block(cookie());

    public static final DefaultParticleType SLEEP = FabricParticleTypes.simple();

    public static void register() {
        Registry.register(Registry.ENTITY_TYPE, Utils.identifier("puff"), PUFF_ENTITY);
        Registry.register(Registry.ITEM, Utils.identifier("puff_spawn_egg"), PUFF_SPAWN_EGG);
        Registry.register(Registry.ITEM, Utils.identifier("mini_puff_spawn_egg"), MINI_PUFF_SPAWN_EGG);

        PUFF_ADULT = Registry.register(Registry.SCHEDULE, "puff_adult", new ScheduleBuilder(new Schedule()).withActivity(0, Activity.IDLE).withActivity(18000, Activity.REST).build());

        PUFF_SPECIFIC_SENSOR = SensorTypeInvoker.invokeRegister("puff_specific_sensor", PuffSpecificSensor::new);
        NEAREST_DANGEROUS_ENTITIES = SensorTypeInvoker.invokeRegister("nearest_dangerous_entities", NearestPuffDangerousLivingEntitySensor::new);
        VISIBLE_INTERESTED_ENTITIES = MemoryModuleTypeInvoker.invokeRegister("visible_interested_entities");
        NEAREST_VISIBLE_RIDABLE_ANIMALS = MemoryModuleTypeInvoker.invokeRegister("nearest_visible_ridable_animal");

        Registry.register(Registry.BLOCK, Utils.identifier("caramel_candy_block"), CARAMEL_CANDY_BLOCK);
        Registry.register(Registry.BLOCK, Utils.identifier("aqua_candy_block"), AQUA_CANDY_BLOCK);
        Registry.register(Registry.BLOCK, Utils.identifier("lime_candy_block"), LIME_CANDY_BLOCK);
        Registry.register(Registry.BLOCK, Utils.identifier("ice_candy_block"), ICE_CANDY_BLOCK);
        Registry.register(Registry.BLOCK, Utils.identifier("orange_candy_block"), ORANGE_CANDY_BLOCK);
        Registry.register(Registry.BLOCK, Utils.identifier("pink_candy_block"), PINK_CANDY_BLOCK);
        Registry.register(Registry.BLOCK, Utils.identifier("red_candy_block"), RED_CANDY_BLOCK);
        Registry.register(Registry.BLOCK, Utils.identifier("yellow_candy_block"), YELLOW_CANDY_BLOCK);
        Registry.register(Registry.BLOCK, Utils.identifier("sugar_block"), SUGAR_BLOCK);
        Registry.register(Registry.BLOCK, Utils.identifier("gold_cookie_block"), GOLD_COOKIE_BLOCK);
        Registry.register(Registry.BLOCK, Utils.identifier("mint_cookie_block"), MINT_COOKIE_BLOCK);
        Registry.register(Registry.BLOCK, Utils.identifier("rose_cookie_block"), ROSE_COOKIE_BLOCK);
        Registry.register(Registry.ITEM, Utils.identifier("caramel_candy_block"), new BlockItem(CARAMEL_CANDY_BLOCK, new Item.Settings()));
        Registry.register(Registry.ITEM, Utils.identifier("aqua_candy_block"), new BlockItem(AQUA_CANDY_BLOCK, new Item.Settings()));
        Registry.register(Registry.ITEM, Utils.identifier("lime_candy_block"), new BlockItem(LIME_CANDY_BLOCK, new Item.Settings()));
        Registry.register(Registry.ITEM, Utils.identifier("ice_candy_block"), new BlockItem(ICE_CANDY_BLOCK, new Item.Settings()));
        Registry.register(Registry.ITEM, Utils.identifier("orange_candy_block"), new BlockItem(ORANGE_CANDY_BLOCK, new Item.Settings()));
        Registry.register(Registry.ITEM, Utils.identifier("pink_candy_block"), new BlockItem(PINK_CANDY_BLOCK, new Item.Settings()));
        Registry.register(Registry.ITEM, Utils.identifier("red_candy_block"), new BlockItem(RED_CANDY_BLOCK, new Item.Settings()));
        Registry.register(Registry.ITEM, Utils.identifier("yellow_candy_block"), new BlockItem(YELLOW_CANDY_BLOCK, new Item.Settings()));
        Registry.register(Registry.ITEM, Utils.identifier("sugar_block"), new BlockItem(SUGAR_BLOCK, new Item.Settings()));
        Registry.register(Registry.ITEM, Utils.identifier("gold_cookie_block"), new BlockItem(GOLD_COOKIE_BLOCK, new Item.Settings()));
        Registry.register(Registry.ITEM, Utils.identifier("mint_cookie_block"), new BlockItem(MINT_COOKIE_BLOCK, new Item.Settings()));
        Registry.register(Registry.ITEM, Utils.identifier("rose_cookie_block"), new BlockItem(ROSE_COOKIE_BLOCK, new Item.Settings()));
    }

    public static void registerClient() {
        Registry.register(Registry.PARTICLE_TYPE, Utils.identifier("sleep"), SLEEP);
        ParticleFactoryRegistry.getInstance().register(SLEEP, SleepParticle.Factory::new);
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((atlasTexture, registry) -> {
            registry.register(Utils.identifier("particle/sleep"));
        }));
    }

    public static void initialize() {
        FabricItemGroupBuilder.create(Utils.identifier("mod_item")).icon(() -> new ItemStack(PUFF_SPAWN_EGG))
                .appendItems(stacks -> {
                    stacks.add(new ItemStack(PUFF_SPAWN_EGG));
                    stacks.add(new ItemStack(MINI_PUFF_SPAWN_EGG));
                    stacks.add(new ItemStack(CARAMEL_CANDY_BLOCK));
                    stacks.add(new ItemStack(AQUA_CANDY_BLOCK));
                    stacks.add(new ItemStack(LIME_CANDY_BLOCK));
                    stacks.add(new ItemStack(ICE_CANDY_BLOCK));
                    stacks.add(new ItemStack(ORANGE_CANDY_BLOCK));
                    stacks.add(new ItemStack(PINK_CANDY_BLOCK));
                    stacks.add(new ItemStack(RED_CANDY_BLOCK));
                    stacks.add(new ItemStack(YELLOW_CANDY_BLOCK));
                    stacks.add(new ItemStack(SUGAR_BLOCK));
                    stacks.add(new ItemStack(GOLD_COOKIE_BLOCK));
                    stacks.add(new ItemStack(MINT_COOKIE_BLOCK));
                    stacks.add(new ItemStack(ROSE_COOKIE_BLOCK));
                }).build();
        FabricDefaultAttributeRegistry.register(PUFF_ENTITY, PuffEntity.createPuffAttributes());
    }

    public static void initializeClient() {
        EntityRendererRegistry.register(PUFF_ENTITY, PuffEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(PuffEntityModel.PUFF, () -> TexturedModelData.of(PuffEntityModel.getTexturedModelData(Dilation.NONE), 64, 64));
        EntityModelLayerRegistry.registerModelLayer(PuffEntityModel.PUFF_BASE_OUTER_ARMOR, () -> TexturedModelData.of(BipedEntityModel.getModelData(new Dilation(1.0f), 0.0f), 64, 32));
        EntityModelLayerRegistry.registerModelLayer(PuffEntityModel.PUFF_BASE_INNER_ARMOR, () -> TexturedModelData.of(BipedEntityModel.getModelData(new Dilation(0.5f), 0.0f), 64, 32));
    }
}
