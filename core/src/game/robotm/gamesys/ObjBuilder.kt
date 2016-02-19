package game.robotm.gamesys

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.Array
import game.robotm.ecs.components.*
import java.util.*


object ObjBuilder {

    var assetManager: AssetManager? = null
    var world: World? = null
    var engine: Engine? = null

    fun createPlayer(x: Float, y: Float) {

        val scale = GM.PLAYER_SCALE

        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.position.set(x, y)
        bodyDef.fixedRotation = true

        val body = world!!.createBody(bodyDef)

        val boxShape = PolygonShape()
        boxShape.setAsBox(0.3f * scale, 0.3f * scale)
        val fixtureDef = FixtureDef()
        fixtureDef.shape = boxShape
        fixtureDef.density = 0.5f
        fixtureDef.filter.categoryBits = GM.CATEGORY_BITS_PLAYER.toShort()
        fixtureDef.filter.maskBits = GM.MASK_BITS_PLAYER.toShort()
        fixtureDef.friction = 0f

        body.createFixture(fixtureDef)
        boxShape.dispose()

        val edgeShape = EdgeShape()
        // sides
        edgeShape.set(Vector2(-0.45f, 0.3f).scl(scale), Vector2(-0.45f, -0.375f).scl(scale))
        fixtureDef.shape = edgeShape
        fixtureDef.friction = 0f
        fixtureDef.filter.categoryBits = GM.CATEGORY_BITS_PLAYER.toShort()
        fixtureDef.filter.maskBits = GM.MASK_BITS_PLAYER.toShort()
        body.createFixture(fixtureDef)

        edgeShape.set(Vector2(0.45f, 0.3f).scl(scale), Vector2(0.45f, -0.375f).scl(scale))
        fixtureDef.shape = edgeShape
        body.createFixture(fixtureDef)

        // feet
        edgeShape.set(Vector2(-0.45f, -0.375f).scl(scale), Vector2(0.45f, -0.375f).scl(scale))
        fixtureDef.shape = edgeShape
        fixtureDef.friction = 0.8f
        body.createFixture(fixtureDef)
        edgeShape.dispose()

        val textureAtlas = assetManager!!.get("img/actors.atlas", TextureAtlas::class.java)
        val textureRegion = textureAtlas.findRegion("RobotM")

        val animations = HashMap<String, Animation>()
        var anim: Animation

        var keyFrames = Array<TextureRegion>()

        // idle animation
        keyFrames.add(TextureRegion(textureRegion, 64 * 3, 0, 64, 48))
        anim = Animation(0.1f, keyFrames, Animation.PlayMode.LOOP)
        animations.put("idle", anim)

        keyFrames.clear()

        // move animation
        for (i in 0..1) {
            keyFrames.add(TextureRegion(textureRegion, 64 * (3 + i), 0, 64, 48))
        }
        anim = Animation(0.1f, keyFrames, Animation.PlayMode.LOOP)
        animations.put("move", anim)

        keyFrames.clear()

        // fall animation
        keyFrames.add(TextureRegion(textureRegion, 64 * 6, 0, 64, 48))
        anim = Animation(0.1f, keyFrames, Animation.PlayMode.NORMAL)
        animations.put("fall", anim)

        val entity = Entity()
        entity.add(PlayerComponent())
        entity.add(PhysicsComponent(body))
        entity.add(RendererComponent(TextureRegion(textureRegion, 64 * 3, 0, 64, 48), 64 / GM.PPM * scale, 48 / GM.PPM * scale))
        entity.add(AnimationComponent(animations, "idle"))
        entity.add(TransformComponent(x, y))

        engine!!.addEntity(entity)

        body.userData = entity
    }

    private fun createWallBody(x: Float, y: Float): Body {

        val bodyDef = BodyDef()
        bodyDef.position.set(x, y)
        bodyDef.type = BodyDef.BodyType.StaticBody

        val body = world!!.createBody(bodyDef)

        val shape = PolygonShape()
        shape.setAsBox(0.5f, 0.5f)

        val fixtureDef = FixtureDef()
        fixtureDef.shape = shape
        fixtureDef.filter.categoryBits = GM.CATEGORY_BITS_STATIC_OBSTACLE.toShort()
        fixtureDef.filter.maskBits = GM.MASK_BITS_STATIC_OBSTACLE.toShort()
        fixtureDef.friction = 0f

        body.createFixture(fixtureDef)
        shape.dispose()

        return body
    }

