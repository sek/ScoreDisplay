package com.stankurdziel.scoredisplay

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.score_view.view.*

class ScoreView : FrameLayout {
    var scoreChangeListener: (Int) -> Unit = {}
    var score: Int = 0
        set(value) {
            if (value !in 0..99) return
            field = value
            left_character.text = if (score < 10) "" else (score / 10).toString()
            right_character.text = (score % 10).toString()
        }

    private fun setScoreAndNotify(score: Int) {
        this.score = score
        scoreChangeListener.invoke(score)
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
        applyAttributes(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
        applyAttributes(attrs)
    }

    private fun init() {
        View.inflate(context, R.layout.score_view, this)
        applyFont("Let's go Digital Regular.ttf", left_character, right_character)
        down.setOnClickListener { setScoreAndNotify(score - 1) }
        up.setOnClickListener { setScoreAndNotify(score + 1) }
        listOf(down, up).forEach { it.setOnLongClickListener { setScoreAndNotify(0); true } }
    }

    private fun applyAttributes(attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.ScoreView, 0, 0)
        val scoreAttribute: Int
        try {
            scoreAttribute = a.getInteger(R.styleable.ScoreView_score, 0)
        } finally {
            a.recycle()
        }
        score = scoreAttribute
    }

    private fun applyFont(fontPath: String, vararg views: TextView) {
        for (view in views) {
            view.typeface = Typeface.createFromAsset(context.assets, fontPath)
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
    }
}
