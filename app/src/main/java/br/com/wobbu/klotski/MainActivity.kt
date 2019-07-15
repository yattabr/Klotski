package br.com.wobbu.klotski

import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import br.com.wobbu.klotski.model.GameBoard
import br.com.wobbu.klotski.utils.Record
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var mGameBoard: GameBoard
    var index: Int = 0
    var baseTimer: Long = 0
    lateinit var record: Stack<Record>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mGameBoard = findViewById(R.id.game_board_layout)
        mGameBoard.index = index
        record = Stack()
        mGameBoard.record = record
        mGameBoard.positionList = resources.getIntArray(R.array.game_board_1)
        mGameBoard.index = 0
        mGameBoard.init()

        mGameBoard.init()
        this.baseTimer = SystemClock.elapsedRealtime()
    }
}
