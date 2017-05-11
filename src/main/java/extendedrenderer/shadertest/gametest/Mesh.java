package extendedrenderer.shadertest.gametest;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * Created by corosus on 08/05/17.
 */
public class Mesh {

    private int vaoId;

    //private int posVboId;

    //private int colourVboId;

    //private int idxVboId;

    private List<Integer> vboIdList = new ArrayList<>();

    private int vertexCount;

    public Mesh(float[] positions, float[] textCoords, int[] indices) {

        vertexCount = indices.length;

        FloatBuffer verticesBuffer = null;
        IntBuffer indicesBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        try {

            verticesBuffer = BufferUtils.createFloatBuffer(positions.length);//MemoryUtil.memAllocFloat(vertices.length);
            //vertexCount = positions.length;
            verticesBuffer.put(positions).flip();

            // Create the VAO and bind to it
            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);



            // Create the VBO and bint to it
            int posVboId = glGenBuffers();
            vboIdList.add(posVboId);
            glBindBuffer(GL_ARRAY_BUFFER, posVboId);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
            // Define structure of the data
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            //might not be needed, but added when downgrading to GLSL 120
            glEnableVertexAttribArray(0);

            //tex vbo
            int texVboId = glGenBuffers();
            vboIdList.add(texVboId);
            textCoordsBuffer = BufferUtils.createFloatBuffer(textCoords.length);
            textCoordsBuffer.put(textCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, texVboId);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            //????
            //glEnableVertexAttribArray(1);

            //index vbo
            int idxVboId = glGenBuffers();
            vboIdList.add(idxVboId);
            indicesBuffer = BufferUtils.createIntBuffer(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            // Unbind the VBO
            glBindBuffer(GL_ARRAY_BUFFER, 0);

            // Unbind the VAO
            glBindVertexArray(0);
        } finally {
            /**
             * TODO: test if we need to actually free the memory since we have to use BufferUtils.createFloatBuffer instead of MemoryUtil.memAllocFloat
             * It's not trivial because I want to make it optional and using jemalloc requires explicit je_free calls to avoid leaking memory. Existing usages of BufferUtils do not have that requirement and will have to be adjusted accordingly.
             *
             * BufferUtils is more automatic, doesnt need freeing, but can be slower and risks memory fragmentation, MemoryUtil gives more control and responsibility
             */
            if (verticesBuffer != null) {
                //MemoryUtil.memFree(verticesBuffer);
            }
        }
    }

    public void render() {
        //System.out.println("render start");
        // Draw the mesh
        glBindVertexArray(getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

        // Restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        //System.out.println("render end");
    }

    public int getVaoId() {
        return vaoId;
    }

    public void setVaoId(int vaoId) {
        this.vaoId = vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void setVertexCount(int vertexCount) {
        this.vertexCount = vertexCount;
    }

    public void cleanup() {
        glDisableVertexAttribArray(0);

        // Delete the VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vboIdList) {
            glDeleteBuffers(vboId);
        }

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }
}
