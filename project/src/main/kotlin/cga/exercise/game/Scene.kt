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
import cga.framework.*
//import cga.framework.GameWindow
//import cga.framework.OBJLoader
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

    private val collisions = mutableListOf<Vector4f>()
    private val interactionAreas = mutableListOf<Vector4f>()

    private var currInteraction = 0
    private val checklists = mutableListOf<Renderable>()

    private var vertexAttributes = arrayOf<VertexAttribute>()


    private var floorMesh : Mesh
    private var floor = Renderable()

    private var interactMesh : Mesh
    private var interact = Renderable()

    private var doorMesh : Mesh
    private var door = Renderable()

    private var checklistRBMesh : Mesh
    private var checklistRB = Renderable()

    private var checklistRRMesh : Mesh
    private var checklistRR = Renderable()

    private var checklistRLMesh : Mesh
    private var checklistRL = Renderable()

    private var checklistFinalMesh : Mesh
    private var checklistFinal = Renderable()

    private var winScreenMesh : Mesh
    private var winScreen = Renderable()

    private var failScreenMesh : Mesh
    private var failScreen = Renderable()


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

        /**Materials**/
        val floorMaterial = loadMaterial("assets/textures/floor_diff.png")
        val skyMaterial = loadMaterial("assets/textures/sky_diff.png")
        val raumMaterial = loadMaterial("assets/textures/raum_diff.png")
        val moebelMaterial = loadMaterial("assets/textures/white.png")
        val interactMaterial = loadMaterial("assets/textures/interact.png")
        val winMaterial = loadMaterial("assets/textures/text_win.png")
        val failMaterial = loadMaterial("assets/textures/text_fail.png")

        /**Meshes**/
        floorMesh = loadMesh("assets/models/floor.obj", floorMaterial)
        floor.list.add(floorMesh)

        skyMesh = loadMesh("assets/models/skycube.obj", skyMaterial)
        sky.list.add(skyMesh)

        interactMesh = loadMesh("assets/models/Interact.obj", interactMaterial)
        interact.list.add(interactMesh)

        doorMesh = loadMesh("assets/models/door.obj", interactMaterial)
        door.list.add(doorMesh)

        checklistRBMesh = loadMesh("assets/models/raumDEF.obj", moebelMaterial)
        checklistRB.list.add(checklistRBMesh)

        checklistFinalMesh = loadMesh("assets/models/raumDEF.obj", moebelMaterial)
        checklistFinal.list.add(checklistFinalMesh)

        winScreenMesh = loadMesh("assets/models/screen.obj", winMaterial)
        winScreen.list.add(winScreenMesh)

        failScreenMesh = loadMesh("assets/models/screen.obj", failMaterial)
        failScreen.list.add(failScreenMesh)

        /**Loads Body and create a mesh**/
        val resBody = OBJLoader.loadOBJ("assets/models/body.obj")
        val bodyObj = resBody.objects[0].meshes[0]
        bodyMesh = Mesh(bodyObj.vertexData, bodyObj.indexData, vertexAttributes)
        body.list.add(bodyMesh)

        /**Balken generierung**/
        //val balkenTextureEmit = Texture2D("assets/textures/red.png", true)
        //val balkenTextureDiff = Texture2D("assets/textures/red.png",true)
        //val balkenTextureSpec = Texture2D("assets/textures/red.png",true)
//
        //balkenTextureEmit.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        //balkenTextureDiff.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        //balkenTextureSpec.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
