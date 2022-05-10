package waltsai.colorfulpuff.server.entity.ai.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import waltsai.colorfulpuff.server.entity.AbstractPuffEntity;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class UpdateTargetTask extends Task<AbstractPuffEntity> {
    private final Predicate<AbstractPuffEntity> startCondition;
    private final Function<AbstractPuffEntity, Optional<? extends LivingEntity>> targetGetter;

    public UpdateTargetTask(Predicate<AbstractPuffEntity> startCondition, Function<AbstractPuffEntity, Optional<? extends LivingEntity>> targetGetter) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleState.REGISTERED));
        this.startCondition = startCondition;
        this.targetGetter = targetGetter;
    }

    public UpdateTargetTask(Function<AbstractPuffEntity, Optional<? extends LivingEntity>> targetGetter) {
        this((mobEntity) -> {
            return true;
        }, targetGetter);
    }

    protected boolean shouldRun(ServerWorld serverWorld, AbstractPuffEntity mobEntity) {
        if (!this.startCondition.test(mobEntity)) {
            return false;
        } else {
            Optional<? extends LivingEntity> optional = this.targetGetter.apply(mobEntity);
            return optional.isPresent() && mobEntity.canTarget(optional.get());
        }
    }

    protected void run(ServerWorld serverWorld, AbstractPuffEntity mobEntity, long l) {
        (this.targetGetter.apply(mobEntity)).ifPresent((livingEntity) -> {
            this.updateAttackTarget(mobEntity, livingEntity);
        });
    }

    private void updateAttackTarget(AbstractPuffEntity entity, LivingEntity target) {
        entity.getBrain().remember(MemoryModuleType.ATTACK_TARGET, target);
        entity.getBrain().forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }
}
