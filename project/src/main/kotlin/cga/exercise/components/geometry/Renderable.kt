package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram

class Renderable(val list : MutableList<Mesh> = mutableListOf()) : IRenderable, Transformable() {

    override fun render(shaderProgram: ShaderProgram) {
        shaderProgram.use()
        shaderProgram.setUniform("model_matrix", getWorldModelMatrix(), false)
        for (m in list){
            m.render(shaderProgram)
        }
    }
}