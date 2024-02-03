package org.example.engine;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.vulkan.EXTDebugUtils.*;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Application {

    private static int debugCallback(int messageSeverity, int messageType, long pCallbackData, long pUserData) {
        VkDebugUtilsMessengerCallbackDataEXT callbackData = VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData);
        System.err.println("Validation layer: " + callbackData.pMessageString());
        return VK_FALSE;
    }

    private static int createDebugUtilsMessengerEXT(VkInstance instance,
                                                    VkDebugUtilsMessengerCreateInfoEXT createInfo,
                                                    VkAllocationCallbacks allocationCallbacks,
                                                    LongBuffer pDebugMessenger) {
        if(vkGetInstanceProcAddr(instance, "vkCreateDebugUtilsMessengerEXT") != NULL) return vkCreateDebugUtilsMessengerEXT(instance, createInfo, allocationCallbacks, pDebugMessenger);
        return VK_ERROR_EXTENSION_NOT_PRESENT;
    }

    private static void destroyDebugUtilsMessengerEXT(VkInstance instance,
                                                      long debugMessenger,
                                                      VkAllocationCallbacks allocationCallbacks) {
        if(vkGetInstanceProcAddr(instance, "vkDestroyDebugUtilsMessengerEXT") != NULL) vkDestroyDebugUtilsMessengerEXT(instance, debugMessenger, allocationCallbacks);
    }

    private static class QueueFamilyIndices {

        private Integer graphics;
        // private Integer render etc.

        private boolean isComplete() {
            return graphics != null;
        }

    }

    public final boolean debugMode;
    public final String title;
    private long window;
    private VkInstance instance;
    private long debugMessenger;
    private VkPhysicalDevice physicalDevice;
    private final Set<String> requiredVulkanValidationLayerNames;

    public Application(final String title, final boolean debugMode) {
        this.title = title == null ? "Engine Application" : title;
        this.debugMode = debugMode;
        if (debugMode) {
            requiredVulkanValidationLayerNames = new HashSet<>();
            requiredVulkanValidationLayerNames.add("VK_LAYER_LUNARG_standard_validation");
        } else {
            requiredVulkanValidationLayerNames = null;
        }
    }

    public void launch() {
        createWindow();
        createVulkan();
        loop();
        clean();
    }

    private void createWindow() {
        if(!glfwInit()) {
            throw new RuntimeException("Cannot initialize GLFW");
        }
        GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_NO_API);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
        this.window = GLFW.glfwCreateWindow(800, 800, this.title, 0,0);
    }

    private void createVulkan() {
        // create vulkan instance
        if (this.debugMode && !checkValidationLayersSupport()) {
            throw new RuntimeException("Vulkan validation layer requested but not supported");
        }
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkApplicationInfo appInfo = VkApplicationInfo.calloc(stack);
            appInfo.sType(VK_STRUCTURE_TYPE_APPLICATION_INFO);
            appInfo.pApplicationName(stack.UTF8Safe("engine"));
            appInfo.applicationVersion(VK_MAKE_VERSION(1,0,0));
            appInfo.pEngineName(stack.UTF8Safe("no engine"));
            appInfo.engineVersion(VK_MAKE_VERSION(1, 0, 0));
            appInfo.apiVersion(VK_API_VERSION_1_0);

            VkInstanceCreateInfo createInfo = VkInstanceCreateInfo.calloc(stack);
            createInfo.sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO);
            createInfo.pApplicationInfo(appInfo);
            createInfo.ppEnabledExtensionNames(getRequiredExtensions(stack));
            createInfo.ppEnabledLayerNames(null);

            if (debugMode) {
                createInfo.ppEnabledLayerNames(getValidationLayersAsPointerBuffer(stack));
                VkDebugUtilsMessengerCreateInfoEXT debugCreateInfo = VkDebugUtilsMessengerCreateInfoEXT.calloc(stack);
                debugCreateInfo.sType(VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT);
                debugCreateInfo.messageSeverity(VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT);
                debugCreateInfo.messageType(VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT);
                debugCreateInfo.pfnUserCallback(Application::debugCallback);
                createInfo.pNext(debugCreateInfo.address());
            }

            PointerBuffer instancePtr = stack.mallocPointer(1);
            if (vkCreateInstance(createInfo, null, instancePtr) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create Vulkan instance");
            }

            this.instance = new VkInstance(instancePtr.get(0), createInfo);
        }

        // setup debug messenger
        if (debugMode) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                VkDebugUtilsMessengerCreateInfoEXT createInfo = VkDebugUtilsMessengerCreateInfoEXT.calloc(stack);
                createInfo.sType(VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT);
                createInfo.messageSeverity(VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT);
                createInfo.messageType(VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT);
                createInfo.pfnUserCallback(Application::debugCallback);
                LongBuffer pDebugMessenger = stack.longs(VK_NULL_HANDLE);
                if(createDebugUtilsMessengerEXT(instance, createInfo, null, pDebugMessenger) != VK_SUCCESS) {
                    throw new RuntimeException("Failed to set up debug messenger");
                }
                debugMessenger = pDebugMessenger.get(0);
            }
        }

        // get physical device
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer deviceCount = stack.ints(0);
            vkEnumeratePhysicalDevices(instance, deviceCount, null);
            if(deviceCount.get(0) == 0) throw new RuntimeException("Failed to find GPUs with Vulkan support");
            PointerBuffer ppPhysicalDevices = stack.mallocPointer(deviceCount.get(0));
            vkEnumeratePhysicalDevices(instance, deviceCount, ppPhysicalDevices);
            for (int i = 0; i < ppPhysicalDevices.capacity(); i++) {
                VkPhysicalDevice device = new VkPhysicalDevice(ppPhysicalDevices.get(i), instance);
                if(isDeviceSuitable(device)) {
                    this.physicalDevice = device;
                    break;
                }
            }
            if (this.physicalDevice == null) throw new RuntimeException("Failed to find a suitable GPU");
        }
    }

    private boolean isDeviceSuitable(VkPhysicalDevice device) {
        QueueFamilyIndices indices = findQueueFamilies(device);
        return indices.isComplete();
    }

    private QueueFamilyIndices findQueueFamilies(VkPhysicalDevice device) {
        QueueFamilyIndices indices = new QueueFamilyIndices();

        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer queueFamilyCount = stack.ints(0);

            vkGetPhysicalDeviceQueueFamilyProperties(device, queueFamilyCount, null);
            VkQueueFamilyProperties.Buffer queueFamilies = VkQueueFamilyProperties.malloc(queueFamilyCount.get(0), stack);
            vkGetPhysicalDeviceQueueFamilyProperties(device, queueFamilyCount, queueFamilies);

            IntStream.range(0, queueFamilies.capacity())
                    .filter(index -> (queueFamilies.get(index).queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0)
                    .findFirst()
                    .ifPresent(index -> indices.graphics = index);
            return indices;
        }
    }

    private PointerBuffer getValidationLayersAsPointerBuffer(MemoryStack stack) {
        PointerBuffer buffer = stack.mallocPointer(requiredVulkanValidationLayerNames.size());
        requiredVulkanValidationLayerNames.stream()
                .map(stack::UTF8)
                .forEach(buffer::put);
        return buffer.rewind();
    }

    private PointerBuffer getRequiredExtensions(MemoryStack stack) {
        PointerBuffer glfwExtensions = glfwGetRequiredInstanceExtensions();
        if(debugMode) {
            PointerBuffer extensions = stack.mallocPointer(glfwExtensions.capacity() + 1);
            extensions.put(glfwExtensions);
            extensions.put(stack.UTF8(VK_EXT_DEBUG_UTILS_EXTENSION_NAME));
            // Rewind the buffer before returning it to reset its position back to 0
            return extensions.rewind();
        }
        return glfwExtensions;
    }

    private boolean checkValidationLayersSupport() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer layerCount = stack.ints(0);
            vkEnumerateInstanceLayerProperties(layerCount, null);
            VkLayerProperties.Buffer availableLayers = VkLayerProperties.malloc(layerCount.get(0), stack);
            vkEnumerateInstanceLayerProperties(layerCount, availableLayers);
            Set<String> availableLayerNames = availableLayers
                    .stream()
                    .map(VkLayerProperties::layerNameString)
                    .collect(Collectors.toSet());
            return availableLayerNames.containsAll(requiredVulkanValidationLayerNames);
        }
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();
        }
    }

    private void clean() {
        if (debugMode) {
            destroyDebugUtilsMessengerEXT(instance, debugMessenger, null);
        }
        vkDestroyInstance(instance, null);
        glfwDestroyWindow(window);
        glfwTerminate();
    }

}
