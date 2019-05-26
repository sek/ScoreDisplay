package com.stankurdziel.scoredisplay

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.main.*

class MainActivity : Activity() {

    private var score = 0
    private lateinit var scoreReference:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        val deviceId = FirebaseInstanceId.getInstance().id
        val database = FirebaseDatabase.getInstance()
        scoreReference = database.getReference("scores/$deviceId")
        scoreReference.setValue(0)
        subscribeToServerSideDbChanges(scoreReference)

        val font = Typeface.createFromAsset(assets, "Let's go Digital Regular.ttf")
        scoreL!!.typeface = font
        scoreR!!.typeface = font
        scoreL!!.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        scoreR!!.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        if (savedInstanceState != null) {
            setScore(savedInstanceState.getInt(SCORE, 0))
        }

        val onClick = View.OnClickListener { v ->
            when (v.id) {
                R.id.down -> incrementScore()
                R.id.up -> decrementScore()
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

    private fun subscribeToServerSideDbChanges(scoreReference: DatabaseReference) {
        // TODO trigger this when user selects to show barcode
        scoreReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                setScore(dataSnapshot.getValue(Int::class.java))
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

    private fun decrementScore() {
        if (score < 99) setScore(score + 1)
    }

    private fun incrementScore() {
        if (score > 0) setScore(score - 1)
    }

    private fun setScore(i: Int) {
        score = i
        // TODO test with no inet connection
        scoreReference.setValue(score)
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
        private val SCORE = "SCORE"
    }
}
