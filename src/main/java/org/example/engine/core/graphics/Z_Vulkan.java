package org.example.engine.core.graphics;

import org.example.engine.core.collections.ArrayLong;
import org.lwjgl.vulkan.*;

import java.util.Set;

// TODO: move Vulkan initialization here.
// TODO: implement all Vulkan related API functionality here, such as creating shaders etc.
public class Z_Vulkan {

    // window stuff belongs here? probably no
    private long window;
    private int windowWidth;
    public int windowHeight;
    // belong here? probably no
    public boolean debugMode;


    public static Set<String> requiredVulkanValidationLayerNames;
    public static Set<String> deviceExtensionNames;
    public static VkInstance instance;
    public static long debugMessenger;
    public static long surface;
    public static VkPhysicalDevice physicalDevice;
    public static VkDevice logicalDevice;
    public static VkQueue graphicsQueue;
    public static VkQueue presentationQueue;

    // swap chain
    public static long swapChain;
    public static ArrayLong swapChainImages;
    public static ArrayLong swapChainImageViews;
    public static int swapChainImageFormat;
    public static VkExtent2D swapChainExtent;

    public static long pipelineLayout;

    // TODO: add anisotropic filtering:
    /*
    http://marcelbraghetto.github.io/a-simple-triangle/2019/10/06/part-27/

    VkSamplerCreateInfo x;
        x.anisotropyEnable();
        x.maxAnisotropy(16);
     */

}
