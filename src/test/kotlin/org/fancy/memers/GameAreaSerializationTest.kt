package org.fancy.memers

import org.fancy.memers.model.generator.BoardGenerator
import org.fancy.memers.ui.main.board.World
import org.hexworks.zircon.api.data.Size3D
import org.junit.jupiter.api.Test

internal class GameAreaSerializationTest {
    @Test
    fun main() {
        val size = Size3D.create(20, 10, 5)
        val board = BoardGenerator.defaultGenerator(size).generateMap()
        val worldBefore = World(size, board.toMutableMap())

        val worldAfter = World.deserialize(worldBefore.serialize())
        check(worldBefore.size == worldAfter.size)
        // for debug
        for (position in worldBefore.actualBoard.keys) {
            val blockBefore = worldBefore.actualBoard[position]
            val blockAfter = worldAfter.actualBoard[position]
            check(blockBefore == blockAfter)
        }
        check(worldBefore.actualBoard == worldAfter.actualBoard)
    }
}
