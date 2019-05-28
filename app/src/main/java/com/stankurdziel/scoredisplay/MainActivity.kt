package com.stankurdziel.scoredisplay

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.main.*

class MainActivity : Activity() {
    private var score = 0
    private var scoreReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        setupGui(savedInstanceState)
        setupScoreReference()
    }

    private fun setupScoreReference() {
        val deviceId = FirebaseInstanceId.getInstance().id
        val database = FirebaseDatabase.getInstance()
        scoreReference = database.getReference("scores/$deviceId")
        scoreReference?.setValue(0)
        subscribeToServerSideDbChanges(scoreReference)
    }

    private fun setupGui(savedInstanceState: Bundle?) {
        applyFont("Let's go Digital Regular.ttf", scoreL, scoreR)

        if (savedInstanceState != null) setScore(savedInstanceState.getInt(SCORE, 0))

        val onClick = View.OnClickListener {
            when (it.id) {
                R.id.down -> setScore(score - 1)
                R.id.up -> setScore(score + 1)
            }
        }
        val onLongClick = View.OnLongClickListener {
            setScore(0)
            true
        }
        down.setOnClickListener(onClick)
        down.setOnLongClickListener(onLongClick)
        up.setOnClickListener(onClick)

        fab.setOnClickListener { startActivity(Intent(this@MainActivity, SettingsActivity::class.java)) }
    }

    private fun applyFont(fontPath: String, vararg views: TextView) {
        val font = Typeface.createFromAsset(assets, fontPath)
        for (view in views) {
            view.typeface = font
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
    }

    private fun subscribeToServerSideDbChanges(scoreReference: DatabaseReference?) {
        // TODO trigger this when user selects to show barcode
        scoreReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                setScore(dataSnapshot.getValue(Int::class.java), false)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("ScoreDisplay", "onCancelled(): " + databaseError.message)
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(SCORE, score)
        super.onSaveInstanceState(outState)
    }

    private fun setScore(newScore: Int, persist: Boolean = true) {
        score = newScore
        if (score < 0) score = 0
        if (score > 99) score = 99
        if (persist) scoreReference?.setValue(score)
        updateUi()
    }

    private fun updateUi() {
        displayScore(score)
    }

    private fun displayScore(score: Int) {
        var leftCharacter = ""
        if (score / 10 != 0) leftCharacter = (score / 10).toString()
        scoreL!!.text = leftCharacter
        val rightCharacter = (score % 10).toString()
        scoreR!!.text = rightCharacter
    }

    override fun onResume() {
        super.onResume()
        setFullscreenFullBrightness()
    }

    private fun setFullscreenFullBrightness() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // TODO in future if score is 0 for a certain amount of time (10 mins?), could let screen turn off
        // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        val lp = window.attributes
        lp.screenBrightness = 1.0f
        window.attributes = lp
    }

    companion object {
        private const val SCORE = "SCORE"
    }
}
