package ink.ptms.tersusai.rule;

import com.google.common.collect.Maps;
import ink.ptms.tersusai.TersusAI;
import ink.ptms.tersusai.inject.TersusInjector;
import io.izzel.taboolib.module.inject.TSchedule;
import io.izzel.taboolib.module.lite.SimpleCounter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author sky
 * @Since 2019-08-24 7:57
 */
public class Collector {

    private static Map<String, CollectTask> worldTask = Maps.newConcurrentMap();
    private static Map<Coordinate, SimpleCounter> filter = Maps.newConcurrentMap();

    @TSchedule(period = 40, async = true)
    static void run() {
        for (World world : Bukkit.getWorlds()) {
            CollectTask collectTask = new CollectTask(getAffectEntity(world)).collect();
            int inhibit = 0;
            boolean global = false;
            for (Rule method : TersusAI.getMethods()) {
                if (method.check(collectTask.getEntities().size(), 0, RuleAffect.GLOBAL)) {
                    global = true;
                    inhibit = method.getInhibit();
                    collectTask.getEntities().forEach(entity -> TersusInjector.level(entity.getLivingEntity(), method.getInhibit()));
                    printLog(method, world.getSpawnLocation(), collectTask.getEntities().size(), 0);
                    break;
                }
            }
            for (CollectGroup collectGroup : collectTask.getGroups()) {
                for (Rule method : TersusAI.getMethods()) {
                    if (method.check(collectGroup.getEntities().size(), collectGroup.getDensity(), RuleAffect.GROUP) && method.getInhibit() < inhibit) {
                        collectGroup.getEntities().forEach(entity -> TersusInjector.level(entity.getLivingEntity(), method.getInhibit()));
                        printLog(method, collectGroup.getCenter(), collectGroup.getEntities().size(), collectGroup.getDensity());
                        break;
                    }
                }
            }
            if (!global) {
                collectTask.getSingle().forEach(entity -> TersusAI.getMethods().stream()
                        .filter(rule -> rule.check(0, 0, RuleAffect.SINGLE))
                        .findFirst()
                        .ifPresent(rule -> TersusInjector.level(entity.getLivingEntity(), rule.getInhibit())));
            }
        }
    }

    public static int getCollectRange() {
        return TersusAI.getConf().getInt("Rules.collect-range");
    }

    public static int getCollectSize() {
        return TersusAI.getConf().getInt("Rules.collect-size");
    }

    public static List<CollectEntity> getAffectEntity(World world) {
        return world.getLivingEntities().stream().filter(TersusAI::isAffected).map(CollectEntity::new).collect(Collectors.toList());
    }

    public static Map<String, CollectTask> getWorldTask() {
        return worldTask;
    }

    private static void printLog(Rule method, Location location, int size, int i) {
        if (filter.computeIfAbsent(new Coordinate(location), n -> new SimpleCounter(TersusAI.getConf().getInt("Rules.logger-level", 10))).next()) {
            method.log(size, i, location);
        }
    }
}
