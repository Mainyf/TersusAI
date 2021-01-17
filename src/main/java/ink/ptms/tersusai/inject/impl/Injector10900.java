package ink.ptms.tersusai.inject.impl;

import ink.ptms.tersusai.inject.TersusSelector;
import io.izzel.taboolib.util.lite.Numbers;
import net.minecraft.server.v1_9_R2.EntityInsentient;
import net.minecraft.server.v1_9_R2.PathfinderGoal;
import net.minecraft.server.v1_9_R2.PathfinderGoalSelector;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;

/**
 * v1.9-1.12
 *
 * @Author sky
 * @Since 2019-08-23 10:53
 */
public class Injector10900 extends PathfinderGoalSelector implements TersusSelector {

    private PathfinderGoalSelector selector;
    private int level = 0;

    public Injector10900() {
        super(null);
    }

    public Injector10900(PathfinderGoalSelector selector) {
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
    public boolean b(int i) {
        return selector.b(i);
    }

    @Override
    public void c(int i) {
        selector.c(i);
    }

    @Override
    public void d(int i) {
        selector.d(i);
    }

    @Override
    public void a(int i, boolean b) {
        selector.a(i, b);
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public TersusSelector generateGoal(LivingEntity entity) {
        return new Injector10900(((EntityInsentient) ((CraftLivingEntity) entity).getHandle()).goalSelector);
    }

    @Override
    public TersusSelector generateTarget(LivingEntity entity) {
        return new Injector10900(((EntityInsentient) ((CraftLivingEntity) entity).getHandle()).targetSelector);
    }
}
