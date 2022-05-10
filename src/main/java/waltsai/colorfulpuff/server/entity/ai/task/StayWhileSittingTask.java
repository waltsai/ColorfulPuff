package waltsai.colorfulpuff.server.entity.ai.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import waltsai.colorfulpuff.server.entity.AbstractPuffEntity;

public class StayWhileSittingTask extends Task<AbstractPuffEntity> {
    public StayWhileSittingTask() {
        super(ImmutableMap.of(), Integer.MAX_VALUE);
    }

    @Override
    protected boolean shouldRun(ServerWorld world, AbstractPuffEntity entity) {
        return entity.isSitting();
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, AbstractPuffEntity entity, long time) {
        return entity.isSitting();
    }

    @Override
    protected void run(ServerWorld world, AbstractPuffEntity entity, long time) {
        super.run(world, entity, time);
        entity.setInSittingPose(true);
    }

    @Override
    protected void keepRunning(ServerWorld world, AbstractPuffEntity entity, long time) {
        super.keepRunning(world, entity, time);
        entity.getNavigation().stop();
    }

    @Override
    protected void finishRunning(ServerWorld world, AbstractPuffEntity entity, long time) {
        super.finishRunning(world, entity, time);
        entity.setInSittingPose(false);
    }
}
