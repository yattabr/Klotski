package br.com.wobbu.klotski.model;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import br.com.wobbu.klotski.R;
import br.com.wobbu.klotski.utils.Record;

import java.util.Stack;


public class GameBoard extends RelativeLayout {
    public int[][] board;
    public int lineBlockWidth;
    public int lineWidth;
    public int blockWidth;
    public Block[] blocks;
    public int[] positionList;
    public int padding;
    public View[] border;
    public int index;
    public TypedArray gameBoardList;
    public boolean isSuccess;
    public int isMoving;
    public Stack<Record> record;


    public GameBoard(Context context) {
        super(context);
//        init(null, 0);
    }

    public GameBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
//        init(attrs, 0);
    }

    public GameBoard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        init(attrs, defStyle);
    }

    public void init() {
        isSuccess = false;
        isMoving = 0;
        board = new int[5][4];
        blocks = new Block[10];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                board[i][j] = -1;
            }
        }

        post(new Runnable() {
            @Override
            public void run() {
                removeAllViews();
                padding = (getRight() - getLeft()) * 3 / 100;
                lineBlockWidth = (getRight() - getLeft() - padding * 2) / 4;
                LayoutParams lp = (LayoutParams) getLayoutParams();
                lineWidth = (int) (0.04 * lineBlockWidth);
                lp.height = lineBlockWidth * 5 + padding * 2;
                lp.width = lineBlockWidth * 4 + padding * 2;
                padding += (int) (1.5 * lineWidth);
                blockWidth = lineBlockWidth - lineWidth;
                setLayoutParams(lp);
                initBoard();
                initBorder();
                System.gc();
            }
        });
    }

    public void moveBlock(int index, int x, int y) {
        int l = x * blockWidth + padding;
        int t = y * blockWidth + padding;
        int r = l + blocks[index].spanX * lineBlockWidth - (blocks[index].spanX - 1) * lineWidth;
        int b = t + blocks[index].spanY * lineBlockWidth - (blocks[index].spanY - 1) * lineWidth;
        blocks[index].layout(l, t, r, b);
        blocks[index].x = x;
        blocks[index].y = y;
        markBlock(index, x, y);
    }

    public void markBlock(int index, int x, int y) {
        for (int i = 0; i < blocks[index].spanX; i++) {
            for (int j = 0; j < blocks[index].spanY; j++) {
                board[j + y][i + x] = index;
            }
        }
        printBoard();
    }

    public void unmarkBlock(int index, int x, int y) {
        for (int i = 0; i < blocks[index].spanX; i++) {
            for (int j = 0; j < blocks[index].spanY; j++) {
                board[j + y][i + x] = -1;
            }
        }
    }

    public void printBoard() {
        String boardString = "board: \n";
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                boardString = boardString + board[i][j] + "\t";
            }
            boardString = boardString + "\n";
        }
        Log.d("printBoard", boardString);
    }

    public void initBoard() {
        TypedArray imageList = getResources().obtainTypedArray(R.array.game_board_image);
        for (int i = 0; i < 10; i += 1) {
            final int index = i;
            final int spanX = positionList[i * 4];
            final int spanY = positionList[i * 4 + 1];
            final int x = positionList[i * 4 + 2];
            final int y = positionList[i * 4 + 3];
            blocks[index] = new Block(this, index, spanX, spanY, imageList.getDrawable(index));
            addView(blocks[index], new LayoutParams(lineBlockWidth * spanX, lineBlockWidth * spanY));
            blocks[index].post(new Runnable() {
                @Override
                public void run() {
                    blocks[index].init();
                    moveBlock(index, x, y);
                }
            });
        }
    }

    public void initBorder() {
        border = new View[5];
        for (int i = 0; i < 5; i++) {
            border[i] = new View(getContext());
            border[i].setBackgroundResource(R.drawable.board);
        }
        addView(border[0], new LayoutParams(lineBlockWidth * 4 + padding * 2, padding));
        addView(border[1], new LayoutParams(padding, lineBlockWidth * 5 + padding * 2));
        addView(border[2], new LayoutParams(padding, lineBlockWidth * 5 + padding * 2));
        addView(border[3], new LayoutParams(lineBlockWidth + padding, padding));
        addView(border[4], new LayoutParams(lineBlockWidth + padding, padding));
        final int offsetX = lineWidth * 3;
        final int offsetY = lineWidth * 4;
        border[0].post(new Runnable() {
            @Override
            public void run() {
                border[0].layout(0, 0, lineBlockWidth * 4 + padding * 2 - offsetX, padding);
            }
        });
        border[1].post(new Runnable() {
            @Override
            public void run() {
                border[1].layout(0, 0, padding, lineBlockWidth * 5 + padding * 2);
            }
        });
        border[2].post(new Runnable() {
            @Override
            public void run() {
                border[2].layout(lineBlockWidth * 4 + padding - offsetX, 0, lineBlockWidth * 4 + padding * 2 - offsetX, lineBlockWidth * 5 + padding * 2);
            }
        });
        border[3].post(new Runnable() {
            @Override
            public void run() {
                border[3].layout(0, lineBlockWidth * 5 + padding - offsetY, lineBlockWidth + padding, lineBlockWidth * 5 + padding * 2);
            }
        });
        border[4].post(new Runnable() {
            @Override
            public void run() {
                border[4].layout(lineBlockWidth * 3 + padding - offsetX, lineBlockWidth * 5 + padding - offsetY, lineBlockWidth * 4 + padding * 2 - offsetX, lineBlockWidth * 5 + padding * 2);
            }
        });
    }

    public void step(int index, int x1, int y1, int x2, int y2) {
        if (x1 == x2 && y1 == y2) {
            return;
        }
        unmarkBlock(index, x1, y1);
        markBlock(index, x2, y2);
        record.push(new Record(index, x1, y1, x2, y2));
    }
}
