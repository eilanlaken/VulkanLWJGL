package org.example.engine.core.graphics;

import org.example.engine.core.collections.ArrayLong;
import org.example.engine.core.files.FileUtils;
import org.example.engine.core.math.MathUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.vulkan.EXTDebugUtils.*;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.system.MemoryUtil.NULL;

// TODO: move everything Vulkan from here to the static global Vulkan instance.
public class Vulkan_tmp {

    public static final int DEFAULT_WINDOW_WIDTH = 720;
    public static final int DEFAULT_WINDOW_HEIGHT = 420;
    private static final int UINT32_MAX = 0xFFFFFFFF;

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

    private static final class QueueFamilyIndices {

        private Integer graphics;
        private Integer presentation;

        private boolean isComplete() {
            return graphics != null && presentation != null;
        }

        public int[] unique() {
            return IntStream.of(graphics, presentation).distinct().toArray();
        }

        public int[] array() {
            return new int[] {graphics, presentation};
        }

    }

    private static final class SwapChainSupportDetails {

        private VkSurfaceCapabilitiesKHR capabilities;
        private VkSurfaceFormatKHR.Buffer formats;
        private IntBuffer presentationModes;

    }

    private final Set<String> requiredVulkanValidationLayerNames;
    private final Set<String> deviceExtensionNames;

    public final boolean debugMode;
    public final String title;
    private long window;
    private int windowWidth;
    private int windowHeight;
    private VkInstance instance;
    private long debugMessenger;
    private long surface;
    private VkPhysicalDevice physicalDevice;
    private VkDevice logicalDevice;
    private VkQueue graphicsQueue;
    private VkQueue presentationQueue;

    // swap chain
    private long swapChain;
    private ArrayLong swapChainImages;
    private ArrayLong swapChainImageViews;
    private int swapChainImageFormat;
    private VkExtent2D swapChainExtent;

    private long pipelineLayout;

    public Vulkan_tmp(final String title, final boolean debugMode) {
        this.title = title == null ? "Engine Application" : title;
        this.windowWidth = DEFAULT_WINDOW_WIDTH;
        this.windowHeight = DEFAULT_WINDOW_HEIGHT;
        this.debugMode = debugMode;
        if (debugMode) {
            requiredVulkanValidationLayerNames = new HashSet<>();
            requiredVulkanValidationLayerNames.add("VK_LAYER_KHRONOS_validation");
        } else {
            requiredVulkanValidationLayerNames = null;
        }
        deviceExtensionNames = Stream.of(VK_KHR_SWAPCHAIN_EXTENSION_NAME).collect(Collectors.toSet());
    }

    public void launch() {
        createWindow();
        initVulkan();
        loop();
        clean();
    }

