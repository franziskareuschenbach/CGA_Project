package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.Material
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.VertexAttribute
import cga.exercise.components.light.PointLight
import cga.exercise.components.light.SpotLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.ModelLoader
import cga.framework.OBJLoader
import org.joml.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL30.*
import java.lang.IllegalArgumentException
import kotlin.math.*

/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram

    private var groundMesh : Mesh

    private var mCycle = ModelLoader.loadModel("assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj", Math.toRadians(-90f),
                                                Math.toRadians(90f),0f) ?: throw IllegalArgumentException("no model.")

    private var ground = Renderable()
    private var camera = TronCamera()

    private var pointLight : PointLight
    private var spotLight : SpotLight

    private var oldMousePosX = -1.0
    //private var oldMousePosY = -1.0
    private var bool = false

    //scene setup
    init {
        staticShader = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")

        //initial opengl state
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow()
        glEnable(GL_CULL_FACE); GLError.checkThrow()
        glFrontFace(GL_CCW); GLError.checkThrow()
        glCullFace(GL_BACK); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()


        /**Create the mesh**/
        val stride = 8 * 4
        val attrPos = VertexAttribute(0,3,GL_FLOAT, stride, 0)        //position
        val attrTC = VertexAttribute(1,2,GL_FLOAT, stride, 3 * 4)     //textureCoordinate
        val attrNorm = VertexAttribute(2,3, GL_FLOAT, stride, 5 * 4)  //normal
        val vertexAttributes = arrayOf<VertexAttribute>(attrPos, attrTC, attrNorm)


        //Material
        val texture_emit = Texture2D("assets/textures/ground_emit.png", true)
        val texture_diff = Texture2D("assets/textures/ground_diff.png",true)
        val texture_spec = Texture2D("assets/textures/ground_spec1.png",true)

        texture_emit.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        texture_diff.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        texture_spec.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        val groundMaterial = Material(texture_diff, texture_emit, texture_spec, 60.0f, Vector2f(64.0f, 64.0f))

        /**Loads Ground and create a mesh**/
        val resGround = OBJLoader.loadOBJ("assets/models/ground.obj")
        val groundObj = resGround.objects[0].meshes[0]
        groundMesh = Mesh(groundObj.vertexData, groundObj.indexData, vertexAttributes, groundMaterial)
        ground.list.add(groundMesh)

        //Lights
        pointLight = PointLight(camera.getWorldPosition(), Vector3f(1.0f))
        spotLight = SpotLight(Vector3f(0.0f, 1.0f, -2.0f), Vector3f(1.0f))

        //Transform
        pointLight.translateLocal(Vector3f(0.0f, 4.0f, 0.0f))
        spotLight.rotateLocal(Math.toRadians(-10.0f), Math.PI.toFloat(), 0.0f) //PI??

        mCycle.scaleLocal(Vector3f(0.8f))

        //Camera
        camera.rotateLocal(Math.toRadians(-40.0f), 0.0f, 0.0f)              //-20 Grad in Bogenmaß umgerechnet-0.34f
        camera.translateLocal(Vector3f(0.0f, 0.0f, 4.0f))

        //Parent
        camera.parent = mCycle
        pointLight.parent = mCycle
        spotLight.parent = mCycle

    }

    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        //staticShader.setUniform("col", Vector3f(1.0f, 0.0f, 0.0f))
        //staticShader.setUniform("col", Vector3f(1.0f, 1.0f, 1.0f)) //Weiss
        staticShader.setUniform("col", Vector3f(0.4235f, 0.4745f, 0.5804f)) //Paynes Grey
        //staticShader.setUniform("col", Vector3f(abs(sin(t / 5)), abs(sin(t / 3)), abs(sin(t / 2))))

        camera.bind(staticShader)
        ground.render(staticShader)

        staticShader.setUniform("col", Vector3f(abs(sin(t / 1)), abs(sin(t / 3)), abs(sin(t / 2))))
        mCycle.render(staticShader)

        pointLight.bind(staticShader, "mCyclePoint")
        pointLight.lightCol = Vector3f(abs(sin(t / 1)), abs(sin(t / 3)), abs(sin(t / 2)))

        spotLight.bind(staticShader, "mCycleSpot", camera.getCalculateViewMatrix())
        //spotLight.lightCol = Vector3f(abs(sin(t / 14)), abs(sin(t / 8)), abs(sin(t / 6)))

    }

    fun update(dt: Float, t: Float) {
        if(window.getKeyState(GLFW_KEY_W)) {
            mCycle.translateLocal(Vector3f(0.0f, 0.0f, -5 * dt))

            if(window.getKeyState(GLFW_KEY_A))
                mCycle.rotateLocal(0.0f, 1 * dt, 0.0f)

            if(window.getKeyState(GLFW_KEY_D))
                mCycle.rotateLocal(0.0f, -1f * dt, 0.0f)
        }

        if(window.getKeyState(GLFW_KEY_S)){
            mCycle.translateLocal(Vector3f(0.0f, 0.0f, 5 * dt))

            if(window.getKeyState(GLFW_KEY_A))
                mCycle.rotateLocal(0.0f, 1f * dt, 0.0f)

            if(window.getKeyState(GLFW_KEY_D))
                mCycle.rotateLocal(0.0f, -1f * dt, 0.0f)
        }
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {
        val deltaX = xpos - oldMousePosX
        //val deltaY = ypos - oldMousePosY

        oldMousePosX = xpos
        //oldMousePosY = ypos

        if (bool)
            camera.rotateAroundPoint(0.0f, Math.toRadians(deltaX.toFloat() * 0.02f) , //0.002 ist viel zu unempfindlich
                                    0.0f, Vector3f(0.0f))
        bool = true
    }

    fun cleanup() {}
}
