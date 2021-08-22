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
import cga.framework.OBJLoader
import org.joml.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL30.*
import kotlin.random.Random
import kotlin.math.*

/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private var staticShader: ShaderProgram
    private var ohneSpotShader : ShaderProgram
    private var raveLightShader : ShaderProgram
    private var shader : ShaderProgram

    private var bodyMesh : Mesh
    private var body = Renderable()

    private var camera = TronCamera()

    private var spotLight : SpotLight

    private var corner1 : PointLight
    private var corner2 : PointLight
    private var corner3 : PointLight
    private var corner4 : PointLight

    private var roomLight1 : PointLight
    private var roomLight2 : PointLight
    private var roomLight3 : PointLight

    private var oldMousePosX = -1.0
    private var oldMousePosY = -1.0
    private var bool = false

    private var timer = 30.0f

    val collisions = mutableListOf<Vector4f>()

    private var floorMesh : Mesh
    private var floor = Renderable()

    /**Raumgeneration**/
    /**Startraum**/
    private var raumBaseMesh : Mesh
    private var raumBase = Renderable()

    private var raumBaseMoebelMesh : Mesh
    private var raumBaseMoebel = Renderable()

    /**Raum_links**/
    private var raumLMesh : Mesh
    private var raumL = Renderable()

    private var raumLMoebelMesh : Mesh
    private var raumLMoebel = Renderable()

    /**Raum_rechts**/
    private var raumRMesh : Mesh
    private var raumR = Renderable()

    private var raumRMoebelMesh : Mesh
    private var raumRMoebel = Renderable()

    /**SkyBox**/
    private var skyMesh : Mesh
    private var sky = Renderable()

    /**Balken**/
    private var balkenMesh : Mesh
    private var balken = Renderable()

    //scene setup
    init {
        staticShader = ShaderProgram("assets/shaders/mitSpot_vert.glsl", "assets/shaders/mitSpot_frag.glsl")
        ohneSpotShader = ShaderProgram("assets/shaders/ohneSpot_vert.glsl", "assets/shaders/ohneSpot_frag.glsl")
        raveLightShader = ShaderProgram("assets/shaders/raveLight_vert.glsl", "assets/shaders/raveLight_frag.glsl")
        shader = ohneSpotShader
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

        /**Material_Floor**/
        val floorTextureEmit = Texture2D("assets/textures/black.png", true)
        val floorTextureDiff = Texture2D("assets/textures/floor_diff.png",true)
        val floorTextureSpec = Texture2D("assets/textures/black.png",true)

        floorTextureEmit.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        floorTextureDiff.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        floorTextureSpec.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        val floorMaterial = Material(floorTextureDiff, floorTextureEmit, floorTextureSpec, 60.0f, Vector2f(64.0f, 64.0f))

        /**Floormesh**/
        val resFloor = OBJLoader.loadOBJ("assets/models/floor.obj")
        val floorObj = resFloor.objects[0].meshes[0]
        floorMesh = Mesh(floorObj.vertexData, floorObj.indexData, vertexAttributes, floorMaterial)
        floor.list.add(floorMesh)

        /**Material_Sky**/
        val skyTextureEmit = Texture2D("assets/textures/white.png", true)
        val skyTextureDiff = Texture2D("assets/textures/sky_diff.png",true)
        val skyTextureSpec = Texture2D("assets/textures/black.png",true)

        skyTextureEmit.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        skyTextureDiff.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        skyTextureSpec.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        val skyMaterial = Material(skyTextureDiff, skyTextureEmit, skyTextureSpec, 60.0f, Vector2f(1.0f, 1.0f))

        /**Skymesh**/
        val resSky = OBJLoader.loadOBJ("assets/models/skycube.obj")
        val skyObj = resSky.objects[0].meshes[0]
        skyMesh = Mesh(skyObj.vertexData, skyObj.indexData, vertexAttributes, skyMaterial)
        sky.list.add(skyMesh)

        /**Material_Raum**/
        val raumTextureEmit = Texture2D("assets/textures/black.png", true)
        val raumTextureDiff = Texture2D("assets/textures/raum_diff.png",true)
        val raumTextureSpec = Texture2D("assets/textures/black.png",true)

        raumTextureEmit.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        raumTextureDiff.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        raumTextureSpec.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        val raumMaterial = Material(raumTextureDiff, raumTextureEmit, raumTextureSpec, 60.0f, Vector2f(64.0f, 64.0f))

        /**Material_Möbel**/
        val moebelTextureEmit = Texture2D("assets/textures/black.png", true)
        val moebelTextureDiff = Texture2D("assets/textures/white.png",true)
        val moebelTextureSpec = Texture2D("assets/textures/black.png",true)

        moebelTextureEmit.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        moebelTextureDiff.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        moebelTextureSpec.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        val moebelMaterial = Material(moebelTextureDiff, moebelTextureEmit, moebelTextureSpec, 60.0f, Vector2f(64.0f, 64.0f))

        /**Loads Body and create a mesh**/
        val resBody = OBJLoader.loadOBJ("assets/models/body.obj")
        val bodyObj = resBody.objects[0].meshes[0]
        bodyMesh = Mesh(bodyObj.vertexData, bodyObj.indexData, vertexAttributes)
        body.list.add(bodyMesh)

        /**Balken generierung**/
        val balkenTextureEmit = Texture2D("assets/textures/red.png", true)
        val balkenTextureDiff = Texture2D("assets/textures/red.png",true)
        val balkenTextureSpec = Texture2D("assets/textures/red.png",true)

        balkenTextureEmit.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        balkenTextureDiff.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        balkenTextureSpec.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        val balkenMaterial = Material(balkenTextureDiff, balkenTextureEmit, balkenTextureSpec, 60.0f, Vector2f(64.0f, 64.0f))

        val resBalken = OBJLoader.loadOBJ("assets/models/balken.obj")
        val balkenObj = resBalken.objects[0].meshes[0]
        balkenMesh = Mesh(balkenObj.vertexData, balkenObj.indexData, vertexAttributes, balkenMaterial)
        balken.list.add(balkenMesh)

        /**Generation der Wohnung**/

        /**Startraum**/
        val resRaumBase = OBJLoader.loadOBJ("assets/models/raumBase.obj")
        val raumBaseObj = resRaumBase.objects[0].meshes[0]
        raumBaseMesh = Mesh(raumBaseObj.vertexData, raumBaseObj.indexData, vertexAttributes, raumMaterial)
        raumBase.list.add(raumBaseMesh)

        val resRaumBaseMoebel = OBJLoader.loadOBJ("assets/models/raumBaseMoebel.obj")
        val raumBaseMoebelObj = resRaumBaseMoebel.objects[0].meshes[0]
        raumBaseMoebelMesh = Mesh(raumBaseMoebelObj.vertexData, raumBaseMoebelObj.indexData, vertexAttributes, moebelMaterial)
        raumBaseMoebel.list.add(raumBaseMoebelMesh)

        // Kollisionen des RaumBase
        collisions.add(Vector4f(-2.25f, 2.25f, 2.25f, 2f))         //1
        collisions.add(Vector4f(-2.25f, 0f, -2f, -2f))           //2
        collisions.add(Vector4f(-2.25f, -2f, 2.25f, -2.25f))       //3
        collisions.add(Vector4f(2f, 0f, 2.5f, -2f))             //4
        collisions.add(Vector4f(-2.25f, 2f, -2f, 1f))            //5
        collisions.add(Vector4f(2f, 2f, 2.25f, 1f))              //6

        collisions.add(Vector4f(-2f, -1f, 2f, -2f))
        collisions.add(Vector4f(-2f, 2f, -1f, 1f))
        collisions.add(Vector4f(0f, 2f, 2f, 1f))

        val seeds = mutableListOf<Vector2i>(
            Vector2i(0, 1),
            Vector2i(0, 2),
            Vector2i(0, 3),
            Vector2i(1, 1),
            Vector2i(1, 2),
            Vector2i(1, 3),
            Vector2i(4, 1),
            Vector2i(4, 2),
            Vector2i(4, 3),
            Vector2i(2, 0),
            Vector2i(2, 4),
            Vector2i(3, 0),
            Vector2i(3, 4)
        )

        val seedWohnung = Random.nextInt(0, 13)

        val genSeed = seeds[seedWohnung]

        /**Raum_links**/

        val resRaumL = OBJLoader.loadOBJ("assets/models/raumDEF.obj")
        val raumLObj = resRaumL.objects[0].meshes[0]
        raumLMesh = Mesh(raumLObj.vertexData, raumLObj.indexData, vertexAttributes, raumMaterial)
        raumL.list.add(raumLMesh)

        val resRaumLMoebel = OBJLoader.loadOBJ("assets/models/raumDEF.obj")
        val raumLMoebelObj = resRaumLMoebel.objects[0].meshes[0]
        raumLMoebelMesh = Mesh(raumLMoebelObj.vertexData, raumLMoebelObj.indexData, vertexAttributes, moebelMaterial)
        raumLMoebel.list.add(raumLMoebelMesh)


        if (genSeed.y == 0){
            val resRaumL0 = OBJLoader.loadOBJ("assets/models/raumL0.obj")
            val raumL0Obj = resRaumL0.objects[0].meshes[0]
            raumLMesh = Mesh(raumL0Obj.vertexData, raumL0Obj.indexData, vertexAttributes, raumMaterial)
            raumL.list.add(raumLMesh)

            val resRaumL0Moebel = OBJLoader.loadOBJ("assets/models/raumL0Moebel.obj")
            val raumL0MoebelObj = resRaumL0Moebel.objects[0].meshes[0]
            raumLMoebelMesh = Mesh(raumL0MoebelObj.vertexData, raumL0MoebelObj.indexData, vertexAttributes, moebelMaterial)
            raumLMoebel.list.add(raumLMoebelMesh)

            collisions.add(Vector4f(-6.5f, 2.25f, -2.25f, 2f))
            collisions.add(Vector4f(-6.5f, 2f, -6.25f, -2f))
            collisions.add(Vector4f(-6.5f, -2f, -2.25f, -2.25f))

            collisions.add(Vector4f(-5.25f, -1f, -3.25f, -2f))
            collisions.add(Vector4f(-3.25f, 0f, -2.25f, -2f))
            collisions.add(Vector4f(-3.25f, 2f, -2.25f, 1f))
            collisions.add(Vector4f(-6.25f, 2f, -5.25f, 1f))
            collisions.add(Vector4f(-6.25f, 0f, -5.25f, -2f))
        }


        else if (genSeed.y == 1){
            val resRaumL1 = OBJLoader.loadOBJ("assets/models/raumL1.obj")
            val raumL1Obj = resRaumL1.objects[0].meshes[0]
            raumLMesh = Mesh(raumL1Obj.vertexData, raumL1Obj.indexData, vertexAttributes, raumMaterial)
            raumL.list.add(raumLMesh)

            val resRaumL1Moebel = OBJLoader.loadOBJ("assets/models/raumL1Moebel.obj")
            val raumL1MoebelObj = resRaumL1Moebel.objects[0].meshes[0]
            raumLMoebelMesh = Mesh(raumL1MoebelObj.vertexData, raumL1MoebelObj.indexData, vertexAttributes, moebelMaterial)
            raumLMoebel.list.add(raumLMoebelMesh)

            collisions.add(Vector4f(-5.5f, 2.25f, -2.25f, 2f))
            collisions.add(Vector4f(-5.5f, 2f, -5.25f, -2f))
            collisions.add(Vector4f(-5.5f, -2f, -2.25f, -2.25f))

            collisions.add(Vector4f(-5.25f, -1f, -4.25f, -2f))
            collisions.add(Vector4f(-3.25f, -1f, -2.25f, -2f))
            collisions.add(Vector4f(-5.25f, 2f, -2.25f, 1f))
        }


        else if (genSeed.y == 2){
            val resRaumL2 = OBJLoader.loadOBJ("assets/models/raumL2.obj")
            val raumL2Obj = resRaumL2.objects[0].meshes[0]
            raumLMesh = Mesh(raumL2Obj.vertexData, raumL2Obj.indexData, vertexAttributes, raumMaterial)
            raumL.list.add(raumLMesh)

            val resRaumL2Moebel = OBJLoader.loadOBJ("assets/models/raumL2Moebel.obj")
            val raumL2MoebelObj = resRaumL2Moebel.objects[0].meshes[0]
            raumLMoebelMesh = Mesh(raumL2MoebelObj.vertexData, raumL2MoebelObj.indexData, vertexAttributes, moebelMaterial)
            raumLMoebel.list.add(raumLMoebelMesh)

            collisions.add(Vector4f(-4.5f, 3.25f, -2f, 3f))
            collisions.add(Vector4f(-4.5f, 3f, -4.25f, -1f))
            collisions.add(Vector4f(-4.5f, -1f, -2.25f, -1.25f))
            collisions.add(Vector4f(-2.25f, 3f, -2f, 2.25f))

            collisions.add(Vector4f(-4.25f, 0f, -2.25f, -1f))
            collisions.add(Vector4f(-4.25f, 3f, -2.25f, 2f))
        }


        else if (genSeed.y == 3){
            val resRaumL3 = OBJLoader.loadOBJ("assets/models/raumL3.obj")
            val raumL3Obj = resRaumL3.objects[0].meshes[0]
            raumLMesh = Mesh(raumL3Obj.vertexData, raumL3Obj.indexData, vertexAttributes, raumMaterial)
            raumL.list.add(raumLMesh)

            val resRaumL3Moebel = OBJLoader.loadOBJ("assets/models/raumL3Moebel.obj")
            val raumL3MoebelObj = resRaumL3Moebel.objects[0].meshes[0]
            raumLMoebelMesh = Mesh(raumL3MoebelObj.vertexData, raumL3MoebelObj.indexData, vertexAttributes, moebelMaterial)
            raumLMoebel.list.add(raumLMoebelMesh)

            collisions.add(Vector4f(-5.5f, 1.25f, -2.25f, 1f))
            collisions.add(Vector4f(-5.5f, 1f, -5.25f, -1f))
            collisions.add(Vector4f(-5.5f, -1f, -2.25f, -1.25f))

            collisions.add(Vector4f(-5.25f, 0f, -2.25f, -1f))
        }


        else if (genSeed.y == 4){
            val resRaumL4 = OBJLoader.loadOBJ("assets/models/raumL4.obj")
            val raumL4Obj = resRaumL4.objects[0].meshes[0]
            raumLMesh = Mesh(raumL4Obj.vertexData, raumL4Obj.indexData, vertexAttributes, raumMaterial)
            raumL.list.add(raumLMesh)

            val resRaumL4Moebel = OBJLoader.loadOBJ("assets/models/raumL4Moebel.obj")
            val raumL4MoebelObj = resRaumL4Moebel.objects[0].meshes[0]
            raumLMoebelMesh = Mesh(raumL4MoebelObj.vertexData, raumL4MoebelObj.indexData, vertexAttributes, moebelMaterial)
            raumLMoebel.list.add(raumLMoebelMesh)

            collisions.add(Vector4f(-4.5f, 1.25f, -2.25f, 1f))
            collisions.add(Vector4f(-4.5f, 1f, -4.25f, -1f))
            collisions.add(Vector4f(-4.5f, -1f, -2.25f, -1.25f))

            collisions.add(Vector4f(-4.25f, 1f, -3.25f, -1f))
        }



        /**Raum_rechts**/

        val resRaumR = OBJLoader.loadOBJ("assets/models/raumDEF.obj")
        val raumRObj = resRaumR.objects[0].meshes[0]
        raumRMesh = Mesh(raumRObj.vertexData, raumRObj.indexData, vertexAttributes, raumMaterial)
        raumR.list.add(raumRMesh)

        val resRaumRMoebel = OBJLoader.loadOBJ("assets/models/raumDEF.obj")
        val raumRMoebelObj = resRaumRMoebel.objects[0].meshes[0]
        raumRMoebelMesh = Mesh(raumRMoebelObj.vertexData, raumRMoebelObj.indexData, vertexAttributes, raumMaterial)
        raumRMoebel.list.add(raumRMesh)



        if (genSeed.x == 0){
            val resRaumR0 = OBJLoader.loadOBJ("assets/models/raumR0.obj")
            val raumR0Obj = resRaumR0.objects[0].meshes[0]
            raumRMesh = Mesh(raumR0Obj.vertexData, raumR0Obj.indexData, vertexAttributes, raumMaterial)
            raumR.list.add(raumRMesh)

            val resRaumR0Moebel = OBJLoader.loadOBJ("assets/models/raumR0Moebel.obj")
            val raumR0MoebelObj = resRaumR0Moebel.objects[0].meshes[0]
            raumRMoebelMesh = Mesh(raumR0MoebelObj.vertexData, raumR0MoebelObj.indexData, vertexAttributes, moebelMaterial)
            raumRMoebel.list.add(raumRMoebelMesh)

            collisions.add(Vector4f(2f, 3.25f, 6.5f, 3f))
            collisions.add(Vector4f(6.25f, 3f, 6.5f, -1f))
            collisions.add(Vector4f(2.25f, -1f, 6.5f, -1.25f))
            collisions.add(Vector4f(2f, 3f, 2.25f, 2.25f))

            collisions.add(Vector4f(2.25f, 0f, 3.25f, -1f))
            collisions.add(Vector4f(4.25f, 0f, 6.25f, -1f))
            collisions.add(Vector4f(3.25f, 3f, 5.25f, 1f))
        }


        else if (genSeed.x == 1){
            val resRaumR1 = OBJLoader.loadOBJ("assets/models/raumR1.obj")
            val raumR1Obj = resRaumR1.objects[0].meshes[0]
            raumRMesh = Mesh(raumR1Obj.vertexData, raumR1Obj.indexData, vertexAttributes, raumMaterial)
            raumR.list.add(raumRMesh)

            val resRaumR1Moebel = OBJLoader.loadOBJ("assets/models/raumR1Moebel.obj")
            val raumR1MoebelObj = resRaumR1Moebel.objects[0].meshes[0]
            raumRMoebelMesh = Mesh(raumR1MoebelObj.vertexData, raumR1MoebelObj.indexData, vertexAttributes, moebelMaterial)
            raumRMoebel.list.add(raumRMoebelMesh)

            collisions.add(Vector4f(2.25f, 1.25f, 6.5f, 1f))
            collisions.add(Vector4f(6.25f, 1f, 6.5f, -2f))
            collisions.add(Vector4f(2.25f, -2f, 6.5f, -2.25f))

            collisions.add(Vector4f(3.25f, 0f, 5.25f, -2f))
        }


        else if (genSeed.x == 2){
            val resRaumR2 = OBJLoader.loadOBJ("assets/models/raumR2.obj")
            val raumR2Obj = resRaumR2.objects[0].meshes[0]
            raumRMesh = Mesh(raumR2Obj.vertexData, raumR2Obj.indexData, vertexAttributes, raumMaterial)
            raumR.list.add(raumRMesh)

            val resRaumR2Moebel = OBJLoader.loadOBJ("assets/models/raumR2Moebel.obj")
            val raumR2MoebelObj = resRaumR2Moebel.objects[0].meshes[0]
            raumRMoebelMesh = Mesh(raumR2MoebelObj.vertexData, raumR2MoebelObj.indexData, vertexAttributes, moebelMaterial)
            raumRMoebel.list.add(raumRMoebelMesh)

            collisions.add(Vector4f(2.25f, 2.25f, 4.5f, 2f))
            collisions.add(Vector4f(4.25f, 2f, 4.5f, -2f))
            collisions.add(Vector4f(2.25f, -2f, 4.5f, -2.25f))

            collisions.add(Vector4f(2.25f, -1f, 4.25f, -2f))
            collisions.add(Vector4f(2.25f, 2f, 4.25f, 1f))
        }


        else if (genSeed.x == 3){
            val resRaumR3 = OBJLoader.loadOBJ("assets/models/raumR3.obj")
            val raumR3Obj = resRaumR3.objects[0].meshes[0]
            raumRMesh = Mesh(raumR3Obj.vertexData, raumR3Obj.indexData, vertexAttributes, raumMaterial)
            raumR.list.add(raumRMesh)

            val resRaumR3Moebel = OBJLoader.loadOBJ("assets/models/raumR3Moebel.obj")
            val raumR3MoebelObj = resRaumR3Moebel.objects[0].meshes[0]
            raumRMoebelMesh = Mesh(raumR3MoebelObj.vertexData, raumR3MoebelObj.indexData, vertexAttributes, moebelMaterial)
            raumRMoebel.list.add(raumRMoebelMesh)

            collisions.add(Vector4f(2.25f, 2.25f, 4.5f, 2f))
            collisions.add(Vector4f(4.25f, 2f, 4.5f, -1f))
            collisions.add(Vector4f(2.25f, -1f, 4.5f, -1.25f))

            collisions.add(Vector4f(2.25f, 0f, 4.25f, -1f))
            collisions.add(Vector4f(3.25f, 2f, 4.25f, 1f))
        }


        else if (genSeed.x == 4){
            val resRaumR4 = OBJLoader.loadOBJ("assets/models/raumR4.obj")
            val raumR4Obj = resRaumR4.objects[0].meshes[0]
            raumRMesh = Mesh(raumR4Obj.vertexData, raumR4Obj.indexData, vertexAttributes, raumMaterial)
            raumR.list.add(raumRMesh)

            val resRaumR4Moebel = OBJLoader.loadOBJ("assets/models/raumR4Moebel.obj")
            val raumR4MoebelObj = resRaumR4Moebel.objects[0].meshes[0]
            raumRMoebelMesh = Mesh(raumR4MoebelObj.vertexData, raumR4MoebelObj.indexData, vertexAttributes, moebelMaterial)
            raumRMoebel.list.add(raumRMoebelMesh)

            collisions.add(Vector4f(2.25f, 2.25f, 4.5f, 2f))
            collisions.add(Vector4f(4.25f, 2f, 4.5f, 0f))
            collisions.add(Vector4f(2.25f, 0f, 4.5f, -0.25f))

            collisions.add(Vector4f(3.25f, 2f, 4.25f, 0f))
        }



        //"Deadzones" für die Collider
        for(c in collisions)
        {
            val threshCollider = 0.15f
            c.x -= threshCollider
            c.y += threshCollider
            c.z += threshCollider
            c.w -= threshCollider
        }


        //Lights
        spotLight = SpotLight(Vector3f(0.0f, 0.0f, 0.0f), Vector3f(1.0f))
        corner1 = PointLight(Vector3f(20.0f,4.0f,20.0f), Vector3f(1.0f, 0.0f, 1.0f), Vector3f(0.7f, 0.3f, 0.0f))
        corner2 = PointLight(Vector3f(-20.0f,4.0f,20.0f), Vector3f(1.0f, 1.0f, 0.0f), Vector3f(0.7f, 0.3f, 0.0f))
        corner3 = PointLight(Vector3f(20.0f,4.0f,-20.0f), Vector3f(0.0f, 1.0f, 1.0f), Vector3f(0.7f, 0.3f, 0.0f))
        corner4 = PointLight(Vector3f(-20.0f,4.0f,-20.0f), Vector3f(1.0f, 0.5f, 0.5f), Vector3f(0.7f, 0.3f, 0.0f))
        roomLight1 = PointLight(Vector3f(0.0f, 2.0f, 0.0f), Vector3f(1.0f))
        roomLight2 = PointLight(Vector3f(-3.5f, 1.65f, 0.5f), Vector3f(1.0f))
        roomLight3 = PointLight(Vector3f(3.5f, 1.65f, 0.5f), Vector3f(1.0f))

        //Transform
        spotLight.rotateLocal(Math.toRadians(-10.0f), Math.PI.toFloat(), 0.0f) //PI??
        body.translateLocal(Vector3f(0.0f, 1.2f, 0.0f))
        balken.translateLocal(Vector3f(-1.142f, -0.345f, -0.3f))


        //Camera
        //camera.translateLocal(Vector3f(0.0f, 1.2f, 0.0f))
        camera.nearPlane = 0.0001f


        //Sky
        sky.scaleLocal(Vector3f(12.0f))


        //Parent
        camera.parent = body
        spotLight.parent = camera
        balken.parent = camera

    }

    fun setTimer(t: Float){
        if ( window.currentTime >= t){
            //cleanup()
            println("Du hast zu lange gebraucht! Du bist gefeuert!!!!")
        }
    }

    fun render(dt: Float, t: Float) { //-> t == window.currentTime
        println(t)
        setTimer(timer)

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        shader.setUniform("col", Vector3f(0.4235f, 0.4745f, 0.5804f)) //Paynes Grey

        camera.bind(shader)

        body.render(shader)
        floor.render(shader)

        sky.render(shader)

        balken.render(shader)

        raumBase.render(shader)
        raumBaseMoebel.render(shader)

        raumL.render(shader)
        raumLMoebel.render(shader)

        raumR.render(shader)
        raumRMoebel.render(shader)

        roomLight1.bind(shader, "roomPoint1")
        roomLight2.bind(shader, "roomPoint2")
        roomLight3.bind(shader, "roomPoint3")
        spotLight.bind(shader, "bodySpot", camera.getCalculateViewMatrix())
        corner1.bind(shader, "corner")
        corner2.bind(shader, "corner2")
        corner3.bind(shader, "corner3")
        corner4.bind(shader, "corner4")
        //RaveLights
        corner4.lightCol = Vector3f(abs(tan(t / 1)), abs(tan(t / 2)), abs(tan(t / 3)))
        corner3.lightCol = Vector3f(abs(tan(t / 1)), abs(tan(t / 2)), abs(tan(t / 3)))
        corner2.lightCol = Vector3f(abs(tan(t / 1)), abs(tan(t / 2)), abs(tan(t / 3)))
        corner1.lightCol = Vector3f(abs(tan(t / 1)), abs(tan(t / 2)), abs(tan(t / 3)))

        collision()
    }

    fun zeitBerechnung(t : Float) : Float{
        return 0.009f/t
    }

    fun update(dt: Float, t: Float) {
        if(window.getKeyState(GLFW_KEY_W))
            body.translateLocal(Vector3f(0.0f, 0.0f, -2 * dt))

        if(window.getKeyState(GLFW_KEY_A))
            body.translateLocal(Vector3f(-1 * dt, 0.0f, 0.0f))

        if(window.getKeyState(GLFW_KEY_D))
            body.translateLocal(Vector3f(1 * dt, 0.0f, 0.0f))

        if(window.getKeyState(GLFW_KEY_S))
            body.translateLocal(Vector3f(0.0f, 0.0f, 2 * dt))

        balken.translateLocal(Vector3f(zeitBerechnung(timer), 0.0f, 0.0f))
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {
        if (window.getKeyState(GLFW_KEY_L)) {
            if (shader == staticShader) {
                shader = ohneSpotShader
            } else {
                shader = staticShader
            }
        }

        if (window.getKeyState(GLFW_KEY_I)){
            if (shader == staticShader || shader == ohneSpotShader){
                shader = raveLightShader
            } else {
                shader = ohneSpotShader
            }
        }
    }

    fun onMouseMove(xpos: Double, ypos: Double) {
        val deltaX = xpos - oldMousePosX
        val deltaY = ypos - oldMousePosY

        oldMousePosX = xpos
        oldMousePosY = ypos

        if (bool) {
            body.rotateAroundPoint(0.0f, -Math.toRadians(deltaX.toFloat() * 0.02f) ,0.0f, camera.getWorldPosition())
            camera.rotateLocal(-Math.toRadians(deltaY.toFloat() * 0.04f),0.0f, 0.0f)
        }
        bool = true
    }

    fun collision() {

        val pushDist = 0.05f   //definiert die "Schiebdistanz" von der Wand weg //muss bei schlechten PCs hochgestellt werden
        val collisionBuffer = 0.3f      //muss bei schlechten PCs hochgestellt werden

        for(c in collisions) {
            if (body.getPosition().x > c.x && body.getPosition().x < c.z && body.getPosition().z < c.y && body.getPosition().z > c.w)
            {
                if (body.getPosition().x > c.x && body.getPosition().x < (c.x + collisionBuffer))
                    body.translateGlobal(Vector3f(-pushDist, 0.0f, 0.0f))

                if(body.getPosition().x < c.z && body.getPosition().x > (c.z - collisionBuffer))
                    body.translateGlobal(Vector3f(pushDist, 0.0f, 0.0f))

                if(body.getPosition().z < c.y && body.getPosition().z > (c.y - collisionBuffer))
                    body.translateGlobal(Vector3f(0f, 0f, pushDist))

                if(body.getPosition().z > c.w && body.getPosition().z < (c.w + collisionBuffer))
                    body.translateGlobal(Vector3f(0f, 0f, -pushDist))
            }
        }
    }

    fun cleanup() {
        window.quit()
    }
}
