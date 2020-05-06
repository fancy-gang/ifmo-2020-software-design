package org.fancy.memers

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.fancy.memers.model.*
import org.fancy.memers.ui.main.board.World
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Size3D

private val MOSHI = Moshi.Builder()
    .add(
        PolymorphicJsonAdapterFactory.of(Entity::class.java, "entityType")
            .withSubtype(Player::class.java, "player")
            .withSubtype(Enemy::class.java, "enemy")
    )
    .add(
        PolymorphicJsonAdapterFactory.of(Block::class.java, "blockType")
            .withSubtype(Empty::class.java, "empty")
            .withSubtype(Floor::class.java, "floor")
            .withSubtype(Wall::class.java, "wall")
            .withSubtype(Entity::class.java, "entity")
            .withSubtype(Player::class.java, "player")
            .withSubtype(Enemy::class.java, "enemy")
    )
    .add(KotlinJsonAdapterFactory())
    .build()
private val ADAPTER = MOSHI.adapter(WorldWrapper::class.java)

private data class WorldWrapper(
    val size: Size3D,

    // moshi does not have adapter for pairs: https://github.com/square/moshi/issues/508
    val positions: List<Position3D>,
    val blocks: List<Block>
)

fun World.serialize(): String {
    val wrapper = WorldWrapper(size, board.keys.toList(), board.values.toList())
    return ADAPTER.toJson(wrapper)
}

fun World.Companion.deserialize(json: String): World {
    val wrapper = ADAPTER.fromJson(json)!!
    val board = (wrapper.positions zip wrapper.blocks).toMap(mutableMapOf())
    return World(wrapper.size, board)
}
