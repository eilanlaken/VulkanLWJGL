package org.example.engine.core.graphics;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.util.shaderc.Shaderc;

import java.nio.ByteBuffer;

public class Z_ShaderProgram implements NativeResource {

    public final String vertexShaderCode;
    public long vertexShaderHandle;
    public ByteBuffer vertexShaderBytecode;

    public final String fragmentShaderCode;
    public long fragmentShaderHandle;
    public ByteBuffer fragmentShaderBytecode;

    public Z_ShaderProgram(final String vertexShaderCode, final String fragmentShaderCode) {
        this.vertexShaderCode = vertexShaderCode;
        this.fragmentShaderCode = fragmentShaderCode;
        compileShaderProgram(vertexShaderCode, fragmentShaderCode);
    }

    private void compileShaderProgram(final String vertexShaderCode, final String fragmentShaderCode) {
        long compiler = Shaderc.shaderc_compiler_initialize();
        if (compiler == MemoryUtil.NULL) throw new RuntimeException("Could not create LWJGL shader compiler.");

        // TODO: might have a problem with the "vertex" parameter into the file name for this.vertexShaderHandle = ...
        // compile vertex shader program
        this.vertexShaderHandle =
                Shaderc.shaderc_compile_into_spv(compiler, vertexShaderCode, Shaderc.shaderc_glsl_vertex_shader, "vertex", "main", MemoryUtil.NULL);
        if (this.vertexShaderHandle == MemoryUtil.NULL)
            throw new RuntimeException("Failed to compile vertex shader.");
        if (Shaderc.shaderc_result_get_compilation_status(this.vertexShaderHandle) != Shaderc.shaderc_compilation_status_success)
            throw new RuntimeException("Error compiling vertex shader into SPIR-V: \n\n" + vertexShaderCode + "\n\n" + "Compilation failed with errors:\n" + Shaderc.shaderc_result_get_error_message(this.vertexShaderHandle));
        this.vertexShaderBytecode = Shaderc.shaderc_result_get_bytes(vertexShaderHandle);

        // TODO: might have a problem with the "fragment" parameter into the file name for this.fragmentShaderHandle = ...
        // compile fragment shader program
        this.fragmentShaderHandle =
                Shaderc.shaderc_compile_into_spv(compiler, fragmentShaderCode, Shaderc.shaderc_glsl_fragment_shader, "fragment", "main", MemoryUtil.NULL);
        if (this.fragmentShaderHandle == MemoryUtil.NULL)
            throw new RuntimeException("Failed to compile fragment shader.");
        if (Shaderc.shaderc_result_get_compilation_status(this.fragmentShaderHandle) != Shaderc.shaderc_compilation_status_success)
            throw new RuntimeException("Error compiling fragment shader into SPIR-V: \n\n" + fragmentShaderCode + "\n\n" + "Compilation failed with errors:\n" + Shaderc.shaderc_result_get_error_message(this.fragmentShaderHandle));
        this.fragmentShaderBytecode = Shaderc.shaderc_result_get_bytes(fragmentShaderHandle);

        Shaderc.shaderc_compiler_release(compiler);
    }

    @Override
    public void free() {
        Shaderc.shaderc_result_release(vertexShaderHandle);
        vertexShaderBytecode = null;
        Shaderc.shaderc_result_release(fragmentShaderHandle);
        fragmentShaderBytecode = null;
    }
}
