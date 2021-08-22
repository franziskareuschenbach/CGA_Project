package cga.framework

import cga.exercise.components.geometry.Material
import cga.exercise.components.geometry.VertexAttribute
import org.joml.Vector4f

val rBCollisions = mutableListOf<Vector4f>(
    //Raum
    Vector4f(-2.25f, 2.25f, 2.25f, 2f),
    Vector4f(-2.25f, 0f, -2f, -2f),
    Vector4f(-2.25f, -2f, 2.25f, -2.25f),
    Vector4f(2f, 0f, 2.5f, -2f),
    Vector4f(-2.25f, 2f, -2f, 1f),
    Vector4f(2f, 2f, 2.25f, 1f),
    //Moebel
    Vector4f(-2f, -1f, 2f, -2f),
    Vector4f(-2f, 2f, -1f, 1f),
    Vector4f(0f, 2f, 2f, 1f)
)

val rBTasks = mutableListOf<Pair<String, Vector4f>>(
    Pair("assets/textures/text_wasEssen.png", Vector4f(-2f, -0.5f, -1f, -1.5f)),
    Pair("assets/textures/text_schluesselFinden.png", Vector4f(0f, 1f, 2f, 0.5f))
)

val l0Collisions = mutableListOf<Vector4f>(
    //Raum
    Vector4f(-6.5f, 2.25f, -2.25f, 2f),
    Vector4f(-6.5f, 2f, -6.25f, -2f),
    Vector4f(-6.5f, -2f, -2.25f, -2.25f),
    //Moebel
    Vector4f(-5.25f, -1f, -3.25f, -2f),
    Vector4f(-3.25f, 0f, -2.25f, -2f),
    Vector4f(-3.25f, 2f, -2.25f, 1f),
    Vector4f(-6.25f, 2f, -5.25f, 1f),
    Vector4f(-6.25f, 0f, -5.25f, -2f)
)

val l0Tasks = mutableListOf<Pair<String, Vector4f>>(
    Pair("assets/textures/text_bettMachen.png", Vector4f(-6.25f, 0.5f, -4.75f, -1f)),
    Pair("assets/textures/text_schluesselFinden.png", Vector4f(-4.25f, -0.5f, -3.25f, -1f))
)

val l1Collisions = mutableListOf<Vector4f>(
    //Raum
    Vector4f(-5.5f, 2.25f, -2.25f, 2f),
    Vector4f(-5.5f, 2f, -5.25f, -2f),
    Vector4f(-5.5f, -2f, -2.25f, -2.25f),
    //Moebel
    Vector4f(-5.25f, -1f, -4.25f, -2f),
    Vector4f(-3.25f, -1f, -2.25f, -2f),
    Vector4f(-5.25f, 2f, -2.25f, 1f)
)

val l1Tasks = mutableListOf<Pair<String, Vector4f>>(
    Pair("assets/textures/text_pinkeln.png", Vector4f(-3.25f, -0.5f, -2.25f, -1f)),
    Pair("assets/textures/text_gesichtWaschen.png", Vector4f(-4.25f, 1f, -3.25f, 0.5f))
)

val l2Collisions = mutableListOf<Vector4f>(
    //Raum
    Vector4f(-4.5f, 3.25f, -2f, 3f),
    Vector4f(-4.5f, 3f, -4.25f, -1f),
    Vector4f(-4.5f, -1f, -2.25f, -1.25f),
    Vector4f(-2.25f, 3f, -2f, 2.25f),
    //Moebel
    Vector4f(-4.25f, 0f, -2.25f, -1f),
    Vector4f(-4.25f, 3f, -2.25f, 2f)
)

val l2Tasks = mutableListOf<Pair<String, Vector4f>>(
    Pair("assets/textures/text_pinkeln.png", Vector4f(-4.25f, 0.5f, -3.25f, 0f)),
    Pair("assets/textures/text_gesichtWaschen.png", Vector4f(-4.25f, 2f, -3.25f, 1.5f))
)

val l3Collisions = mutableListOf<Vector4f>(
    //Raum
    Vector4f(-5.5f, 1.25f, -2.25f, 1f),
    Vector4f(-5.5f, 1f, -5.25f, -1f),
    Vector4f(-5.5f, -1f, -2.25f, -1.25f),
    //Moebel
    Vector4f(-5.25f, 0f, -2.25f, -1f)
)

val l3Tasks = mutableListOf<Pair<String, Vector4f>>(
    Pair("assets/textures/text_pinkeln.png", Vector4f(-4.25f, 0.5f, -3.25f, 0f)),
    Pair("assets/textures/text_gesichtWaschen.png", Vector4f(-3.25f, 0.5f, -2.25f, 0f))
)

