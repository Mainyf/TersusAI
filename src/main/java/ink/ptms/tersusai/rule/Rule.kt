package ink.ptms.tersusai.rule

import com.google.common.collect.Lists
import io.izzel.taboolib.module.nms.NMS
import io.izzel.taboolib.util.lite.Numbers
import io.izzel.taboolib.util.lite.Scripts
import org.bukkit.Location
import java.util.function.Consumer

import javax.script.CompiledScript
import javax.script.SimpleBindings

/**
 * @Author sky
 * @Since 2019-08-23 16:42
 */
class Rule {

    var condition: CompiledScript? = null
        private set
    var inhibit: Int = 0
        private set
    var affect: RuleAffect? = null
        private set
    var logger: List<String>? = null
        private set

    constructor(condition: String, inhibit: Int, affect: String) {
        this.condition = Scripts.compile(condition)
        this.inhibit = inhibit
        this.affect = RuleAffect.fromStr(affect)
        this.logger = Lists.newArrayList()
    }

    constructor(condition: String, inhibit: Int, affect: String, logger: List<String>) {
        this.condition = Scripts.compile(condition)
        this.inhibit = inhibit
        this.affect = RuleAffect.fromStr(affect)
        this.logger = logger
    }

    fun check(count: Int, p: Double, affect: RuleAffect): Boolean {
        if (affect !== this.affect) {
            return false
        }
        val simpleBindings = SimpleBindings()
        simpleBindings["\$random"] = Numbers.getRandom().nextDouble()
        simpleBindings["\$tps"] = NMS.handle().tps
        simpleBindings["\$count"] = count
        simpleBindings["\$p"] = p
        try {
            return Numbers.getBoolean(condition!!.eval(simpleBindings).toString())!!
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return false
    }

    fun log(count: Int, p: Double, location: Location) {
        logger!!.stream().map { line ->
            line
                    .replace("\$tps", NMS.handle().tps[0].toString())
                    .replace("\$count", count.toString())
                    .replace("\$p", p.toString())
                    .replace("\$location", location.world!!.name + ":" + location.blockX + "," + location.blockY + "," + location.blockZ)
        }.forEach { println(it) }
    }
}