    private void createWindow() {
        if(!glfwInit()) {
            throw new RuntimeException("Cannot initialize GLFW");
        }
        GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_NO_API);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
        this.window = GLFW.glfwCreateWindow(windowWidth, windowHeight, this.title, 0,0);
    }

    private void initVulkan() {
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
                createInfo.ppEnabledLayerNames(convertToPointerBuffer(stack, requiredVulkanValidationLayerNames));
                VkDebugUtilsMessengerCreateInfoEXT debugCreateInfo = VkDebugUtilsMessengerCreateInfoEXT.calloc(stack);
                debugCreateInfo.sType(VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT);
                debugCreateInfo.messageSeverity(VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT);
                debugCreateInfo.messageType(VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT);
                debugCreateInfo.pfnUserCallback(Vulkan_tmp::debugCallback);
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
                createInfo.pfnUserCallback(Vulkan_tmp::debugCallback);
                LongBuffer pDebugMessenger = stack.longs(VK_NULL_HANDLE);
                if(createDebugUtilsMessengerEXT(instance, createInfo, null, pDebugMessenger) != VK_SUCCESS) {
                    throw new RuntimeException("Failed to set up debug messenger");
                }
                debugMessenger = pDebugMessenger.get(0);
            }
        }

        // create surface
        try(MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer pSurface = stack.longs(VK_NULL_HANDLE);
            if(glfwCreateWindowSurface(instance, window, null, pSurface) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create window surface");
            }
            this.surface = pSurface.get(0);
        }

        // pick physical device
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

        // create logical device
        try (MemoryStack stack = MemoryStack.stackPush()) {
            QueueFamilyIndices indices = findQueueFamilies(physicalDevice);
            int[] uniqueQueueFamilies = indices.unique();
            VkDeviceQueueCreateInfo.Buffer queueCreateInfos = VkDeviceQueueCreateInfo.calloc(uniqueQueueFamilies.length, stack);
            for (int i = 0; i < uniqueQueueFamilies.length; i++) {
                VkDeviceQueueCreateInfo queueCreateInfo = queueCreateInfos.get(i);
                queueCreateInfo.sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO);
                queueCreateInfo.queueFamilyIndex(uniqueQueueFamilies[i]);
                queueCreateInfo.pQueuePriorities(stack.floats(1.0f));
            }

            VkPhysicalDeviceFeatures deviceFeatures = VkPhysicalDeviceFeatures.calloc(stack);
            VkDeviceCreateInfo createInfo = VkDeviceCreateInfo.calloc(stack);
            createInfo.sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO);
            createInfo.pQueueCreateInfos(queueCreateInfos);
            createInfo.pEnabledFeatures(deviceFeatures);
            createInfo.ppEnabledExtensionNames(convertToPointerBuffer(stack, deviceExtensionNames));

            if(debugMode) {
                createInfo.ppEnabledLayerNames(convertToPointerBuffer(stack, requiredVulkanValidationLayerNames));
            }

            PointerBuffer pDevice = stack.pointers(VK_NULL_HANDLE);
            if(vkCreateDevice(physicalDevice, createInfo, null, pDevice) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create logical device");
            }

            this.logicalDevice = new VkDevice(pDevice.get(0), physicalDevice, createInfo);
            PointerBuffer pQueue = stack.pointers(VK_NULL_HANDLE);
            vkGetDeviceQueue(logicalDevice, indices.graphics, 0, pQueue);
            graphicsQueue = new VkQueue(pQueue.get(0), logicalDevice);
            vkGetDeviceQueue(logicalDevice, indices.presentation, 0, pQueue);
            presentationQueue = new VkQueue(pQueue.get(0), logicalDevice);
        }

        // create swapchain
        try (MemoryStack stack = MemoryStack.stackPush()) {
            SwapChainSupportDetails swapChainSupport = querySwapChainSupport(physicalDevice, stack);

            VkSurfaceFormatKHR surfaceFormat = chooseSwapSurfaceFormat(swapChainSupport.formats);
            int presentationMode = chooseSwapPresentMode(swapChainSupport.presentationModes);
            VkExtent2D extent = chooseSwapExtent(stack, swapChainSupport.capabilities);

            IntBuffer imageCount = stack.ints(swapChainSupport.capabilities.minImageCount() + 1);
            if (swapChainSupport.capabilities.maxImageCount() > 0 && imageCount.get(0) > swapChainSupport.capabilities.maxImageCount()) {
                imageCount.put(0, swapChainSupport.capabilities.maxImageCount());
            }

            VkSwapchainCreateInfoKHR createInfo = VkSwapchainCreateInfoKHR.calloc(stack);
            createInfo.sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR);
            createInfo.surface(surface);

            // image settings
            createInfo.minImageCount(imageCount.get(0));
            createInfo.imageFormat(surfaceFormat.format());
            createInfo.imageColorSpace(surfaceFormat.colorSpace());
            createInfo.imageExtent(extent);
            createInfo.imageArrayLayers(1);
            createInfo.imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT);

            QueueFamilyIndices indices = findQueueFamilies(physicalDevice);

            if (!indices.graphics.equals(indices.presentation)) {
                createInfo.imageSharingMode(VK_SHARING_MODE_CONCURRENT);
                createInfo.pQueueFamilyIndices(stack.ints(indices.graphics, indices.presentation));
            } else {
                createInfo.imageSharingMode(VK_SHARING_MODE_EXCLUSIVE);
            }

            createInfo.preTransform(swapChainSupport.capabilities.currentTransform());
            createInfo.compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR);
            createInfo.presentMode(presentationMode);
            createInfo.clipped(true);
            createInfo.oldSwapchain(VK_NULL_HANDLE);

            LongBuffer pSwapChain = stack.longs(VK_NULL_HANDLE);
            if (vkCreateSwapchainKHR(logicalDevice, createInfo, null, pSwapChain) != VK_SUCCESS) {
                throw new RuntimeException("Could not create swapchain.");
            }

            swapChain = pSwapChain.get(0);
            vkGetSwapchainImagesKHR(logicalDevice, swapChain, imageCount, null);
            LongBuffer pSwapchainImages = stack.mallocLong(imageCount.get(0));
            vkGetSwapchainImagesKHR(logicalDevice, swapChain, imageCount, pSwapchainImages);
            swapChainImages = new ArrayLong(imageCount.get(0));
            for (int i = 0; i < pSwapchainImages.capacity(); i++) {
                swapChainImages.add(pSwapchainImages.get(i));
            }
            swapChainImageFormat = surfaceFormat.format();
            swapChainExtent = VkExtent2D.create().set(extent);
        }

        // create image views for the images in the swapchain
        try (MemoryStack stack = MemoryStack.stackPush()) {
            swapChainImageViews = new ArrayLong();
            LongBuffer pImageView = stack.mallocLong(1);
            for (int i = 0; i < swapChainImages.size; i++) {
                VkImageViewCreateInfo createInfo = VkImageViewCreateInfo.calloc(stack);

                createInfo.sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO);
                createInfo.image(swapChainImages.items[i]);
                createInfo.viewType(VK_IMAGE_VIEW_TYPE_2D);
                createInfo.format(swapChainImageFormat);

                createInfo.components().r(VK_COMPONENT_SWIZZLE_IDENTITY);
                createInfo.components().g(VK_COMPONENT_SWIZZLE_IDENTITY);
                createInfo.components().b(VK_COMPONENT_SWIZZLE_IDENTITY);
                createInfo.components().a(VK_COMPONENT_SWIZZLE_IDENTITY);

                createInfo.subresourceRange().aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
                createInfo.subresourceRange().baseMipLevel(0);
                createInfo.subresourceRange().levelCount(1);
                createInfo.subresourceRange().baseMipLevel(0);
                createInfo.subresourceRange().layerCount(1);

                if (vkCreateImageView(logicalDevice, createInfo, null, pImageView) != VK_SUCCESS) throw new RuntimeException("Could not create image view.");
                swapChainImageViews.add(pImageView.get(0));
            }
        }

        // create rendering pipelines
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final String vertexShaderGlsl = FileUtils.getFileContent("shaders/shader.vert");
            final String fragmentShaderGlsl = FileUtils.getFileContent("shaders/shader.frag");

            Z_ShaderProgram shaderProgram = new Z_ShaderProgram(vertexShaderGlsl, fragmentShaderGlsl);

            long vertShaderModule = createShaderModule(shaderProgram.vertexShaderBytecode);
            long fragShaderModule = createShaderModule(shaderProgram.fragmentShaderBytecode);

            ByteBuffer entryPoint = stack.UTF8("main");
            VkPipelineShaderStageCreateInfo.Buffer shaderStages = VkPipelineShaderStageCreateInfo.calloc(2, stack);

            VkPipelineShaderStageCreateInfo vertShaderStageCreateInfo = shaderStages.get(0);
            vertShaderStageCreateInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO);
            vertShaderStageCreateInfo.stage(VK_SHADER_STAGE_VERTEX_BIT);
            vertShaderStageCreateInfo.module(vertShaderModule);
            vertShaderStageCreateInfo.pName(entryPoint);

            VkPipelineShaderStageCreateInfo fragShaderStageCreateInfo = shaderStages.get(1);
            fragShaderStageCreateInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO);
            fragShaderStageCreateInfo.stage(VK_SHADER_STAGE_FRAGMENT_BIT);
            fragShaderStageCreateInfo.module(fragShaderModule);
            fragShaderStageCreateInfo.pName(entryPoint);

            // VERTEX STAGE
            VkPipelineVertexInputStateCreateInfo vertexInputInfo = VkPipelineVertexInputStateCreateInfo.calloc(stack);
            vertexInputInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO);

            // ASSEMBLY STAGE
            VkPipelineInputAssemblyStateCreateInfo inputAssemblyInfo = VkPipelineInputAssemblyStateCreateInfo.calloc(stack);
            inputAssemblyInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO);
            inputAssemblyInfo.topology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST);
            inputAssemblyInfo.primitiveRestartEnable(false);

            // VIEWPORT
            VkViewport.Buffer viewport = VkViewport.calloc(1, stack);
            viewport.x(0.0f);
            viewport.y(0.0f);
            viewport.width(swapChainExtent.width());
            viewport.height(swapChainExtent.height());
            viewport.minDepth(0.0f);
            viewport.maxDepth(1.0f);
            VkRect2D.Buffer scissor = VkRect2D.calloc(1, stack);
            scissor.offset(VkOffset2D.calloc(stack).set(0,0));
            scissor.extent(swapChainExtent);
            VkPipelineViewportStateCreateInfo viewportCreateInfo = VkPipelineViewportStateCreateInfo.calloc(stack);
            viewportCreateInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO);
            viewportCreateInfo.pViewports(viewport);
            viewportCreateInfo.pScissors(scissor);

            // MULTISAMPLING
            VkPipelineMultisampleStateCreateInfo multisampleInfo = VkPipelineMultisampleStateCreateInfo.calloc(stack);
            multisampleInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO);
            multisampleInfo.sampleShadingEnable(false);
            multisampleInfo.rasterizationSamples(VK_SAMPLE_COUNT_1_BIT);

            // COLOR BLENDING
            VkPipelineColorBlendAttachmentState.Buffer colorBlendAttachment = VkPipelineColorBlendAttachmentState.calloc(1, stack);
            colorBlendAttachment.colorWriteMask(VK_COLOR_COMPONENT_R_BIT | VK_COLOR_COMPONENT_G_BIT | VK_COLOR_COMPONENT_B_BIT | VK_COLOR_COMPONENT_A_BIT);
            // TODO: optional. Modify and see
            // alpha blending -->
            colorBlendAttachment.blendEnable(true);
            colorBlendAttachment.srcColorBlendFactor(VK_BLEND_FACTOR_SRC_ALPHA);
            colorBlendAttachment.dstColorBlendFactor(VK_BLEND_FACTOR_ONE_MINUS_SRC_ALPHA);
            colorBlendAttachment.colorBlendOp(VK_BLEND_OP_ADD);
            colorBlendAttachment.srcAlphaBlendFactor(VK_BLEND_FACTOR_ONE);
            colorBlendAttachment.dstAlphaBlendFactor(VK_BLEND_FACTOR_ZERO);
            colorBlendAttachment.alphaBlendOp(VK_BLEND_OP_ADD);
            // <-- alpha blending
            VkPipelineColorBlendStateCreateInfo colorBlending = VkPipelineColorBlendStateCreateInfo.calloc(stack);
            colorBlending.sType(VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO);
            colorBlending.logicOpEnable(false);
            colorBlending.logicOp(VK_LOGIC_OP_COPY);
            colorBlending.pAttachments(colorBlendAttachment);
            colorBlending.blendConstants(stack.floats(0.0f, 0.0f, 0.0f, 0.0f));

            // finally: PIPELINE LAYOUT CREATION
            VkPipelineLayoutCreateInfo pipelineLayoutCreateInfo = VkPipelineLayoutCreateInfo.calloc(stack);
            pipelineLayoutCreateInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO);
            LongBuffer pPipelineLayout = stack.longs(VK_NULL_HANDLE);
            if (vkCreatePipelineLayout(logicalDevice, pipelineLayoutCreateInfo, null, pPipelineLayout) != VK_SUCCESS) throw new RuntimeException("Could not create rendering pipeline layout.");

            // RELEASE RESOURCES
            vkDestroyShaderModule(logicalDevice, vertShaderModule, null);
            vkDestroyShaderModule(logicalDevice, fragShaderModule, null);

            shaderProgram.free();
        }
    }

    private long createShaderModule(ByteBuffer spirvCode) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkShaderModuleCreateInfo createInfo = VkShaderModuleCreateInfo.calloc(stack);

            createInfo.sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO);
            createInfo.pCode(spirvCode);
            LongBuffer pShaderModule = stack.mallocLong(1);
            if (vkCreateShaderModule(logicalDevice, createInfo, null, pShaderModule) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create shader module");
            }

            return pShaderModule.get(0);
        }
    }

    private VkSurfaceFormatKHR chooseSwapSurfaceFormat(VkSurfaceFormatKHR.Buffer availableFormats) {
        return availableFormats.stream()
                .filter(availableFormat -> availableFormat.format() == VK_FORMAT_B8G8R8_UNORM)
                .filter(availableFormat -> availableFormat.colorSpace() == VK_COLOR_SPACE_SRGB_NONLINEAR_KHR)
                .findAny()
                .orElse(availableFormats.get(0));
    }

    private int chooseSwapPresentMode(IntBuffer availablePresentModes) {
        for (int i = 0; i < availablePresentModes.capacity(); i++) {
            if (availablePresentModes.get(i) == VK_PRESENT_MODE_MAILBOX_KHR) return availablePresentModes.get(i);
        }
        return VK_PRESENT_MODE_FIFO_KHR;
    }

    private VkExtent2D chooseSwapExtent(MemoryStack stack, VkSurfaceCapabilitiesKHR capabilities) {
        if (capabilities.currentExtent().width() != UINT32_MAX) {
            return capabilities.currentExtent();
        }

        VkExtent2D actualExtent = VkExtent2D.malloc(stack).set(windowWidth, windowHeight);
        VkExtent2D minExtent = capabilities.minImageExtent();
        VkExtent2D maxExtent = capabilities.maxImageExtent();
        actualExtent.height(MathUtils.clamp(minExtent.height(), maxExtent.height(), actualExtent.height()));
        actualExtent.width(MathUtils.clamp(minExtent.width(), maxExtent.width(), actualExtent.width()));
        return actualExtent;
    }

    private boolean isDeviceSuitable(VkPhysicalDevice device) {
        QueueFamilyIndices indices = findQueueFamilies(device);

        boolean extensionSupported = checkDeviceExtensionSupport(device);
        boolean swapChainAdequate = false;
        if (extensionSupported) {
            try(MemoryStack stack = MemoryStack.stackPush()) {
                SwapChainSupportDetails swapChainSupport = querySwapChainSupport(device, stack);
                swapChainAdequate = swapChainSupport.formats.hasRemaining() && swapChainSupport.presentationModes.hasRemaining();
            }
        }

        return indices.isComplete() && extensionSupported && swapChainAdequate;
    }

    private SwapChainSupportDetails querySwapChainSupport(VkPhysicalDevice device, MemoryStack stack) {
        SwapChainSupportDetails details = new SwapChainSupportDetails();
        details.capabilities = VkSurfaceCapabilitiesKHR.malloc(stack);
        vkGetPhysicalDeviceSurfaceCapabilitiesKHR(device, surface, details.capabilities);
        IntBuffer count = stack.ints(0);
        vkGetPhysicalDeviceSurfaceFormatsKHR(device, surface, count, null);

        if (count.get(0) != 0) {
            details.formats = VkSurfaceFormatKHR.malloc(count.get(0), stack);
            vkGetPhysicalDeviceSurfaceFormatsKHR(device, surface, count, details.formats);
        }

        vkGetPhysicalDeviceSurfacePresentModesKHR(device, surface, count, null);
        if (count.get(0) != 0) {
            details.presentationModes = stack.mallocInt(count.get(0));
            vkGetPhysicalDeviceSurfacePresentModesKHR(device, surface, count, details.presentationModes);
        }

        return details;
    }

    private boolean checkDeviceExtensionSupport(VkPhysicalDevice device) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer extensionCount = stack.ints(0);
            vkEnumerateDeviceExtensionProperties(device, (String) null, extensionCount, null);
            VkExtensionProperties.Buffer availableExtensions = VkExtensionProperties.malloc(extensionCount.get(0), stack);
            vkEnumerateDeviceExtensionProperties(device, (String) null, extensionCount, availableExtensions);
            return availableExtensions.stream()
                    .map(VkExtensionProperties::extensionNameString)
                    .collect(Collectors.toSet())
                    .containsAll(deviceExtensionNames);
        }
    }

    private QueueFamilyIndices findQueueFamilies(VkPhysicalDevice device) {
        QueueFamilyIndices indices = new QueueFamilyIndices();

        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer queueFamilyCount = stack.ints(0);

            vkGetPhysicalDeviceQueueFamilyProperties(device, queueFamilyCount, null);
            VkQueueFamilyProperties.Buffer queueFamilies = VkQueueFamilyProperties.malloc(queueFamilyCount.get(0), stack);
            vkGetPhysicalDeviceQueueFamilyProperties(device, queueFamilyCount, queueFamilies);

            IntBuffer presentSupport = stack.ints(VK_FALSE);

            for (int i = 0; i < queueFamilies.capacity() || !indices.isComplete(); i++) {
                if((queueFamilies.get(i).queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0) indices.graphics = i;
                vkGetPhysicalDeviceSurfaceSupportKHR(device, i, surface, presentSupport);
                if(presentSupport.get(0) == VK_TRUE) indices.presentation = i;
            }
            return indices;
        }
    }

    private PointerBuffer convertToPointerBuffer(MemoryStack stack, Collection<String> names) {
        PointerBuffer buffer = stack.mallocPointer(names.size());
        names.stream()
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
        vkDestroyPipelineLayout(logicalDevice, pipelineLayout, null);
        for (int i = 0; i < swapChainImageViews.size; i++) {
            vkDestroyImageView(logicalDevice, swapChainImageViews.items[i], null);
        }
        vkDestroySwapchainKHR(this.logicalDevice, swapChain, null);
        vkDestroyDevice(this.logicalDevice, null);
        if (this.debugMode) {
            destroyDebugUtilsMessengerEXT(this.instance, this.debugMessenger, null);
        }
        vkDestroySurfaceKHR(instance, surface, null);
        vkDestroyInstance(this.instance, null);
        glfwDestroyWindow(this.window);
        glfwTerminate();
    }

}
