package org.example.engine.core.graphics;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FreeType;

import static org.lwjgl.stb.STBTruetype.*;

public final class FontBuilder {



    private FontBuilder() {}

    public synchronized static void buildFont(final String path, int fontSize) {
        try (MemoryStack stack = MemoryStack.stackPush()){
            int error;
            // Handle for the FreeType library
            PointerBuffer ftLibrary = stack.mallocPointer(1);;
            error = FreeType.FT_Init_FreeType(ftLibrary);
            if (error != 0) {
                throw new GraphicsException("Could not initialize FreeType library. Error code: " + error);
            }

            // Allocate space for the face pointer
            PointerBuffer ftFace = stack.mallocPointer(1);
            error = FreeType.FT_New_Face(ftLibrary.get(0), path, 0, ftFace);
            if (error != 0) {
                throw new GraphicsException("Could not load font face. Error code: " + error);
            }

        }

    }


}
