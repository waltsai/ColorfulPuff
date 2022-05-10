package waltsai.colorfulpuff.server.entity.ai.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.poi.PointOfInterestStorage;
import waltsai.colorfulpuff.server.entity.AbstractPuffEntity;

public class WalkToPointOfInterestTask extends Task<AbstractPuffEntity> {
    private final float speed;
    private final int completionRange;

    public WalkToPointOfInterestTask(float speed, int completionRange) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT));
        this.speed = speed;
        this.completionRange = completionRange;
    }

    protected boolean shouldRun(ServerWorld serverWorld, AbstractPuffEntity villagerEntity) {
        return !serverWorld.isNearOccupiedPointOfInterest(villagerEntity.getBlockPos());
    }

    protected void run(ServerWorld serverWorld, AbstractPuffEntity villagerEntity, long l) {
        PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
        int i = pointOfInterestStorage.getDistanceFromNearestOccupied(ChunkSectionPos.from(villagerEntity.getBlockPos()));
        Vec3d vec3d = null;

        for(int j = 0; j < 5; ++j) {
            Vec3d vec3d2 = FuzzyTargeting.find(villagerEntity, 15, 7, (blockPos) -> {
                return (double)(-pointOfInterestStorage.getDistanceFromNearestOccupied(ChunkSectionPos.from(blockPos)));
            });
            if (vec3d2 != null) {
                int k = pointOfInterestStorage.getDistanceFromNearestOccupied(ChunkSectionPos.from(new BlockPos(vec3d2)));
                if (k < i) {
                    vec3d = vec3d2;
                    break;
                }

                if (k == i) {
                    vec3d = vec3d2;
                }
            }
        }

        if (vec3d != null) {
            villagerEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, (new WalkTarget(vec3d, this.speed, this.completionRange)));
        }

    }
}