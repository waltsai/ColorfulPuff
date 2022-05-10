package waltsai.colorfulpuff.server.entity.ai.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import waltsai.colorfulpuff.server.entity.AbstractPuffEntity;

public class ReactAttackTask extends Task<AbstractPuffEntity> {
    private LivingEntity attacker;
    public ReactAttackTask() {
        super(ImmutableMap.of(MemoryModuleType.ANGRY_AT, MemoryModuleState.REGISTERED, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleState.REGISTERED), 20, 60);
    }

    protected boolean shouldRun(ServerWorld serverWorld, AbstractPuffEntity mobEntity) {
        if (mobEntity.getRecentDamageSource() == null) {
            return false;
        } else if (mobEntity.isBaby()) {
            return false;
        } else {
            Entity target = mobEntity.getRecentDamageSource().getAttacker();
            if(target instanceof LivingEntity) {
                this.attacker = (LivingEntity) target;
                return mobEntity.canTarget((LivingEntity) target);
            }
            return false;
        }
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, AbstractPuffEntity entity, long time) {
        return true;
    }

    @Override
    protected void finishRunning(ServerWorld world, AbstractPuffEntity entity, long time) {
        if(this.attacker != null) {
            this.angryAt(entity, (LivingEntity) this.attacker);
        }
    }

    private void angryAt(AbstractPuffEntity entity, LivingEntity target) {
        entity.getBrain().remember(MemoryModuleType.ANGRY_AT, target.getUuid(), 600L);
        entity.getBrain().forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);

        if (target.getType() == EntityType.PLAYER && entity.world.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER)) {
            entity.getBrain().remember(MemoryModuleType.UNIVERSAL_ANGER, true, 600L);
        }
    }
}
