package ink.ptms.tersusai.inject;

import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;

/**
 * @Author sky
 * @Since 2019-08-23 14:31
 */
public class TersusInjectorImpl extends TersusInjector {

    @Override
    public TersusSelector getGoal0(LivingEntity entity) {
        Object pathfinder = ((EntityInsentient) ((CraftLivingEntity) entity).getHandle()).goalSelector;
        return pathfinder instanceof TersusSelector ? (TersusSelector) pathfinder : null;
    }

    @Override
    public TersusSelector getTarget0(LivingEntity entity) {
        Object pathfinder = ((EntityInsentient) ((CraftLivingEntity) entity).getHandle()).targetSelector;
        return pathfinder instanceof TersusSelector ? (TersusSelector) pathfinder : null;
    }

    @Override
    public void setGoal0(LivingEntity entity, TersusSelector selector) {
        ((EntityInsentient) ((CraftLivingEntity) entity).getHandle()).goalSelector = (PathfinderGoalSelector) selector;
    }

    @Override
    public void setTarget0(LivingEntity entity, TersusSelector selector) {
        ((EntityInsentient) ((CraftLivingEntity) entity).getHandle()).targetSelector = (PathfinderGoalSelector) selector;
    }

    @Override
    public boolean isInsentient0(LivingEntity entity) {
        return ((CraftLivingEntity) entity).getHandle() instanceof EntityInsentient;
    }
}
