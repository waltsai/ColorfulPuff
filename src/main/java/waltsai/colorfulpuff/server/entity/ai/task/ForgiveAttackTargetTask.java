package waltsai.colorfulpuff.server.entity.ai.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import waltsai.colorfulpuff.server.entity.AbstractPuffEntity;

public class ForgiveAttackTargetTask extends Task<AbstractPuffEntity> {
    public ForgiveAttackTargetTask() {
        super(ImmutableMap.of(MemoryModuleType.ANGRY_AT, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.ATTACK_TARGET, MemoryModuleState.REGISTERED));
    }


}
