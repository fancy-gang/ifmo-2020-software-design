package org.fancy.memers.model

import org.fancy.memers.model.ai.EnemyBehaviour
import org.hexworks.zircon.api.color.TileColor
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.graphics.Symbols
import java.util.*


sealed class Block : Drawable {
    override fun equals(other: Any?): Boolean = this === other || javaClass == other?.javaClass
    override fun hashCode(): Int = javaClass.hashCode()

    abstract val canStepOn: Boolean
    override val isVisible: Boolean get() = true
}

class Empty : Block() {
    override val symbol: Char
        get() = ' '

    override val canStepOn: Boolean
        get() = true
    override val displayName: String
        get() = ""
}

class Floor : Block() {
    var item: Item? = null
    override val symbol: Char
        get() = item?.symbol ?: Symbols.INTERPUNCT
    override val foregroundColor: TileColor
        get() = TileColor.fromString(if (item == null) "#75715E" else "#FFEB3B")
    override val backgroundColor: TileColor
        get() = TileColor.fromString("#1e2320", 100)
    override val canStepOn: Boolean
        get() = true
    override val displayName: String
        get() = "Floor"
}

class Wall : Block() {
    override val symbol: Char
        get() = '#'
    override val foregroundColor: TileColor
        get() = TileColor.fromString("#75715E")
    override val backgroundColor: TileColor
        get() = TileColor.fromString("#3E3D32")
    override val canStepOn: Boolean
        get() = false
    override val displayName: String
        get() = "Wall"
}

sealed class Creature(
    var position: Position3D,
    open val attack: Int = DEFAULT_ATTACK,
    var health: Int = INITIAL_HEALTH,
    open val defence: Int = 0
) : Block() {
    override fun equals(other: Any?): Boolean = super.equals(other) && position == (other as Creature).position
    override fun hashCode(): Int = Objects.hashCode(position)
    override fun toString(): String {
        return "Entity(health=$health)"
    }

    open val effects = mutableListOf<Effect>()

    open fun updateEffects() {
        effects.forEach { it.duration -= 1 }
        effects.removeIf { !it.isActive }
    }

    protected val initialHealth = health
    val isDead: Boolean get() = health <= 0

    override val canStepOn: Boolean
        get() = false
    override val isVisible: Boolean
        get() = !isDead

    inline fun <reified EffectType: Effect> hasEffect() = effects.indexOfFirst { it is EffectType } != -1

    companion object {
        const val DEFAULT_ATTACK: Int = 1
        const val INITIAL_HEALTH: Int = 100
    }
}

class Player(position: Position3D) : Creature(position) {
    val inventory: Inventory = Inventory()

    override val attack: Int = super.attack + inventory.activeItems.sumBy { it.attackBonus }
    override val defence: Int = inventory.activeItems.sumBy { it.attackBonus }

    override val symbol: Char get() = '@'

    override fun toString(): String = "Player(health=$health, effects=$effects)"

    /*
     * Считает текущий цвет
     * minimumColor – изначальный цвет
     * maximumColor – цвет при 0 значении health
     */
    override val foregroundColor: TileColor
        get() = gradientColor(
            TileColor.fromString("#00ff00"),
            TileColor.fromString("#ffff00"),
            -(initialHealth - health) / initialHealth.toDouble()
        )
    override val backgroundColor: TileColor get() = TileColor.fromString("#1e2320")
    override val displayName: String
        get() = "Player"
}

class Enemy(private val name: String, val behaviour: EnemyBehaviour, position: Position3D) : Creature(position) {
    override val symbol: Char get() = 'E'

    override fun toString(): String = "Enemy(health=$health, behaviour=$behaviour, effects=$effects)"

    /*
        Считает текущий цвет
        minimumColor – изначальный цвет
        maximumColor – цвет при 0 значении health
     */
    override val foregroundColor: TileColor
        get() = gradientColor(
            minimumColor = TileColor.fromString("#ff0000"),
            maximumColor = TileColor.fromString("#ffff00"),
            ratio = (initialHealth - health) / initialHealth.toDouble()
        )
    override val backgroundColor: TileColor get() = TileColor.fromString("#1e2320")
    override val displayName: String
        get() = name
}
