package org.fancy.memers.model.generator

import org.fancy.memers.model.*
import org.hexworks.zircon.api.data.Position3D
import org.hexworks.zircon.api.data.Size3D

class UniformBoardGenerator(
    private val boardSize: Size3D,
    private val fillRate: Double = 0.2,
    seed: Int? = null
) : RandomBoardGenerator(seed) {
    init {
        check(0 < fillRate && fillRate < 1)
        check(boardSize.xLength > 2 && boardSize.yLength > 2 && boardSize.zLength > 0)
    }

    override fun generateMap(withPlayer: Boolean, numberEnemies: Int): Map<Position3D, Block> {
        val pairs = boardSize.fetchFloorPositions()
            .map { it to randomBlock(it) }
            .toList()
        val board = pairs.toMap().toMutableMap()
        if (withPlayer) {
            val playerPosition = randomPlayerPosition()
            board[playerPosition] = Player(playerPosition)
        }
        return board
    }

    private fun randomPlayerPosition(): Position3D {
        return Position3D.create(
            random.nextInt(boardSize.xLength),
            random.nextInt(boardSize.yLength),
            boardSize.zLength - 1
        )
    }

    private fun randomBlock(position: Position3D): Block {
        val isSafePositions = position
            .contains2D(
                0 until boardSize.xLength,
                0 until boardSize.yLength
            )
        return if (isSafePositions && random.nextDouble() >= fillRate) {
            Floor()
        } else {
            Wall()
        }
    }
}
