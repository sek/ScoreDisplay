package com.stankurdziel.scoredisplay

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.main.*

class MainActivity : Activity() {
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
        if (savedInstanceState != null) score_view.score = savedInstanceState.getInt(SCORE, 0)
        score_view.scoreChangeListener = { scoreReference?.setValue(it) }

        fab.setOnClickListener { startActivity(Intent(this@MainActivity, SettingsActivity::class.java)) }
    }

    private fun subscribeToServerSideDbChanges(scoreReference: DatabaseReference?) {
        scoreReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                score_view.score = dataSnapshot.getValue(Int::class.java)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("ScoreDisplay", "onCancelled(): " + databaseError.message)
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(SCORE, score_view.score)
        super.onSaveInstanceState(outState)
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
        // window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.attributes.screenBrightness = 1.0f
    }

    companion object {
        private const val SCORE = "SCORE"
    }
}
