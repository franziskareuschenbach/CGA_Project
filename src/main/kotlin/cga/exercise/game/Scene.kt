package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.Material
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.VertexAttribute
import cga.exercise.components.light.SpotLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.OBJLoader
import org.joml.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL30.*

/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram

    private var groundMesh : Mesh

    private var bodyMesh : Mesh
    private var body = Renderable()

    private var ground = Renderable()
    private var camera = TronCamera()

    private var spotLight : SpotLight

    private var oldMousePosX = -1.0
    private var oldMousePosY = -1.0
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

        val resBody = OBJLoader.loadOBJ("assets/models/kreis.obj")
        val bodyObj = resBody.objects[0].meshes[0]
        bodyMesh = Mesh(bodyObj.vertexData, bodyObj.indexData, vertexAttributes)
        body.list.add(bodyMesh)

        //Lights
        spotLight = SpotLight(Vector3f(0.0f, 1.0f, -2.0f), Vector3f(1.0f))

        //Transform
        spotLight.rotateLocal(Math.toRadians(-10.0f), Math.PI.toFloat(), 0.0f) //PI??

        body.translateLocal(Vector3f(0.0f, 1.0f, 0.0f))

        //Camera
        camera.translateLocal(Vector3f(0.0f, 1.0f, 0.0f))

        //Parent
        camera.parent = body
        spotLight.parent = body

    }

    fun setTimer(t: Float){
        if ( window.currentTime >= t){
            cleanup()
            println("Du hast zu lange gebraucht! Du bist gefeuert!!!!")
        }
    }

    fun render(dt: Float, t: Float) { //-> t == window.currentTime
        println(t)
        setTimer(240f) //4Min

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        staticShader.setUniform("col", Vector3f(0.4235f, 0.4745f, 0.5804f)) //Paynes Grey

        camera.bind(staticShader)
        ground.render(staticShader)

        //body.render(staticShader)

        spotLight.bind(staticShader, "mCycleSpot", camera.getCalculateViewMatrix())
    }

    fun update(dt: Float, t: Float) {
        if(window.getKeyState(GLFW_KEY_W))
            body.translateLocal(Vector3f(0.0f, 0.0f, -5 * dt))

        if(window.getKeyState(GLFW_KEY_A))
            body.rotateLocal(0.0f, 1 * dt, 0.0f)

        if(window.getKeyState(GLFW_KEY_D))
            body.rotateLocal(0.0f, -1f * dt, 0.0f)

        if(window.getKeyState(GLFW_KEY_S))
            body.translateLocal(Vector3f(0.0f, 0.0f, 5 * dt))
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {
        val deltaX = xpos - oldMousePosX
        val deltaY = ypos - oldMousePosY

        oldMousePosX = xpos
        oldMousePosY = ypos

        if (bool) {
            body.rotateAroundPoint(0.0f, -Math.toRadians(deltaX.toFloat() * 0.02f) ,0.0f, body.getPosition())
            camera.rotateLocal(-Math.toRadians(deltaY.toFloat() * 0.04f),0.0f, 0.0f)
        }
        bool = true
    }

    fun cleanup() {
        window.quit()
    }
}
