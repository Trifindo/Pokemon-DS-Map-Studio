/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

import com.jogamp.opengl.GL2ES1;
//import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
//import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;
import static com.jogamp.opengl.GL2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2.GL_VERTEX_SHADER;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GLContext;


/**
 *
 * @author Trifindo
 */
public class GlUtils {
    public static int createShaderProgram(String vertPath, String fragPath) {
        GL2ES2 gl = GLContext.getCurrentGL().getGL2ES2();
        String vshaderSource[];
        String fshaderSource[];

        int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
        int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);

        vshaderSource = Utils.readShaderAsResource(vertPath);
        fshaderSource = Utils.readShaderAsResource(fragPath);

        gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
        gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0);

        gl.glCompileShader(vShader);
        gl.glCompileShader(fShader);

        int vfprogram = gl.glCreateProgram();
        gl.glAttachShader(vfprogram, vShader);
        gl.glAttachShader(vfprogram, fShader);
        gl.glLinkProgram(vfprogram);

        gl.glDeleteShader(vShader);
        gl.glDeleteShader(fShader);
        return vfprogram;
    }
}
