package ink.ptms.tersusai.inject.impl;

import ink.ptms.tersusai.inject.TersusSelector;
import io.izzel.taboolib.util.lite.Numbers;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.PathfinderGoal;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;

/**
 * v1.8
 *
 * @Author sky
 * @Since 2019-08-23 10:53
 */
public class Injector10800 extends PathfinderGoalSelector implements TersusSelector {

    private PathfinderGoalSelector selector;
    private int level = 0;

    public Injector10800() {
        super(null);
    }

    public Injector10800(PathfinderGoalSelector selector) {
        super(null);
        this.selector = selector;
    }

    @Override
    public void a(int i, PathfinderGoal pathfindergoal) {
        selector.a(i, pathfindergoal);
    }

    @Override
    public void a(PathfinderGoal pathfindergoal) {
        selector.a(pathfindergoal);
    }

    @Override
    public void a() {
        if (level == 0 || Numbers.getRandom().nextInt(100) >= level) {
            selector.a();
        }
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public TersusSelector generateGoal(LivingEntity entity) {
        return new Injector10800(((EntityInsentient) ((CraftLivingEntity) entity).getHandle()).goalSelector);
    }

    @Override
    public TersusSelector generateTarget(LivingEntity entity) {
        return new Injector10800(((EntityInsentient) ((CraftLivingEntity) entity).getHandle()).targetSelector);
    }
}
