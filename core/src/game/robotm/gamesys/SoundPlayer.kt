package game.robotm.gamesys

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound


object SoundPlayer {

    var readySound: Sound? = null
    var goSound: Sound? = null
    var gameOverSound: Sound? = null
    var damagedSound: Sound? = null
    var explodeSound: Sound? = null
    var jumpSound: Sound? = null
    var springSound: Sound? = null
    var cantJumpSound: Sound? = null
    var engineSound: Sound? = null
    var powerUpSound: Sound? = null

    fun load(assetManager: AssetManager) {
        readySound = assetManager.get("sounds/ready.ogg", Sound::class.java)
        goSound = assetManager.get("sounds/go.ogg", Sound::class.java)
        gameOverSound = assetManager.get("sounds/game_over.ogg", Sound::class.java)
        damagedSound = assetManager.get("sounds/damaged.ogg", Sound::class.java)
        explodeSound = assetManager.get("sounds/explode.ogg", Sound::class.java)
        jumpSound = assetManager.get("sounds/jump.ogg", Sound::class.java)
        springSound = assetManager.get("sounds/spring.ogg", Sound::class.java)
        cantJumpSound = assetManager.get("sounds/cant_jump.ogg", Sound::class.java)
        engineSound = assetManager.get("sounds/engine.ogg", Sound::class.java)
        powerUpSound = assetManager.get("sounds/power_up.ogg", Sound::class.java)
    }

    fun getSound(sound: String): Sound {
        when (sound) {
            "ready" -> return readySound!!
            "go" -> return goSound!!
            "game_over" -> return gameOverSound!!
            "damaged" -> return damagedSound!!
            "explode" -> return explodeSound!!
            "jump" -> return jumpSound!!
            "spring" -> return springSound!!
            "cant_jump" -> return cantJumpSound!!
            "engine" -> return engineSound!!
            "power_up" -> return powerUpSound!!
            else -> return readySound!!
        }
    }

    fun play(sound: String): Long {
        when (sound) {
            "ready" -> return readySound!!.play()
            "go" -> return goSound!!.play()
            "game_over" -> return gameOverSound!!.play()
            "damaged" -> return damagedSound!!.play()
            "explode" -> return explodeSound!!.play()
            "jump" -> return jumpSound!!.play()
            "spring" -> return springSound!!.play()
            "cant_jump" -> return cantJumpSound!!.play()
            "engine" -> return engineSound!!.play()
            "power_up" -> return powerUpSound!!.play()
            else -> return -1L
        }
    }
}