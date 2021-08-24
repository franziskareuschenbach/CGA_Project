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
import org.joml.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL30.*
import kotlin.math.*
import kotlin.random.Random


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {


    /**Shader**/
    private val mitSpotShader: ShaderProgram
    private var ohneSpotShader : ShaderProgram
    private var raveLightShader : ShaderProgram
    private var shader : ShaderProgram


    /**Meshes_Divers**/
    private var floorMesh : Mesh
    private var floor = Renderable()

    private var interactMesh : Mesh
    private var interact = Renderable()

    private var doorMesh : Mesh
    private var door = Renderable()

    private var barMesh : Mesh
    private var bar = Renderable()

    private var bodyMesh : Mesh
    private var body = Renderable()

    private var skyMesh : Mesh
    private var sky = Renderable()


    /**Meshes_Checklist**/
    private var checklistRBMesh : Mesh
    private var checklistRB = Renderable()

    private var checklistRMesh : Mesh
    private var checklistR = Renderable()

    private var checklistLMesh : Mesh
    private var checklistL = Renderable()

    private var checklistFinalMesh : Mesh
    private var checklistFinal = Renderable()


    /**Meshes_Screen**/
    private var winScreenMesh : Mesh
    private var winScreen = Renderable()

    private var failScreenMesh : Mesh
    private var failScreen = Renderable()



    /**Meshes_Raum**/
    /**Raum_Base**/
    private var roomBaseMesh : Mesh
    private var roomBase = Renderable()

    private var roomBaseFurnitureMesh : Mesh
    private var roomBaseFurniture = Renderable()


    /**Raum_links**/
    private var roomLMesh : Mesh
    private var roomL = Renderable()

    private var roomLFurnitureMesh : Mesh
    private var roomLFurniture = Renderable()


    /**Raum_rechts**/
    private var roomRMesh : Mesh
    private var roomR = Renderable()

    private var roomRFurnitureMesh : Mesh
    private var RoomRFurniture = Renderable()


    /**Licht**/
    private var spotLight : SpotLight

    private var corner1 : PointLight
    private var corner2 : PointLight
    private var corner3 : PointLight
    private var corner4 : PointLight

    private var roomLight1 : PointLight
    private var roomLight2 : PointLight
    private var roomLight3 : PointLight


    /**Kamera**/
    private var camera = TronCamera()


    /**Maus**/
    private var oldMousePosX = -1.0
    private var oldMousePosY = -1.0
    private var bool = false


    /**Kollisionen**/
    private val collisions = mutableListOf<Vector4f>()


    /**Aufgaben**/
    private val interactionAreas = mutableListOf<Vector4f>()
    private var currInteraction = 0

    private val checklists = mutableListOf<Renderable>()


    /**Vertex_Attributes**/
    private var vertexAttributes = arrayOf<VertexAttribute>()


    /**Timer**/
    private var timer = 30.0f
    private var timer2 = timer


    //scene setup
    init {
        mitSpotShader = ShaderProgram("assets/shaders/mitSpot_vert.glsl", "assets/shaders/mitSpot_frag.glsl")                //unterschiedliche Shader werden geladen
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
        vertexAttributes = arrayOf<VertexAttribute>(attrPos, attrTC, attrNorm)


        /**Materialien**/
        val floorMaterial = loadMaterial("assets/textures/floor_diff.png")                                                                      //Materialien werden mit ausgelagereter Funktion "loadMaterial" erstellt
        val skyMaterial = loadMaterial("assets/textures/sky_diff.png")
        val roomMaterial = loadMaterial("assets/textures/raum_diff.png")
        val furnitureMaterial = loadMaterial("assets/textures/white.png")
        val interactMaterial = loadMaterial("assets/textures/interact.png")
        val winMaterial = loadMaterial("assets/textures/text_win.png")
        val failMaterial = loadMaterial("assets/textures/text_fail.png")
        val barMaterial = loadMaterial("assets/textures/red.png", "assets/textures/red.png", "assets/textures/red.png")


        /**Meshes**/
        floorMesh = loadMesh("assets/models/floor.obj", floorMaterial)                                                                          //Meshes werden mit ausgelagerter Funktion "loadMesh" erstellt
        floor.list.add(floorMesh)

        skyMesh = loadMesh("assets/models/skycube.obj", skyMaterial)
        sky.list.add(skyMesh)

        doorMesh = loadMesh("assets/models/door.obj", interactMaterial)
        door.list.add(doorMesh)

        interactMesh = loadMesh("assets/models/Interact.obj", interactMaterial)
        interact.list.add(interactMesh)

        barMesh = loadMesh("assets/models/balken.obj", barMaterial)
        bar.list.add(barMesh)

        bodyMesh = loadMesh("assets/models/body.obj", barMaterial)
        body.list.add(bodyMesh)

        checklistRBMesh = loadMesh("assets/models/raumDEF.obj", furnitureMaterial)
        checklistRB.list.add(checklistRBMesh)

        checklistFinalMesh = loadMesh("assets/models/raumDEF.obj", furnitureMaterial)
        checklistFinal.list.add(checklistFinalMesh)

        winScreenMesh = loadMesh("assets/models/screen.obj", winMaterial)
        winScreen.list.add(winScreenMesh)

        failScreenMesh = loadMesh("assets/models/screen.obj", failMaterial)
        failScreen.list.add(failScreenMesh)


        /**Generierung der Wohnung**/

        /**Seed**/

        val seeds = mutableListOf<Vector2i>(                        //Liste aus logischen Seeds; jeder hat ein Schlaf- und ein Badezimmer
            Vector2i(0, 1),                                   //(rechter Raum, linker Raum)
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

        val genSeed = seeds[Random.nextInt(0, 13)]      //Wohnnungsseed wird zufällig ausgewählt


        /**BaseRaum**/
        roomBaseMesh = loadMesh("assets/models/raumBase.obj", roomMaterial)
        roomBase.list.add(roomBaseMesh)

        roomBaseFurnitureMesh = loadMesh("assets/models/raumBaseMoebel.obj", furnitureMaterial)
        roomBaseFurniture.list.add(roomBaseFurnitureMesh)


        loadCollision(rBCollisions)                             //Kollisionsdaten werden aus separater Klasse "RoomData" geladen

        loadTaskRB()                                            //Aufgabeninformationen für den BaseRaum werden aus separater Klasse "RoomData" geladen



        /**Raum_links**/

        roomLMesh = loadMesh("assets/models/raumDEF.obj", roomMaterial)                     //Dummy-Meshes werden geladen
        roomL.list.add(roomLMesh)

        roomLFurnitureMesh = loadMesh("assets/models/raumDEF.obj", furnitureMaterial)
        roomLFurniture.list.add(roomLFurnitureMesh)

        checklistLMesh = loadMesh("assets/models/raumDEF.obj", furnitureMaterial)
        checklistL.list.add(checklistLMesh)



        if (genSeed.y == 0) {
            roomLMesh = loadMesh("assets/models/raumL0.obj", roomMaterial)
            roomL.list.add(roomLMesh)

            roomLFurnitureMesh = loadMesh("assets/models/raumL0Moebel.obj", furnitureMaterial)
            roomLFurniture.list.add(roomLFurnitureMesh)

            loadCollision(l0Collisions)

            loadTaskRL(l0Tasks)
        }


        else if (genSeed.y == 1){
            roomLMesh = loadMesh("assets/models/raumL1.obj", roomMaterial)
            roomL.list.add(roomLMesh)

            roomLFurnitureMesh = loadMesh("assets/models/raumL1Moebel.obj", furnitureMaterial)
            roomLFurniture.list.add(roomLFurnitureMesh)

            loadCollision(l1Collisions)

            loadTaskRL(l1Tasks)
        }


        else if (genSeed.y == 2){
            roomLMesh = loadMesh("assets/models/raumL2.obj", roomMaterial)
            roomL.list.add(roomLMesh)

            roomLFurnitureMesh = loadMesh("assets/models/raumL2Moebel.obj", furnitureMaterial)
            roomLFurniture.list.add(roomLFurnitureMesh)

            loadCollision(l2Collisions)

            loadTaskRL(l2Tasks)
        }


        else if (genSeed.y == 3){
            roomLMesh = loadMesh("assets/models/raumL3.obj", roomMaterial)
            roomL.list.add(roomLMesh)

            roomLFurnitureMesh = loadMesh("assets/models/raumL3Moebel.obj", furnitureMaterial)
            roomLFurniture.list.add(roomLFurnitureMesh)

            loadCollision(l3Collisions)

            loadTaskRL(l3Tasks)
        }


        else if (genSeed.y == 4){
            roomLMesh = loadMesh("assets/models/raumL4.obj", roomMaterial)
            roomL.list.add(roomLMesh)

            roomLFurnitureMesh = loadMesh("assets/models/raumL4Moebel.obj", furnitureMaterial)
            roomLFurniture.list.add(roomLFurnitureMesh)

            loadCollision(l4Collisions)

            loadTaskRL(l4Tasks)
        }


        /**Raum_rechts**/

        roomRMesh = loadMesh("assets/models/raumDEF.obj", roomMaterial)
        roomR.list.add(roomRMesh)

        roomRFurnitureMesh = loadMesh("assets/models/raumDEF.obj", furnitureMaterial)
        RoomRFurniture.list.add(roomRFurnitureMesh)

        checklistRMesh = loadMesh("assets/models/raumDEF.obj", furnitureMaterial)
        checklistR.list.add(checklistRMesh)



        if (genSeed.x == 0){
            roomRMesh = loadMesh("assets/models/raumR0.obj", roomMaterial)
            roomR.list.add(roomRMesh)

            roomRFurnitureMesh = loadMesh("assets/models/raumR0Moebel.obj", furnitureMaterial)
            RoomRFurniture.list.add(roomRFurnitureMesh)

            loadCollision(r0Collisions)

            loadTaskRR(r0Tasks)
        }


        else if (genSeed.x == 1){
            roomRMesh = loadMesh("assets/models/raumR1.obj", roomMaterial)
            roomR.list.add(roomRMesh)

            roomRFurnitureMesh = loadMesh("assets/models/raumR1Moebel.obj", furnitureMaterial)
            RoomRFurniture.list.add(roomRFurnitureMesh)

            loadCollision(r1Collisions)

            loadTaskRR(r1Tasks)
        }


        else if (genSeed.x == 2){
            roomRMesh = loadMesh("assets/models/raumR2.obj", roomMaterial)
            roomR.list.add(roomRMesh)

            roomRFurnitureMesh = loadMesh("assets/models/raumR2Moebel.obj", furnitureMaterial)
            RoomRFurniture.list.add(roomRFurnitureMesh)

            loadCollision(r2Collisions)

            loadTaskRR(r2Tasks)
        }


        else if (genSeed.x == 3){
            roomRMesh = loadMesh("assets/models/raumR3.obj", roomMaterial)
            roomR.list.add(roomRMesh)

            roomRFurnitureMesh = loadMesh("assets/models/raumR3Moebel.obj", furnitureMaterial)
            RoomRFurniture.list.add(roomRFurnitureMesh)

            loadCollision(r3Collisions)

            loadTaskRR(r3Tasks)
        }


        else if (genSeed.x == 4){
            roomRMesh = loadMesh("assets/models/raumR4.obj", roomMaterial)
            roomR.list.add(roomRMesh)

            roomRFurnitureMesh = loadMesh("assets/models/raumR4Moebel.obj", furnitureMaterial)
            RoomRFurniture.list.add(roomRFurnitureMesh)

            loadCollision(r4Collisions)

            loadTaskRR(r4Tasks)
        }


        loadTaskFinal()                                                 //Die letzte Aufgabe, die Wohnung zu verlassen wird geladen und an die Aufgabenliste gehangen

        /**"Deadzones" für die Collider**/
        for(c in collisions)                                            //Die Deadzones für die Collider beschreiben, wie groß die unsichtbaren Wände um die gerenderten Wände herum sind; praktisch eine Pufferzone
        {
            val threshCollider = 0.15f                                  //Die Distanz von der gerenderten Wand, mit der die Kollisionen berechnet werden
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

        spotLight.rotateLocal(Math.toRadians(-10.0f), Math.PI.toFloat(), 0.0f)

        camera.nearPlane = 0.0001f                                  //Die Near-Plane wird kleiner definiert, da man sonst durch Wände sehen könnte


        sky.scaleLocal(Vector3f(12.0f))                         //Die "Skybox" wird skaliert


        door.translateLocal(Vector3f(-0.5f, 0f, 2.125f))

        bar.translateLocal(Vector3f(-1.142f, -0.345f, -0.3f))

        body.translateLocal(Vector3f(0.0f, 1.2f, 0.0f))


        for (c in checklists) {                                 //Die Checkliste wird an die richtige Position bewegt und an die Kamera gebunden
            c.translateGlobal(Vector3f(-0.14f, 0.03f, 0f))
            c.parent = camera
        }


        /**Parenting**/
        camera.parent = body
        spotLight.parent = camera
        interact.parent = camera
        winScreen.parent = camera
        failScreen.parent = camera
        bar.parent = camera
    }

    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        setTimer(timer)

        camera.bind(shader)

        /**rendern der Szene**/
        floor.render(shader)
        sky.render(shader)
        body.render(shader)
        door.render(shader)
        bar.render(shader)
        roomBase.render(shader)
        roomBaseFurniture.render(shader)
        roomL.render(shader)
        roomLFurniture.render(shader)
        roomR.render(shader)
        RoomRFurniture.render(shader)


        /**Rendern der Lichter**/
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


        collision()                     //Die Kollisionsabfrage wird aufgerufen


        if(currInteraction != -1)
            interaction()               //Die Interaktionsabfrage wird unter der Bedingung aufgerufen, dass die letzte Interaktion noch nicht getätigt ist


        if(currInteraction == -1) {
            winScreen.render(shader)    //Der Win-Screen wird gerendert unter der Bedingung, dass die letzte Interaktion getätigt wurde
            timer = 100f                //"Deaktivierung" des Timers

            if(window.currentTime >= timer2 + 2f)
                cleanup()
        }
    }

    fun zeitBerechnung(t : Float) : Float{
        return 0.009f/t
    }

    fun update(dt: Float, t: Float) {               //Die WASD-Steuerung

        if(window.getKeyState(GLFW_KEY_W))
            body.translateLocal(Vector3f(0.0f, 0.0f, -2 * dt))

        if(window.getKeyState(GLFW_KEY_A))
            body.translateLocal(Vector3f(-2 * dt, 0.0f, 0.0f))

        if(window.getKeyState(GLFW_KEY_D))
            body.translateLocal(Vector3f(2 * dt, 0.0f, 0.0f))

        if(window.getKeyState(GLFW_KEY_S))
            body.translateLocal(Vector3f(0.0f, 0.0f, 2 * dt))

        bar.translateLocal(Vector3f(zeitBerechnung(timer), 0.0f, 0.0f))     //Der Zeitbalken am unteren Ende des Bildschirms wird bewegt

    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {

        if (window.getKeyState(GLFW_KEY_L)) {                                   //Die anderen Shader können aktiviert werden
            if (shader == mitSpotShader) {
                shader = ohneSpotShader
            }
            else
            {
                shader = mitSpotShader
            }
        }

        if (window.getKeyState(GLFW_KEY_I)){

            if (shader == mitSpotShader || shader == ohneSpotShader){
                shader = raveLightShader
            }
            else
            {
                shader = ohneSpotShader
            }
        }

    }


    fun onMouseMove(xpos: Double, ypos: Double) {

        val deltaX = xpos - oldMousePosX                                //Die First-Person-Maussteuerung
        val deltaY = ypos - oldMousePosY

        oldMousePosX = xpos
        oldMousePosY = ypos

        if (bool) {
            body.rotateAroundPoint(0.0f, -Math.toRadians(deltaX.toFloat() * 0.04f) ,0.0f, body.getWorldPosition())
            camera.rotateLocal(-Math.toRadians(deltaY.toFloat() * 0.08f),0.0f, 0.0f)
        }
        bool = true
    }

    fun collision() {

        val pushDist = 0.004f   //definiert die Distanz, die der Spieler bei einer Kollision zurückgeschoben wird; muss bei schwächerer Hardware erhöht werden
        val collisionBuffer = 0.25f     //definiert die Dicke der Kollisionsfelder

        for(c in collisions) {              //Die Kollisionsabfrage greift auf die Kollisionsdefinitionen in der separaten Klasse "RoomData" zu
            if (body.getPosition().x > c.x && body.getPosition().x < c.z && body.getPosition().z < c.y && body.getPosition().z > c.w)   //Die Kollisionsabfrage funktioniert in 2D; um jede Wand und jedes Möbelstück wird ein Rechteck aufgespannt,
            {                                                                                                                           //welches die Kollisionen dieses beschreibt. Wenn der Spieler nun ein solches Feld betritt wird er wieder in
                if (body.getPosition().x > c.x && body.getPosition().x < (c.x + collisionBuffer))                                       //die entgegengesetzte Richtung zurückgeschoben.
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

        checklists[currInteraction].render(shader)      //Die Interaktionsabfrage funktioniert ähnlich der Kollisionsabfrage; In der separaten Klasse "RoomData" sind Aufgaben und
                                                        //ihre Interaktionsfelder definiert. Befindet sich der Spieler in dem Interaktionsfeld der aktuellen Aufgabe, so wird der
                                                        //Interaktionsprompt gerendert. Wenn der Spieler nun "E" drückt wird die Aufgabe erledigt und die nächste wird gezeigt.
                                                        //Wenn der Spieler die letzte Aufgabe erfüllt hat wird der "currInteraction"-Zähler auf -1 gesetzt um zu signalisieren,
                                                        //dass das Spiel zu Ende ist.
        if (body.getPosition().x > interactionAreas[currInteraction].x && body.getPosition().x < interactionAreas[currInteraction].z
            && body.getPosition().z < interactionAreas[currInteraction].y && body.getPosition().z > interactionAreas[currInteraction].w) {

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

    fun loadMesh (path : String, tempMaterial : Material) : Mesh {      //ausgelagerte Funktion zum Laden von Meshes; nur zur Ordnung erstellt

        val resTemp = OBJLoader.loadOBJ(path)
        val tempObj = resTemp.objects[0].meshes[0]
        return Mesh(tempObj.vertexData, tempObj.indexData, vertexAttributes, tempMaterial)
    }

    fun loadCollision (collisionOfRoom : MutableList<Vector4f>) {       //ausgelagerte Funktion zum Laden von Kollisionen; nur zur Ordnung erstellt

        var runCollisions = 0
        while (runCollisions < collisionOfRoom.size) {
            collisions.add(collisionOfRoom[runCollisions])
            runCollisions++
        }
    }

    fun loadMaterial(texDiff : String) : Material {                     //ausgelagerte Funktion zum Laden von Materialien; nur zur Ordnung erstellt

        val tempTextureEmit = Texture2D("assets/textures/black.png", true)
        val tempTextureDiff = Texture2D(texDiff,true)
        val tempTextureSpec = Texture2D("assets/textures/black.png",true)

        tempTextureEmit.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        tempTextureDiff.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        tempTextureSpec.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        return Material(tempTextureDiff, tempTextureEmit, tempTextureSpec, 60.0f, Vector2f(1.0f, 1.0f))
    }

    fun loadMaterial(texEmit : String, texDiff : String, texSpec : String) : Material {     //ausgelagerte Funktion zum Laden von Materialien; nur zur Ordnung erstellt
        //Mit diesem Konstruktor können auch die Emit- und Specular-Texturen geändert werden
        val tempTextureEmit = Texture2D(texEmit, true)
        val tempTextureDiff = Texture2D(texDiff,true)
        val tempTextureSpec = Texture2D(texSpec,true)

        tempTextureEmit.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        tempTextureDiff.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        tempTextureSpec.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        return Material(tempTextureDiff, tempTextureEmit, tempTextureSpec, 60.0f, Vector2f(1.0f, 1.0f))
    }

    fun loadTaskRB(){                                                                       //ausgelagerte Funktion zum Laden von Aufgaben; nur zur Ordnung erstellt

        val seedTaskRB = Random.nextInt(0, 2)

        val checklistRBMaterial = loadMaterial(rBTasks[seedTaskRB].first)

        checklistRBMesh = loadMesh("assets/models/checklist.obj", checklistRBMaterial)
        checklistRB.list.add(checklistRBMesh)

        interactionAreas.add(rBTasks[seedTaskRB].second)

        checklists.add(checklistRB)
    }

    fun loadTaskRL(taskList : MutableList<Pair<String, Vector4f>>){                         //ausgelagerte Funktion zum Laden von Aufgaben; nur zur Ordnung erstellt

        val seedTaskL = Random.nextInt(0, 2)

        val checklistRLMaterial = loadMaterial(taskList[seedTaskL].first)

        checklistLMesh = loadMesh("assets/models/checklist.obj", checklistRLMaterial)
        checklistL.list.add(checklistLMesh)

        interactionAreas.add(taskList[seedTaskL].second)

        checklists.add(checklistL)
    }

    fun loadTaskRR(taskList : MutableList<Pair<String, Vector4f>>){                         //ausgelagerte Funktion zum Laden von Aufgaben; nur zur Ordnung erstellt

        val seedTaskR = Random.nextInt(0, 2)

        val checklistRRMaterial = loadMaterial(taskList[seedTaskR].first)

        checklistRMesh = loadMesh("assets/models/checklist.obj", checklistRRMaterial)
        checklistR.list.add(checklistRMesh)

        interactionAreas.add(taskList[seedTaskR].second)

        checklists.add(checklistR)
    }

    fun loadTaskFinal() {                                                                   //ausgelagerte Funktion zum Laden von Aufgaben; nur zur Ordnung erstellt

        val checklistFinalMaterial = loadMaterial("assets/textures/text_zurArbeit.png")

        checklistFinalMesh = loadMesh("assets/models/checklist.obj", checklistFinalMaterial)
        checklistFinal.list.add(checklistFinalMesh)

        interactionAreas.add(Vector4f(-1f, 2f, 0f, 1.5f))

        checklists.add(checklistFinal)
    }

    fun setTimer(t: Float){                                                                 //Funktion für die Zeitbegrenzung

        if ( window.currentTime >= t){
            failScreen.render(shader)

            if(window.currentTime > t + 5f)
                cleanup()
        }
    }

    fun cleanup() {
        window.quit()
    }
}
