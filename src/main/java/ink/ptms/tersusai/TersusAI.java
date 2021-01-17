package ink.ptms.tersusai;

import com.google.common.collect.Lists;
import ink.ptms.tersusai.inject.TersusInjector;
import ink.ptms.tersusai.rule.Rule;
import io.izzel.taboolib.loader.Plugin;
import io.izzel.taboolib.loader.PluginBase;
import io.izzel.taboolib.loader.PluginBoot;
import io.izzel.taboolib.module.config.TConfig;
import io.izzel.taboolib.module.inject.TInject;
import io.izzel.taboolib.module.inject.TSchedule;
import io.izzel.taboolib.module.lite.SimpleI18n;
import io.izzel.taboolib.module.locale.logger.TLogger;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.NumberConversions;

import java.util.List;
import java.util.Map;

/**
 * @Author sky
 * @Since 2019-08-23 0:48
 */
public class TersusAI extends Plugin {

    public static final TersusAI INSTANCE = new TersusAI();

    @TInject(state = TInject.State.ACTIVATED)
    private static Metrics metrics;
    @TInject
    private static TLogger logger;
    @TInject
    private static TConfig conf;

    private static final List<Rule> methods = Lists.newArrayList();

    /**
     * 判断生物是否受本插件效果影响
     * 判断内容：
     * - 世界禁用
     * - 实体类型
     * - 实体名称
     */
    public static boolean isAffected(LivingEntity entity) {
        return TersusInjector.isInsentient(entity)
                && !conf.getStringList("DisableWorlds").contains(entity.getWorld().getName())
                && !conf.getStringList("DisableEntities").contains(entity.getType().name().toLowerCase())
                && !conf.getStringList("DisableEntities").contains(SimpleI18n.getCustomName(entity));
    }

    @TSchedule
    static void reload() {
        conf.listener(() -> {
            methods.clear();
            conf.getList("Rules.methods", Lists.newArrayList()).forEach(e -> {
                if (e instanceof Map) {
                    Map<?, ?> map = (Map<?, ?>) e;
                    if (map.containsKey("logger") && map.get("logger") instanceof List) {
                        methods.add(new Rule(String.valueOf(map.get("condition")), NumberConversions.toInt(map.get("inhibit")), String.valueOf(map.get("affect")), (List) map.get("logger")));
                    } else {
                        methods.add(new Rule(String.valueOf(map.get("condition")), NumberConversions.toInt(map.get("inhibit")), String.valueOf(map.get("affect"))));
                    }
                } else {
                    logger.warn("Invalid control rule format: " + e);
                }
            });
            logger.info("Loaded " + methods.size() + " control rules.");
        }).runListener();
    }

    public static TConfig getConf() {
        return conf;
    }

    public static List<Rule> getMethods() {
        return methods;
    }
}
