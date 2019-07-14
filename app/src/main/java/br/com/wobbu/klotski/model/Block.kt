package br.com.wobbu.klotski.model

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView

import java.lang.Math.abs


class Block : android.support.v7.widget.AppCompatImageView {
    lateinit var mGameBoard: GameBoard
    private var currentX: Int = 0
    private var currentY: Int = 0
    private var movable: BooleanArray? = null
    private var start: Int = 0
    private var end: Int = 0
    private var begin: Int = 0
    private var direction: Int = 0
    private var flag: Boolean = false
    var spanX: Int = 0
    var spanY: Int = 0
    var x: Int = 0
    var y: Int = 0
    var index: Int = 0
    var padding: Int = 0
    private var temp1: Int = 0
    private var temp2: Int = 0


    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(
        gameBoard: GameBoard,
        _index: Int,
        _spanX: Int,
        _spanY: Int,
        drawable: Drawable
    ) : super(gameBoard.context) {
        mGameBoard = gameBoard
        padding = gameBoard.padding
        index = _index
        spanX = _spanX
        spanY = _spanY
        setBackgroundDrawable(null)
        background = null
        setImageDrawable(drawable)
        setPadding(0, 0, 0, 0)
        scaleType = ImageView.ScaleType.FIT_XY
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    fun init(attrs: AttributeSet?, defStyle: Int) {
        movable = booleanArrayOf(false, false, false, false)
        x = 0
        y = 0
    }

    fun checkMovable() {
        val board = mGameBoard.board
        for (i in 0..3) {
            this.movable?.set(i, true)
        }
        if (x - 1 >= 0) {
            for (i in 0 until spanY) {
                if (board[y + i][x - 1] != -1) {
                    movable?.set(0, false)
                    break
                }
            }
        } else {
            movable?.set(0, false)
        }
        if (x + spanX <= 3) {
            for (i in 0 until spanY) {
                if (board[y + i][x + spanX] != -1) {
                    movable?.set(2, false)
                    break
                }
            }
        } else {
            movable?.set(2, false)
        }
        if (y - 1 >= 0) {
            for (i in 0 until spanX) {
                if (board[y - 1][x + i] != -1) {
                    movable?.set(1, false)
                    break
                }
            }
        } else {
            movable?.set(1, false)
        }
        if (y + spanY <= 4) {
            for (i in 0 until spanX) {
                if (board[y + spanY][x + i] != -1) {
                    movable?.set(3, false)
                    break
                }
            }
        } else {
            movable?.set(3, false)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mGameBoard.isMoving != index && mGameBoard.isMoving != -1 || mGameBoard.isSuccess) {
            return super.onTouchEvent(event)
        }
        val action = event.action
        val X = event.rawX.toInt()
        val Y = event.rawY.toInt()
        val deltaX = X - currentX
        val deltaY = Y - currentY
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                checkMovable()
                mGameBoard.isMoving = index
                currentX = X
                currentY = Y
                direction = 0
                flag = true
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (flag) {
                    if ((movable!![1] || movable!![3]) && (movable!![0] || movable!![2])) {
                        direction = if (abs(deltaX) > abs(deltaY)) 1 else 2
                    } else if (movable!![0] || movable!![2]) {
                        direction = 1
                    } else if (movable!![1] || movable!![3]) {
                        direction = 2
                    } else {
                        direction = 0
                    }
                    if (direction == 1) {
                        begin = currentX
                        if (movable!![0] && movable!![2]) {
                            start = -mGameBoard.blockWidth
                            end = mGameBoard.blockWidth
                        } else if (movable!![0]) {
                            start = -mGameBoard.blockWidth
                            end = 0
                        } else {
                            start = 0
                            end = mGameBoard.blockWidth
                        }

                    } else if (direction == 2) {
                        begin = currentY
                        if (movable!![1] && movable!![3]) {
                            start = -mGameBoard.blockWidth
                            end = mGameBoard.blockWidth
                        } else if (movable!![1]) {
                            start = -mGameBoard.blockWidth
                            end = 0
                        } else {
                            start = 0
                            end = mGameBoard.blockWidth
                        }
                    }
                    flag = false
                }

                if (direction == 1) {
                    val offset = X - begin
                    if (offset <= end && offset >= start) {
                        layout(left + deltaX, top, right + deltaX, bottom)
                        currentX = X
                    } else if (offset > end) {
                        temp1 = x * mGameBoard.blockWidth + end + padding
                        temp2 = temp1 + spanX * mGameBoard.blockWidth + mGameBoard.lineWidth
                        layout(temp1, top, temp2, bottom)
                        currentX = begin + end
                        if (end == 0) {
                            flag = true
                        }
                    } else {
                        temp1 = x * mGameBoard.blockWidth + start + padding
                        temp2 = temp1 + spanX * mGameBoard.blockWidth + mGameBoard.lineWidth
                        layout(temp1, top, temp2, bottom)
                        currentX = begin + start
                        if (start == 0) {
                            flag = true
                        }
                    }
                } else if (direction == 2) {
                    val offset = Y - begin
                    if (offset <= end && offset >= start) {
                        layout(left, top + deltaY, right, bottom + deltaY)
                        currentY = Y
                    } else if (offset > end) {
                        layout(
                            left,
                            y * mGameBoard.blockWidth + end + padding,
                            right,
                            (y + spanY) * mGameBoard.blockWidth + end + padding + mGameBoard.lineWidth
                        )
                        currentY = begin + end
                        if (end == 0) {
                            flag = true
                        }
                    } else {
                        layout(
                            left,
                            y * mGameBoard.blockWidth + start + padding,
                            right,
                            (y + spanY) * mGameBoard.blockWidth + start + padding + mGameBoard.lineWidth
                        )
                        currentY = begin + start
                        if (start == 0) {
                            flag = true
                        }
                    }
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                var newX = x
                var newY = y
                if (direction == 1) {
                    val offset = currentX - begin
                    if (offset >= end / 2 && offset <= end) {
                        layout(
                            x * mGameBoard.blockWidth + end + padding,
                            top,
                            (x + spanX) * mGameBoard.blockWidth + end + padding + mGameBoard.lineWidth,
                            bottom
                        )
                        newX += end / mGameBoard.blockWidth
                    } else if (offset <= start / 2 && offset >= start) {
                        layout(
                            x * mGameBoard.blockWidth + start + padding,
                            top,
                            (x + spanX) * mGameBoard.blockWidth + start + padding + mGameBoard.lineWidth,
                            bottom
                        )
                        newX += start / mGameBoard.blockWidth
                    } else {
                        layout(
                            x * mGameBoard.blockWidth + padding,
                            top,
                            (x + spanX) * mGameBoard.blockWidth + padding + mGameBoard.lineWidth,
                            bottom
                        )
                    }
                } else if (direction == 2) {
                    val offset = currentY - begin
                    if (offset >= end / 2 && offset <= end) {
                        layout(
                            left,
                            y * mGameBoard.blockWidth + end + padding,
                            right,
                            (y + spanY) * mGameBoard.blockWidth + end + padding + mGameBoard.lineWidth
                        )
                        newY += end / mGameBoard.blockWidth
                    } else if (offset <= start / 2 && offset >= start) {
                        layout(
                            left,
                            y * mGameBoard.blockWidth + start + padding,
                            right,
                            (y + spanY) * mGameBoard.blockWidth + start + padding + mGameBoard.lineWidth
                        )
                        newY += start / mGameBoard.blockWidth
                    } else {
                        layout(
                            left,
                            y * mGameBoard.blockWidth + padding,
                            right,
                            (y + spanY) * mGameBoard.blockWidth + padding + mGameBoard.lineWidth
                        )
                    }
                }
                mGameBoard.step(index, x, y, newX, newY)
                x = newX
                y = newY
                mGameBoard.isMoving = -1
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}
