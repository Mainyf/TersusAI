package ink.ptms.tersusai.rule

/**
 * @Author sky
 * @Since 2019-08-23 16:41
 */
enum class RuleAffect {

    GROUP, GLOBAL, SINGLE, NONE;

    companion object {
        fun fromStr(`in`: String): RuleAffect {
            return try {
                RuleAffect.valueOf(`in`.toUpperCase())
            } catch (ignore: Throwable) {
                NONE
            }
        }
    }
}