//
        //val balkenMaterial = Material(balkenTextureDiff, balkenTextureEmit, balkenTextureSpec, 60.0f, Vector2f(64.0f, 64.0f))
        val balkenMaterial = loadMaterial("assets/textures/red.png")

        //val resBalken = OBJLoader.loadOBJ("assets/models/balken.obj")
        //val balkenObj = resBalken.objects[0].meshes[0]
        //balkenMesh = Mesh(balkenObj.vertexData, balkenObj.indexData, vertexAttributes, balkenMaterial)
        //balken.list.add(balkenMesh)

        balkenMesh = loadMesh("assets/models/balken.obj", balkenMaterial)
        balken.list.add(balkenMesh)

        /**Generation der Wohnung**/


        /**Startraum**/

        // Wohnungsseed

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


        /**BaseRaum**/
        raumBaseMesh = loadMesh("assets/models/raumBase.obj", raumMaterial)
        raumBase.list.add(raumBaseMesh)

        raumBaseMoebelMesh = loadMesh("assets/models/raumBaseMoebel.obj", moebelMaterial)
        raumBaseMoebel.list.add(raumBaseMoebelMesh)




        loadCollision(rBCollisions)

        loadTaskRB()

        /**Raum_links**/

        raumLMesh = loadMesh("assets/models/raumDEF.obj", raumMaterial)
        raumL.list.add(raumLMesh)

        raumLMoebelMesh = loadMesh("assets/models/raumDEF.obj", moebelMaterial)
        raumLMoebel.list.add(raumLMoebelMesh)

        checklistRLMesh = loadMesh("assets/models/raumDEF.obj", moebelMaterial)
        checklistRL.list.add(checklistRLMesh)



        if (genSeed.y == 0) {
            raumLMesh = loadMesh("assets/models/raumL0.obj", raumMaterial)
            raumL.list.add(raumLMesh)

            raumLMoebelMesh = loadMesh("assets/models/raumL0Moebel.obj", moebelMaterial)
            raumLMoebel.list.add(raumLMoebelMesh)

            loadCollision(l0Collisions)

            loadTaskRL(l0Tasks)
        }


        else if (genSeed.y == 1){
            raumLMesh = loadMesh("assets/models/raumL1.obj", raumMaterial)
            raumL.list.add(raumLMesh)

            raumLMoebelMesh = loadMesh("assets/models/raumL1Moebel.obj", moebelMaterial)
            raumLMoebel.list.add(raumLMoebelMesh)

            loadCollision(l1Collisions)

            loadTaskRL(l1Tasks)
        }


        else if (genSeed.y == 2){
            raumLMesh = loadMesh("assets/models/raumL2.obj", raumMaterial)
            raumL.list.add(raumLMesh)

            raumLMoebelMesh = loadMesh("assets/models/raumL2Moebel.obj", moebelMaterial)
            raumLMoebel.list.add(raumLMoebelMesh)

            loadCollision(l2Collisions)

            loadTaskRL(l2Tasks)
        }


        else if (genSeed.y == 3){
            raumLMesh = loadMesh("assets/models/raumL3.obj", raumMaterial)
            raumL.list.add(raumLMesh)

            raumLMoebelMesh = loadMesh("assets/models/raumL3Moebel.obj", moebelMaterial)
            raumLMoebel.list.add(raumLMoebelMesh)

            loadCollision(l3Collisions)

            loadTaskRL(l3Tasks)
        }


        else if (genSeed.y == 4){
            raumLMesh = loadMesh("assets/models/raumL4.obj", raumMaterial)
            raumL.list.add(raumLMesh)

            raumLMoebelMesh = loadMesh("assets/models/raumL4Moebel.obj", moebelMaterial)
            raumLMoebel.list.add(raumLMoebelMesh)

            loadCollision(l4Collisions)

            loadTaskRL(l4Tasks)
        }



        /**Raum_rechts**/

        raumRMesh = loadMesh("assets/models/raumDEF.obj", raumMaterial)
        raumR.list.add(raumRMesh)

        raumRMoebelMesh = loadMesh("assets/models/raumDEF.obj", moebelMaterial)
        raumRMoebel.list.add(raumRMoebelMesh)

        checklistRRMesh = loadMesh("assets/models/raumDEF.obj", moebelMaterial)
        checklistRR.list.add(checklistRRMesh)



        if (genSeed.x == 0){
            raumRMesh = loadMesh("assets/models/raumR0.obj", raumMaterial)
            raumR.list.add(raumRMesh)

            raumRMoebelMesh = loadMesh("assets/models/raumR0Moebel.obj", moebelMaterial)
            raumRMoebel.list.add(raumRMoebelMesh)

            loadCollision(r0Collisions)

            loadTaskRR(r0Tasks)
        }


        else if (genSeed.x == 1){
            raumRMesh = loadMesh("assets/models/raumR1.obj", raumMaterial)
            raumR.list.add(raumRMesh)

            raumRMoebelMesh = loadMesh("assets/models/raumR1Moebel.obj", moebelMaterial)
            raumRMoebel.list.add(raumRMoebelMesh)

            loadCollision(r1Collisions)

            loadTaskRR(r1Tasks)
        }


        else if (genSeed.x == 2){
            raumRMesh = loadMesh("assets/models/raumR2.obj", raumMaterial)
            raumR.list.add(raumRMesh)

            raumRMoebelMesh = loadMesh("assets/models/raumR2Moebel.obj", moebelMaterial)
            raumRMoebel.list.add(raumRMoebelMesh)

            loadCollision(r2Collisions)

            loadTaskRR(r2Tasks)
        }


        else if (genSeed.x == 3){
            raumRMesh = loadMesh("assets/models/raumR3.obj", raumMaterial)
            raumR.list.add(raumRMesh)

            raumRMoebelMesh = loadMesh("assets/models/raumR3Moebel.obj", moebelMaterial)
            raumRMoebel.list.add(raumRMoebelMesh)

            loadCollision(r3Collisions)

            loadTaskRR(r3Tasks)
        }


        else if (genSeed.x == 4){
            raumRMesh = loadMesh("assets/models/raumR4.obj", raumMaterial)
            raumR.list.add(raumRMesh)

            raumRMoebelMesh = loadMesh("assets/models/raumR4Moebel.obj", moebelMaterial)
            raumRMoebel.list.add(raumRMoebelMesh)

            loadCollision(r4Collisions)

            loadTaskRR(r4Tasks)
        }

        //ENDE
        loadTaskFinal()

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
        camera.translateLocal(Vector3f(0.0f, 1.2f, 0.0f))
        camera.nearPlane = 0.0001f
        camera.farPlane = 100f


        //Sky
        sky.scaleLocal(Vector3f(12.0f))

        //door
        door.translateLocal(Vector3f(-0.5f, 0f, 2.125f))


        //checklist

        for (c in checklists)
            c.translateGlobal(Vector3f(-0.14f, 0.03f, 0f))

        interact.parent = camera
        winScreen.parent = camera
        failScreen.parent = camera

        for(c in checklists)
            c.parent = camera

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

        door.render(shader)

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
        if(currInteraction != -1)
            interaction()
        if(currInteraction == -1)
            winScreen.render(shader)
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
    fun interaction() {

        checklists[currInteraction].render(shader)


        if (body.getPosition().x > interactionAreas[currInteraction].x && body.getPosition().x < interactionAreas[currInteraction].z && body.getPosition().z < interactionAreas[currInteraction].y && body.getPosition().z > interactionAreas[currInteraction].w) {
            interact.render(shader)
            if(window.getKeyState(GLFW_KEY_E)) {
                if(currInteraction != interactionAreas.size-1)
                    currInteraction++
                else {
                    currInteraction = -1
                }
            }

        }

    }

    fun loadMesh (path : String, tempMaterial : Material) : Mesh {
        val resTemp = OBJLoader.loadOBJ(path)
        val tempObj = resTemp.objects[0].meshes[0]
        return Mesh(tempObj.vertexData, tempObj.indexData, vertexAttributes, tempMaterial)
    }

    fun loadCollision (collisionOfRoom : MutableList<Vector4f>) {
        var runCollisions = 0
        while (runCollisions < collisionOfRoom.size) {
            collisions.add(collisionOfRoom[runCollisions])
            runCollisions++
        }
    }

    fun loadMaterial(texDiff : String) : Material {
        val tempTextureEmit = Texture2D("assets/textures/black.png", true)
        val tempTextureDiff = Texture2D(texDiff,true)
        val tempTextureSpec = Texture2D("assets/textures/black.png",true)

        tempTextureEmit.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        tempTextureDiff.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        tempTextureSpec.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        return Material(tempTextureDiff, tempTextureEmit, tempTextureSpec, 60.0f, Vector2f(1.0f, 1.0f))
    }

    fun loadMaterial(texEmit : String, texDiff : String, texSpec : String) : Material {
        val tempTextureEmit = Texture2D(texEmit, true)
        val tempTextureDiff = Texture2D(texDiff,true)
        val tempTextureSpec = Texture2D(texSpec,true)

        tempTextureEmit.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        tempTextureDiff.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        tempTextureSpec.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        return Material(tempTextureDiff, tempTextureEmit, tempTextureSpec, 60.0f, Vector2f(1.0f, 1.0f))
    }

    fun loadTaskRB(){
        val seedTaskRB = Random.nextInt(0, 2)

        val checklistRBMaterial = loadMaterial(rBTasks[seedTaskRB].first)

        checklistRBMesh = loadMesh("assets/models/checklist.obj", checklistRBMaterial)
        checklistRB.list.add(checklistRBMesh)

        interactionAreas.add(rBTasks[seedTaskRB].second)

        checklists.add(checklistRB)
    }

    fun loadTaskRL(taskList : MutableList<Pair<String, Vector4f>>){


        val seedTaskL = Random.nextInt(0, 2)

        val checklistRLMaterial = loadMaterial(taskList[seedTaskL].first)

        checklistRLMesh = loadMesh("assets/models/checklist.obj", checklistRLMaterial)
        checklistRL.list.add(checklistRLMesh)

        interactionAreas.add(taskList[seedTaskL].second)

        checklists.add(checklistRL)
    }

    fun loadTaskRR(taskList : MutableList<Pair<String, Vector4f>>){
        val seedTaskR = Random.nextInt(0, 2)

        val checklistRRMaterial = loadMaterial(taskList[seedTaskR].first)

        checklistRRMesh = loadMesh("assets/models/checklist.obj", checklistRRMaterial)
        checklistRR.list.add(checklistRRMesh)

        interactionAreas.add(taskList[seedTaskR].second)

        checklists.add(checklistRR)
    }

    fun loadTaskFinal() {
        val checklistFinalMaterial = loadMaterial("assets/textures/text_zurArbeit.png")

        checklistFinalMesh = loadMesh("assets/models/checklist.obj", checklistFinalMaterial)
        checklistFinal.list.add(checklistFinalMesh)

        interactionAreas.add(Vector4f(-1f, 2f, 0f, 1.5f))

        checklists.add(checklistFinal)
    }

    fun cleanup() {
        window.quit()
    }
}
