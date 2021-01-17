package ink.ptms.tersusai;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import ink.ptms.tersusai.inject.TersusInjector;
import ink.ptms.tersusai.rule.CollectEntity;
import ink.ptms.tersusai.rule.CollectGroup;
import ink.ptms.tersusai.rule.CollectTask;
import ink.ptms.tersusai.rule.Collector;
import io.izzel.taboolib.module.command.base.*;
import io.izzel.taboolib.module.inject.TInject;
import io.izzel.taboolib.module.lite.SimpleI18n;
import io.izzel.taboolib.module.locale.TLocale;
import io.izzel.taboolib.module.nms.NMS;
import io.izzel.taboolib.module.tellraw.TellrawJson;
import io.izzel.taboolib.util.lite.Numbers;
import io.izzel.taboolib.util.lite.SoundPack;
import io.izzel.taboolib.util.lite.cooldown.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author sky
 * @Since 2019-08-23 0:50
 */
@BaseCommand(
        name = "tersusAi", aliases = {"ta", "tersus"}, permission = "*"
)
public class TersusCommand extends BaseMainCommand {

    @TInject
    private static final Cooldown cooldown = new Cooldown("TersusAI:CommandSound", 100);
    private static final String normal = "§7§l[§f§lTersus§7§l] §7";
    private static final String error = "§c§l[§4§lTersus§c§l] §c";
    private static final SoundPack soundPack = new SoundPack("ENTITY_EXPERIENCE_ORB_PICKUP-1-2");

    public static void normal(CommandSender sender, String args) {
        sender.sendMessage(normal + TLocale.Translate.setColored(args));
        // 音效
        if (sender instanceof Player && !cooldown.isCooldown(sender.getName(), 0)) {
            soundPack.play(((Player) sender).getLocation());
        }
    }

    public static void error(CommandSender sender, String args) {
        sender.sendMessage(error + TLocale.Translate.setColored(args));
        // 音效
        if (sender instanceof Player && !cooldown.isCooldown(sender.getName(), 0)) {
            soundPack.play(((Player) sender).getLocation());
        }
    }

    @SubCommand
    BaseSubCommand status = new BaseSubCommand() {

        @Override
        public String getDescription() {
            return "获取信息";
        }

        @Override
        public void onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
            normal(sender, "§7§m                                   ");
            normal(sender, "状态: §f" + Arrays.stream(NMS.handle().getTPS()).mapToObj(i -> Numbers.format(i).toString()).collect(Collectors.joining(", ")));
            for (World world : Bukkit.getWorlds()) {
                List<LivingEntity> livingEntities = world.getLivingEntities();
                Map<String, Integer> map = Maps.newTreeMap();
                Map<String, Integer> mapAffect = Maps.newHashMap();
                for (LivingEntity entity : livingEntities) {
                    String name = SimpleI18n.getName(entity);
                    map.put(name, map.getOrDefault(name, 0) + 1);
                    if (TersusInjector.isInsentient(entity) && TersusInjector.isInjected(entity)) {
                        mapAffect.put(name, mapAffect.getOrDefault(name, 0) + 1);
                    }
                }
                List<String> entitiesSorted = Lists.newArrayList(map.keySet());
                entitiesSorted.sort((b, a) -> map.get(a).compareTo(map.get(b)));
                normal(sender, "世界: &f" + world.getName());
                normal(sender, "&8- &7区块: &f" + world.getLoadedChunks().length);
                TellrawJson.create()
                        .append("§7§l[§f§lTersus§7§l] §8- §7实体: §f" + livingEntities.size() + " §a(" + (mapAffect.values().stream().mapToInt(i -> i).sum()) + ") ")
                        .append("§8(详细)").hoverText("§7详细信息↓\n" + entitiesSorted.stream().map(e -> " §f" + e + " §8-> §f" + map.get(e) + " §a(" + mapAffect.getOrDefault(e, 0) + ")").collect(Collectors.joining("\n")))
                        .send(sender);
            }
            normal(sender, "§7§m                                   ");
        }
    };

    @SubCommand
    BaseSubCommand collect = new BaseSubCommand() {

        @Override
        public String getDescription() {
            return "实体分组调试";
        }

        @Override
        public void onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
            long time1 = System.currentTimeMillis();
            normal(sender, "§7§m                                   ");
            normal(sender, "获取有效实体.");
            List<CollectEntity> affectEntity = Collector.getAffectEntity(((Player) sender).getWorld());
            normal(sender, "创建工作线程.");
            Bukkit.getScheduler().runTaskAsynchronously(TersusAI.INSTANCE.getPlugin(), () -> {
                long time2 = System.currentTimeMillis();
                CollectTask collectTask = new CollectTask(affectEntity).collect();
                normal(sender, "实体分组.");
                int i = 1;
                for (CollectGroup group : collectTask.getGroups()) {
                    TellrawJson.create()
                            .append("§7§l[§f§lTersus§7§l] §8  ")
                            .append((i++) + " §7-> §f" + group.getEntities().size() + " §8(" + Math.floor(Math.PI * group.getRange() * group.getRange()) + "m²) (ρ=" + Numbers.format((double) group.getEntities().size() / group.getDensity()) + ")").hoverText("点击传送至附近").clickCommand("/tp " + group.getCenter().getX() + " " + group.getCenter().getY() + " " + group.getCenter().getZ())
                            .send(sender);
                    ((Player) sender).spawnParticle(Particle.END_ROD, group.getMaxDistanceCache()[0].getLocation(), 1000, 0, 100, 0, 0);
                    ((Player) sender).spawnParticle(Particle.END_ROD, group.getMaxDistanceCache()[1].getLocation(), 1000, 0, 100, 0, 0);
                }
                normal(sender, "§7耗时 &f" + (System.currentTimeMillis() - time1) + "ms &8(async: " + (time2 - time1) + "ms)");
                normal(sender, "§7§m                                   ");
            });
        }

        @Override
        public CommandType getType() {
            return CommandType.PLAYER;
        }
    };

    @SubCommand
    BaseSubCommand reload = new BaseSubCommand() {

        @Override
        public String getDescription() {
            return "重载配置";
        }

        @Override
        public void onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
            TersusAI.reload();
            normal(sender, "§7重载完成.");
        }
    };
}
