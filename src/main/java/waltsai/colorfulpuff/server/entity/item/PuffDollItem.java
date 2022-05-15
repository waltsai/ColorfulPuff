package waltsai.colorfulpuff.server.entity.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import waltsai.colorfulpuff.ModProvider;
import waltsai.colorfulpuff.client.style.EyesStyle;
import waltsai.colorfulpuff.server.entity.PuffDollEntity;

import java.util.*;

public class PuffDollItem extends Item {
    private final EntityType<PuffDollEntity> type;

    public PuffDollItem(EntityType<PuffDollEntity> type, Item.Settings settings) {
        super(settings);
        this.type = type;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (!(world instanceof ServerWorld)) {
            return ActionResult.SUCCESS;
        }
        ItemStack itemStack = context.getStack();
        BlockPos blockPos = context.getBlockPos();
        Direction direction = context.getSide();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.isOf(Blocks.SPAWNER) && world.getBlockEntity(blockPos) instanceof MobSpawnerBlockEntity) {
            return ActionResult.FAIL;
        }
        BlockPos blockPos2 = blockState.getCollisionShape(world, blockPos).isEmpty() ? blockPos : blockPos.offset(direction);
        Entity entity;
        NbtCompound compound = itemStack.getOrCreateNbt();
        if (compound.getCompound(EntityType.ENTITY_TAG_KEY).isEmpty()) {
            NbtCompound entityType = new NbtCompound();
            entityType.put("Pos", this.toNbtList(context.getHitPos().x, context.getHitPos().y, context.getHitPos().z));
            entityType.putUuid(Entity.UUID_KEY, UUID.randomUUID());
            compound.put(EntityType.ENTITY_TAG_KEY, entityType);
        } else {
            NbtCompound entityType = compound.getCompound(EntityType.ENTITY_TAG_KEY);
            entityType.put("Pos", this.toNbtList(context.getHitPos().x, context.getHitPos().y, context.getHitPos().z));
            entityType.putUuid(Entity.UUID_KEY, UUID.randomUUID());
        }

        if ((entity = ModProvider.PUFF_DOLL_ENTITY.spawnFromItemStack((ServerWorld) world, itemStack, context.getPlayer(), blockPos2, SpawnReason.SPAWN_EGG, true, !Objects.equals(blockPos, blockPos2) && direction == Direction.UP)) != null) {
            itemStack.decrement(1);
            /*
            entity.setYaw(rot.x);
            entity.setPitch(rot.y);
             */
            world.emitGameEvent((Entity) context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);
        }
        return ActionResult.CONSUME;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText("itemtooltip.colorfulpuff.puff_doll.title"));
        PuffDollEntity.ClothType clothType = PuffDollEntity.ClothType.byId(stack.getOrCreateNbt().getCompound(EntityType.ENTITY_TAG_KEY).getInt("ClothType"));
        PuffDollEntity.EyeType eyeType = PuffDollEntity.EyeType.byId(stack.getOrCreateNbt().getCompound(EntityType.ENTITY_TAG_KEY).getInt("EyeType"));
        tooltip.add(new TranslatableText("cloth.colorfulpuff.puff_doll.title").append(" ").append(new TranslatableText("cloth.colorfulpuff.puff_doll." + clothType.getName()).setStyle(clothType.getStyle())));
        tooltip.add(eyeType.getTooltip());
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        BlockHitResult hitResult = SpawnEggItem.raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
        if (((HitResult) hitResult).getType() != HitResult.Type.BLOCK) {
            return TypedActionResult.pass(itemStack);
        }
        if (!(world instanceof ServerWorld)) {
            return TypedActionResult.success(itemStack);
        }
        BlockHitResult blockHitResult = hitResult;
        BlockPos blockPos = blockHitResult.getBlockPos();
        if (!(world.getBlockState(blockPos).getBlock() instanceof FluidBlock)) {
            return TypedActionResult.pass(itemStack);
        }
        if (!world.canPlayerModifyAt(user, blockPos) || !user.canPlaceOn(blockPos, blockHitResult.getSide(), itemStack)) {
            return TypedActionResult.fail(itemStack);
        }
        EntityType<?> entityType = ModProvider.PUFF_DOLL_ENTITY;
        NbtCompound compound = itemStack.getOrCreateNbt();
        compound.getCompound(EntityType.ENTITY_TAG_KEY).put("Pos", this.toNbtList(blockHitResult.getPos().x, blockHitResult.getPos().y, blockHitResult.getPos().z));
        compound.getCompound(EntityType.ENTITY_TAG_KEY).putUuid(Entity.UUID_KEY, UUID.randomUUID());

        if (entityType.spawnFromItemStack((ServerWorld) world, itemStack, user, blockPos, SpawnReason.SPAWN_EGG, false, false) == null) {
            return TypedActionResult.pass(itemStack);
        }
        if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        world.emitGameEvent(GameEvent.ENTITY_PLACE, user);
        return TypedActionResult.consume(itemStack);
    }

    protected NbtList toNbtList(double ... values) {
        NbtList nbtList = new NbtList();
        for (double d : values) {
            nbtList.add(NbtDouble.of(d));
        }
        return nbtList;
    }

    protected NbtList toNbtList(float ... values) {
        NbtList nbtList = new NbtList();
        for (float d : values) {
            nbtList.add(NbtDouble.of(d));
        }
        return nbtList;
    }

    public Vec2f lookAt(Vec3d pos, Vec3d target) {
        double d = target.x - pos.x;
        double e = target.y - pos.y;
        double f = target.z - pos.z;
        double g = Math.sqrt(d * d + f * f);
        return new Vec2f(MathHelper.wrapDegrees((float) (MathHelper.atan2(f, d) * 57.2957763671875) - 90.0f), MathHelper.wrapDegrees((float) (-(MathHelper.atan2(e, g) * 57.2957763671875))));
    }
}