val l4Collisions = mutableListOf<Vector4f>(
    //Raum
    Vector4f(-4.5f, 1.25f, -2.25f, 1f),
    Vector4f(-4.5f, 1f, -4.25f, -1f),
    Vector4f(-4.5f, -1f, -2.25f, -1.25f),
    //Moebel
    Vector4f(-4.25f, 1f, -3.25f, -1f)
)

val l4Tasks = mutableListOf<Pair<String, Vector4f>>(
    Pair("assets/textures/text_bettMachen.png", Vector4f(-3.25f, 1f, -2.75f, -1f)),
    Pair("assets/textures/text_schluesselFinden.png", Vector4f(-3.25f, 1f, -2.75f, -1f))
)

val r0Collisions = mutableListOf<Vector4f>(
    //Raum
    Vector4f(2f, 3.25f, 6.5f, 3f),
    Vector4f(6.25f, 3f, 6.5f, -1f),
    Vector4f(2.25f, -1f, 6.5f, -1.25f),
    Vector4f(2f, 3f, 2.25f, 2.25f),
    //Moebel
    Vector4f(2.25f, 0f, 3.25f, -1f),
    Vector4f(4.25f, 0f, 6.25f, -1f),
    Vector4f(3.25f, 3f, 5.25f, 1f)
)

val r0Tasks = mutableListOf<Pair<String, Vector4f>>(
    Pair("assets/textures/text_bettMachen.png", Vector4f(2.75f, 3f, 5.75f, 0.5f)),
    Pair("assets/textures/text_schluesselFinden.png", Vector4f(4.25f, 0.5f, 6.25f, 0f))
)

val r1Collisions = mutableListOf<Vector4f>(
    //Raum
    Vector4f(2.25f, 1.25f, 6.5f, 1f),
    Vector4f(6.25f, 1f, 6.5f, -2f),
    Vector4f(2.25f, -2f, 6.5f, -2.25f),
    //Moebel
    Vector4f(3.25f, 0f, 5.25f, -2f)
)

val r1Tasks = mutableListOf<Pair<String, Vector4f>>(
    Pair("assets/textures/text_bettMachen.png", Vector4f(2.75f, 0.5f, 5.75f, -2f)),
    Pair("assets/textures/text_schluesselFinden.png", Vector4f(2.75f, 0.5f, 5.75f, -2f))
)

val r2Collisions = mutableListOf<Vector4f>(
    //Raum
    Vector4f(2.25f, 2.25f, 4.5f, 2f),
    Vector4f(4.25f, 2f, 4.5f, -2f),
    Vector4f(2.25f, -2f, 4.5f, -2.25f),
    //Moebel
    Vector4f(2.25f, -1f, 4.25f, -2f),
    Vector4f(2.25f, 2f, 4.25f, 1f)
)

val r2Tasks = mutableListOf<Pair<String, Vector4f>>(
    Pair("assets/textures/text_pinkeln.png", Vector4f(2.25f, -0.5f, 3.25f, -1f)),
    Pair("assets/textures/text_gesichtWaschen.png", Vector4f(2.25f, 1f, 3.25f, 0.5f))
)

val r3Collisions = mutableListOf<Vector4f>(
    //Raum
    Vector4f(2.25f, 2.25f, 4.5f, 2f),
    Vector4f(4.25f, 2f, 4.5f, -1f),
    Vector4f(2.25f, -1f, 4.5f, -1.25f),
    //Moebel
    Vector4f(2.25f, 0f, 4.25f, -1f),
    Vector4f(3.25f, 2f, 4.25f, 1f)
)

val r3Tasks = mutableListOf<Pair<String, Vector4f>>(
    Pair("assets/textures/text_pinkeln.png", Vector4f(2.25f, 0.5f, 3.25f, 0f)),
    Pair("assets/textures/text_gesichtWaschen.png", Vector4f(3.25f, 0.5f, 4.25f, 0f))
)

val r4Collisions = mutableListOf<Vector4f>(
    //Raum
    Vector4f(2.25f, 2.25f, 4.5f, 2f),
    Vector4f(4.25f, 2f, 4.5f, 0f),
    Vector4f(2.25f, 0f, 4.5f, -0.25f),
    //Moebel
    Vector4f(3.25f, 2f, 4.25f, 0f)
)

val r4Tasks = mutableListOf<Pair<String, Vector4f>>(
    Pair("assets/textures/text_bettMachen.png", Vector4f(2.75f, 2f, 3.25f, 0f)),
    Pair("assets/textures/text_schluesselFinden.png", Vector4f(2.75f, 2f, 3.25f, 0f))
)

class RoomData () {}

