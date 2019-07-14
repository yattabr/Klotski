package br.com.wobbu.klotski.model

import android.content.Context
import android.content.res.TypedArray
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import br.com.wobbu.klotski.R

class GameBoard : RelativeLayout {
    lateinit var board: Array<IntArray>
    lateinit var blocks: Array<Block>
    lateinit var border: Array<View>
    var lineBlockWidth: Int = 0
    var lineWidth: Int = 0
    var blockWidth: Int = 0
    var positionList: IntArray? = null
    var padding: Int = 0
    var index: Int = 0
    var gameBoardList: TypedArray? = null
    var isSuccess: Boolean = false
    var isMoving: Int = 0
    var stepView: TextView? = null

    constructor(context: Context) : super(context) {
        //        init(null, 0);
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        //        init(attrs, 0);
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        //        init(attrs, defStyle);
    }

    fun init() {
        isSuccess = false
        isMoving = -1
        board = Array(5) { IntArray(4) }
        blocks = arrayOf(blocks[10])
        for (i in 0..4) {
            for (j in 0..3) {
                board[i][j] = -1
            }
        }
        stepView!!.text = "0"
        post {
            removeAllViews()
            padding = (right - left) * 3 / 100
            lineBlockWidth = (right - left - padding * 2) / 4
            val lp = layoutParams as ConstraintLayout.LayoutParams
            lineWidth = (0.04 * lineBlockWidth).toInt()
            lp.height = lineBlockWidth * 5 + padding * 2
            lp.width = lineBlockWidth * 4 + padding * 2
            padding += (1.5 * lineWidth).toInt()
            blockWidth = lineBlockWidth - lineWidth
            layoutParams = lp
            initBoard()
            initBorder()
            System.gc()
        }
    }

    fun moveBlock(index: Int, x: Int, y: Int) {
        val l = x * blockWidth + padding
        val t = y * blockWidth + padding
        val r = l + blocks[index].spanX * lineBlockWidth - (blocks[index].spanX - 1) * lineWidth
        val b = t + blocks[index].spanY * lineBlockWidth - (blocks[index].spanY - 1) * lineWidth
        blocks[index].layout(l, t, r, b)
        blocks[index].x = x
        blocks[index].y = y
        markBlock(index, x, y)
    }

    fun markBlock(index: Int, x: Int, y: Int) {
        for (i in 0 until blocks[index].spanX) {
            for (j in 0 until blocks[index].spanY) {
                board[j + y][i + x] = index
            }
        }
        printBoard()
    }

    fun unmarkBlock(index: Int, x: Int, y: Int) {
        for (i in 0 until blocks[index].spanX) {
            for (j in 0 until blocks[index].spanY) {
                board[j + y][i + x] = -1
            }
        }
    }

    fun printBoard() {
        var boardString = "board: \n"
        for (i in 0..4) {
            for (j in 0..3) {
                boardString = boardString + board[i][j] + "\t"
            }
            boardString = boardString + "\n"
        }
        Log.d("printBoard", boardString)
    }

    fun initBoard() {
        val imageList = resources.obtainTypedArray(R.array.game_board_image)
        var i = 0
        while (i < 10) {
            val index = i
            val spanX = positionList!![i * 4]
            val spanY = positionList!![i * 4 + 1]
            val x = positionList!![i * 4 + 2]
            val y = positionList!![i * 4 + 3]
            blocks[index] = Block(this, index, spanX, spanY, imageList.getDrawable(index))
            addView(blocks[index], RelativeLayout.LayoutParams(lineBlockWidth * spanX, lineBlockWidth * spanY))
            blocks[index].post {
                blocks[index].init(null, 0)
                moveBlock(index, x, y)
            }
            i += 1
        }
    }

    fun initBorder() {
        border = arrayOf(border[5])
        for (i in 0..4) {
            border[i] = View(context)
            border[i].setBackgroundResource(R.drawable.board)
        }
        addView(border[0], LayoutParams(lineBlockWidth * 4 + padding * 2, padding))
        addView(border[1], LayoutParams(padding, lineBlockWidth * 5 + padding * 2))
        addView(border[2], LayoutParams(padding, lineBlockWidth * 5 + padding * 2))
        addView(border[3], LayoutParams(lineBlockWidth + padding, padding))
        addView(border[4], LayoutParams(lineBlockWidth + padding, padding))
        val offsetX = lineWidth * 3
        val offsetY = lineWidth * 4
        border[0].post { border[0].layout(0, 0, lineBlockWidth * 4 + padding * 2 - offsetX, padding) }
        border[1].post { border[1].layout(0, 0, padding, lineBlockWidth * 5 + padding * 2) }
        border[2].post {
            border[2].layout(
                lineBlockWidth * 4 + padding - offsetX,
                0,
                lineBlockWidth * 4 + padding * 2 - offsetX,
                lineBlockWidth * 5 + padding * 2
            )
        }
        border[3].post {
            border[3].layout(
                0,
                lineBlockWidth * 5 + padding - offsetY,
                lineBlockWidth + padding,
                lineBlockWidth * 5 + padding * 2
            )
        }
        border[4].post {
            border[4].layout(
                lineBlockWidth * 3 + padding - offsetX,
                lineBlockWidth * 5 + padding - offsetY,
                lineBlockWidth * 4 + padding * 2 - offsetX,
                lineBlockWidth * 5 + padding * 2
            )
        }
    }

    fun step(index: Int, x1: Int, y1: Int, x2: Int, y2: Int) {
        if (x1 == x2 && y1 == y2) {
            return
        }
        unmarkBlock(index, x1, y1)
        markBlock(index, x2, y2)
    }
}
