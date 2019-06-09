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
            field = value
            left_character.text = if (score < 10) "" else (score / 10).toString()
            right_character.text = (score % 10).toString()
        }

    private var scoreInternal: Int
        get() = this.score
        set(value) {
            score = value
            scoreChangeListener.invoke(score)
        }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        init()
        if (attrs != null) applyAttributes(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
        applyAttributes(attrs)
    }

    private fun init() {
        View.inflate(context, R.layout.score_view, this)
        applyFont("Let's go Digital Regular.ttf", left_character, right_character)
        down.setOnClickListener { scoreInternal = score - 1 }
        up.setOnClickListener { scoreInternal = score + 1 }
        listOf(down, up).forEach {
            it.setOnLongClickListener {
                scoreInternal = 0
                true
            }
        }
    }

    private fun applyAttributes(attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.ScoreView,
                0, 0)
        val score: Int
        try {
            score = a.getInteger(R.styleable.ScoreView_score, 0)
        } finally {
            a.recycle()
        }
        this.score = score
    }

    private fun applyFont(fontPath: String, vararg views: TextView) {
        val font = Typeface.createFromAsset(context.assets, fontPath)
        for (view in views) {
            view.typeface = font
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
    }
}
