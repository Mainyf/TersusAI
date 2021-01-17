package ink.ptms.tersusai.listener

import ink.ptms.tersusai.TersusAI
import ink.ptms.tersusai.inject.TersusInjector
import ink.ptms.tersusai.inject.TersusSelector
import io.izzel.taboolib.Version
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.inject.TSchedule
import io.izzel.taboolib.util.lite.SoundPack
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.EquipmentSlot
import java.util.function.Consumer

/**
 * @Author sky
 * @Since 2019-08-23 1:08
 */
@TListener
class ListenerEntity : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: EntitySpawnEvent) {
        if (e.entity is LivingEntity && TersusAI.isAffected(e.entity as LivingEntity)) {
            val range = TersusAI.getConf().getInt("EntitySpawnLimit.range")
            val count = TersusAI.getConf().getInt("EntitySpawnLimit.count")
            if (range > 0 && count > 0 && e.entity.getNearbyEntities(range.toDouble(), range.toDouble(), range.toDouble()).stream().filter { i -> i.type == e.entity.type }.count() > count) {
                e.isCancelled = true
            } else {
                TersusInjector.inject(e.entity as LivingEntity)
            }
        }
    }

    @EventHandler
    fun e(e: PlayerInteractEntityEvent) {
        if (e.rightClicked is LivingEntity
                // 管理
                && e.player.isOp
                // 潜行
                && e.player.isSneaking
                // 创造
                && e.player.gameMode == GameMode.CREATIVE
                // 物品
                && e.player.itemInHand.type == Material.BLAZE_ROD
                // 调试
                && TersusAI.getConf().getBoolean("Rules.debug-item")
                // 有效实体
                && TersusInjector.isInsentient(e.rightClicked as LivingEntity)
                // 主手点击
                && (Version.isBefore(Version.v1_9) || e.hand == EquipmentSlot.HAND)) {
            e.isCancelled = true
            e.player.sendMessage("§7§l[§f§lTersus§7§l] §7AI-Level (Goal): §f" + (TersusInjector.getGoal(e.rightClicked as LivingEntity)?.level ?: "Unknown"))
            e.player.sendMessage("§7§l[§f§lTersus§7§l] §7AI-Level (Target): §f" + (TersusInjector.getTarget(e.rightClicked as LivingEntity)?.level ?: "Unknown"))
            soundPack.play(e.player)
        }
    }

    companion object {

        private val soundPack = SoundPack("ENTITY_EXPERIENCE_ORB_PICKUP-1-2")

        @TSchedule(period = 200, async = true)
        fun init() {
            Bukkit.getWorlds().stream().flatMap { world -> world.livingEntities.stream() }.filter { livingEntity -> TersusAI.isAffected(livingEntity) && !TersusInjector.isInjected(livingEntity) }.forEach { TersusInjector.inject(it) }
        }
    }
}