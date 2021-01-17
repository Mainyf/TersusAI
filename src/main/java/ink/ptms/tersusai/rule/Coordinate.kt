package ink.ptms.tersusai.rule

import org.bukkit.Location

import java.util.Objects

/**
 * @Author sky
 * @Since 2019-08-25 16:38
 */
class Coordinate(location: Location) {

    private val x: Int = location.blockX / 16
    private val y: Int = location.blockY / 16
    private val z: Int = location.blockZ / 16

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is Coordinate) {
            return false
        }
        val that = other as Coordinate?
        return x == that!!.x && y == that.y && z == that.z
    }

    override fun hashCode(): Int {
        return Objects.hash(x, y, z)
    }
}
