package ink.ptms.tersusai.inject.impl;

import ink.ptms.tersusai.inject.TersusSelector;
import io.izzel.taboolib.util.lite.Numbers;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;

import java.util.stream.Stream;

/**
 * v1.14
 *
 * @Author sky
 * @Since 2019-08-23 10:53
 */
public class Injector11600 extends PathfinderGoalSelector implements TersusSelector {

    private PathfinderGoalSelector selector;
    private int level = 0;

    public Injector11600() {
        super(null);
    }

    public Injector11600(PathfinderGoalSelector selector) {
        super(null);
        this.selector = selector;
    }

    @Override
    public void a(int var0, PathfinderGoal var1) {
        selector.a(var0, var1);
    }

    @Override
    public void a(PathfinderGoal var0) {
        selector.a(var0);
    }

    @Override
    public void doTick() {
        if (level == 0 || Numbers.getRandom().nextInt(100) >= level) {
            selector.doTick();
        }
    }

    @Override
    public Stream<PathfinderGoalWrapped> d() {
        return selector.d();
    }

    @Override
    public void a(PathfinderGoal.Type var0) {
        selector.a(var0);
    }

    @Override
    public void b(PathfinderGoal.Type var0) {
        selector.b(var0);
    }

    @Override
    public void a(PathfinderGoal.Type var0, boolean var1) {
        selector.a(var0, var1);
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
        return new Injector11600(((EntityInsentient) ((CraftLivingEntity) entity).getHandle()).goalSelector);
    }

    @Override
    public TersusSelector generateTarget(LivingEntity entity) {
        return new Injector11600(((EntityInsentient) ((CraftLivingEntity) entity).getHandle()).targetSelector);
    }
}