    fun createWall(left: Float, right: Float, top: Float, height: Int, type: String = "Grass") {

        val textureAtlas = assetManager!!.get("img/actors.atlas", TextureAtlas::class.java)
        val textureRegion = TextureRegion(textureAtlas.findRegion(type), 64 * 5, 0, 64, 64)

        var body: Body
        var entity: Entity
        for (i in 0..height - 1) {
            body = createWallBody(left, top - i)
            entity = Entity()
            entity.add(TransformComponent(left, top - i))
            entity.add(PhysicsComponent(body))
            entity.add(RendererComponent(textureRegion, 1f, 1f))
            body.userData = entity
            engine!!.addEntity(entity)

            body = createWallBody(right, top - i)
            entity = Entity()
            entity.add(TransformComponent(right, top - i))
            entity.add(PhysicsComponent(body))
            entity.add(RendererComponent(textureRegion, 1f, 1f))
            body.userData = entity
            engine!!.addEntity(entity)

        }
    }

    fun createFloor(x: Float, y: Float, width: Int, type: String = "Grass") {
        val textureAtlas = assetManager!!.get("img/actors.atlas", TextureAtlas::class.java)

        for (i in 0..width - 1) {

            val bodyDef = BodyDef()
            bodyDef.type = BodyDef.BodyType.StaticBody
            bodyDef.position.set(x + i, y)

            val body = world!!.createBody(bodyDef)

            val shape = PolygonShape()
            shape.setAsBox(0.5f, 0.5f)

            val fixtureDef = FixtureDef()
            fixtureDef.shape = shape

            when (type) {
                "Purple" -> {
                    fixtureDef.filter.categoryBits = GM.CATEGORY_BITS_STATIC_OBSTACLE_UNJUMPABLE.toShort()
                    fixtureDef.filter.maskBits = GM.MAST_BITS_STATIC_OBSTACLE_UNJUMPABLE.toShort()
                }
                else -> {
                    fixtureDef.filter.categoryBits = GM.CATEGORY_BITS_STATIC_OBSTACLE.toShort()
                    fixtureDef.filter.maskBits = GM.MASK_BITS_STATIC_OBSTACLE.toShort()
                }
            }



            when (type) {
                "Grass" -> fixtureDef.friction = 0.8f
                "Sand" -> fixtureDef.friction = 1f
                "Choco" -> fixtureDef.friction = 0.01f
                "Purple" -> fixtureDef.friction = 0.5f
                else -> fixtureDef.friction = 0.8f
            }

            body.createFixture(fixtureDef)
            shape.dispose()

            val textureRegion: TextureRegion

            if (width == 1) {
                textureRegion = TextureRegion(textureAtlas.findRegion(type), 0, 0, 64, 64)
            } else {
                when (i) {
                    0 -> textureRegion = TextureRegion(textureAtlas.findRegion(type), 64 * 1, 0, 64, 64)
                    width - 1 -> textureRegion = TextureRegion(textureAtlas.findRegion(type), 64 * 3, 0, 64, 64)
                    else -> textureRegion = TextureRegion(textureAtlas.findRegion(type), 64 * 2, 0, 64, 64)
                }
            }

            val entity = Entity()
            entity.add(TransformComponent(x + i, y))
            entity.add(PhysicsComponent(body))
            entity.add(RendererComponent(textureRegion, 1f, 1f))

            engine!!.addEntity(entity)

            body.userData = entity
        }
    }

    fun generateFloors(start: Float, height: Int, leftBound: Float, rightBound: Float, type: String = "Grass") {

        var gap: Int = MathUtils.random(4, 6)
        var length: Int = MathUtils.random(4, 6)

        var x: Float = MathUtils.random(leftBound.toInt() + 1, rightBound.toInt() - length - 2) + 0.5f
        var y: Float = MathUtils.floor(start) - gap + 0.5f

        while (y >= start - height) {

            if (gap >= 5 || MathUtils.random() > 0.05f) {
                createFloor(x, y, length, type)
            }

            x = MathUtils.random(-GM.SCREEN_WIDTH.toInt() / 2 + 1, GM.SCREEN_WIDTH.toInt() / 2 - length - 2) + 0.5f
            y -= gap

            gap = MathUtils.random(4, 6)
            length = MathUtils.random(4, 6)
        }

    }

    fun generateFloorsAndWalls(start: Float, height: Int, left: Float = -GM.SCREEN_WIDTH / 2f + 0.5f, right: Float = GM.SCREEN_WIDTH / 2f - 0.5f) {
        val types = arrayOf("Grass", "Sand", "Choco", "Purple")

        var floorType: String
        var wallType: String

        if (start > -150f) {
            floorType = types[0]
            wallType = types[0]
        } else if (start > -300f) {
            floorType = types[1]
            wallType = types[1]
        } else if (start > -450f) {
            floorType = types[2]
            wallType = types[2]
        } else if (start > -600f) {
            floorType = types[3]
            wallType = types[3]
        } else {
            // random floor type
            floorType = types[MathUtils.random(0, types.size - 1)]
            wallType = "Purple"
        }

        generateFloors(start, height, left, right, floorType)
        createWall(left, right, start, height, wallType)

    }

    fun createSpike(x: Float, y: Float) {

    }

    fun generateSpike(x: Float, y: Float, length: Int) {

    }
}