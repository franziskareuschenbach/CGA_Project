package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import cga.framework.GLError
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30.*

/**
 * Creates a Mesh object from vertexdata, intexdata and a given set of vertex attributes
 *
 * @param vertexdata plain float array of vertex data
 * @param indexdata  index data
 * @param attributes vertex attributes contained in vertex data
 * @throws Exception If the creation of the required OpenGL objects fails, an exception is thrown
 *
 * Created by Fabian on 16.09.2017.
 */
class Mesh(vertexdata: FloatArray, indexdata: IntArray, attributes: Array<VertexAttribute>, var material: Material? = null) {
    //private data
    private var vao = 0
    private var vbo = 0
    private var ibo = 0
    private var indexcount = indexdata.size

    init {
        /**Vertex Array Object erstellen und binden/aktivieren**/
        vao = glGenVertexArrays()
        glBindVertexArray(vao)

        /**Vertex Buffer Objects erstellen, binden/aktivieren, (mit data füllen)**/
        vbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferData(GL_ARRAY_BUFFER,vertexdata, GL_STATIC_DRAW)

        /**Index Buffer Objects erstellen, binden/aktivieren, (mit data füllen)**/
        ibo = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexdata, GL_STATIC_DRAW)

        /**Aktivierung und Spezifizierung der VertexAttribute**/

        for(m in attributes){
            glEnableVertexAttribArray(m.index)
            glVertexAttribPointer(m.index, m.n, m.type, false ,m.stride, m.offset)
        }

        /**Lösen der Bindung**/ //Nicht ganz sicher?
        glBindVertexArray(0)
    }
    /**
     * renders the mesh
     */
    fun render() {
        glBindVertexArray(vao)
        glDrawElements(GL_TRIANGLES, indexcount, GL_UNSIGNED_INT, 0)
        glBindVertexArray(0)
    }

    fun render(shaderProgram: ShaderProgram){
        material?.bind(shaderProgram)
        render()
    }

    /**
     * Deletes the previously allocated OpenGL objects for this mesh
     */
    fun cleanup() {
        if (ibo != 0) GL15.glDeleteBuffers(ibo)
        if (vbo != 0) GL15.glDeleteBuffers(vbo)
        if (vao != 0) glDeleteVertexArrays(vao)
    }
}
