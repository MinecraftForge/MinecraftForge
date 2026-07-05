/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.earlydisplay.vulkan;

import net.minecraftforge.fml.earlydisplay.*;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.*;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanRenderBackend extends BaseRenderBackend {
    private static final Logger LOGGER = LoggerFactory.getLogger("EARLYDISPLAY");

    /** Enables the Vulkan validation layer and debug messenger when set (and the layer is installed). */
    private static final boolean ENABLE_VALIDATION = Boolean.getBoolean("fml.earlydisplay.vulkan.validation");

    static final long UINT64_MAX = 0xFFFFFFFFFFFFFFFFL;
    private static final int MAX_QUADS = 16384;
    private static final int MAX_TEXTURES = 16;

    VkDevice device;
    private VkInstance instance;
    private long debugMessenger;
    private VkDebugUtilsMessengerCallbackEXT debugCallback;
    private VkPhysicalDevice physicalDevice;
    private VkQueue graphicsQueue;
    private int graphicsQueueFamily;

    private long surface;
    private long swapchain;
    private long[] swapchainImages;
    private long[] swapchainImageViews;
    private long[] framebuffers;
    private long renderPass;
    private int swapchainWidth;
    private int swapchainHeight;
    private int swapchainFormat;
    private boolean swapchainOutOfDate;

    private VulkanShader vkShader;
    private long descriptorPool;
    private long descriptorSet;
    private long sampler;

    private final long[] textureImages = new long[MAX_TEXTURES];
    private final long[] textureMemory = new long[MAX_TEXTURES];
    private final long[] textureViews = new long[MAX_TEXTURES];
    private long placeholderImage;
    private long placeholderMemory;
    private long placeholderView;

    private long commandPool;
    VkCommandBuffer commandBuffer;
    private long imageAvailableSemaphore;
    private long[] renderFinishedSemaphores;
    private long inFlightFence;
    private int currentImageIndex;

    long indexBuffer;
    long indexBufferMemory;

    float pushScreenWidth, pushScreenHeight;
    int pushRenderType, pushTextureNumber;

    private long window;
    private boolean closed;
    private int fboWidth, fboHeight;

    private final java.util.List<VulkanVertexBuilder> vertexBuilders = new java.util.ArrayList<>();

    @Override
    public void applyWindowHints() {
        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
    }

    @Override
    public long createWindow(int winWidth, int winHeight, String title) {
        this.window = glfwCreateWindow(winWidth, winHeight, title, 0L, 0L);
        if (this.window == 0L) throw new RuntimeException("Failed to create GLFW window");
        return this.window;
    }

    @Override
    public void initialize(long window, ColourScheme colours, int fbScale,
                           PerformanceInfo perfInfo, String mcVersion) {
        this.colourScheme = colours;

        try (var stack = MemoryStack.stackPush()) {
            createInstance(stack);
            createSurface(stack);
            pickPhysicalDevice(stack);
            this.version = queryVersion(stack);
            createLogicalDevice(stack);
            createSwapchain(stack);
            createImageViews(stack);
            createRenderPass(stack);
            createFramebuffers(stack);
            createCommandPool(stack);
            createCommandBuffer(stack);
            createSyncObjects(stack);
            createSampler(stack);
        }

        vkShader = new VulkanShader(this, device, renderPass);
        vkShader.init();
        this.shader = vkShader;

        createIndexBuffer();
        createPlaceholderTexture();
        createDescriptorPool();

        int[] w = new int[1], h = new int[1];
        glfwGetFramebufferSize(window, w, h);
        this.fboWidth = w[0];
        this.fboHeight = h[0];

        this.context = new RenderElement.DisplayContext(
            854, 480, fbScale, shader, colours, perfInfo, this, this::createBufferBuilder);
    }

    private void createInstance(MemoryStack stack) {
        var appInfo = VkApplicationInfo.calloc(stack)
            .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
            .pApplicationName(stack.UTF8("Forge Early Display"))
            .applicationVersion(VK_MAKE_VERSION(1, 0, 0))
            .pEngineName(stack.UTF8("Forge"))
            .engineVersion(VK_MAKE_VERSION(1, 0, 0))
            .apiVersion(VK_API_VERSION_1_0);

        PointerBuffer glfwExtensions = glfwGetRequiredInstanceExtensions();
        if (glfwExtensions == null)
            throw new RuntimeException("Failed to get required Vulkan instance extensions from GLFW");

        boolean hasValidation = false;
        if (ENABLE_VALIDATION) {
            var pCount = stack.ints(0);
            vkEnumerateInstanceLayerProperties(pCount, null);
            int layerCount = pCount.get(0);
            if (layerCount > 0) {
                var layerProps = VkLayerProperties.calloc(layerCount, stack);
                vkEnumerateInstanceLayerProperties(pCount, layerProps);
                for (int i = 0; i < layerCount; i++) {
                    if ("VK_LAYER_KHRONOS_validation".equals(layerProps.get(i).layerNameString())) {
                        hasValidation = true;
                        break;
                    }
                }
            }
            if (!hasValidation)
                LOGGER.warn("Vulkan validation requested but VK_LAYER_KHRONOS_validation is not installed");
        }

        PointerBuffer extensions = glfwExtensions;
        if (hasValidation) {
            extensions = stack.mallocPointer(glfwExtensions.remaining() + 1);
            for (int i = 0; i < glfwExtensions.remaining(); i++)
                extensions.put(i, glfwExtensions.get(i));
            extensions.put(glfwExtensions.remaining(), stack.UTF8(EXTDebugUtils.VK_EXT_DEBUG_UTILS_EXTENSION_NAME));
        }

        var createInfo = VkInstanceCreateInfo.calloc(stack)
            .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
            .pApplicationInfo(appInfo)
            .ppEnabledExtensionNames(extensions);

        if (hasValidation)
            createInfo.ppEnabledLayerNames(stack.pointers(stack.UTF8("VK_LAYER_KHRONOS_validation")));

        var pInstance = stack.mallocPointer(1);
        vkCheck(vkCreateInstance(createInfo, null, pInstance), "Failed to create VkInstance");
        instance = new VkInstance(pInstance.get(0), createInfo);

        if (hasValidation)
            createDebugMessenger(stack);
    }

    private void createDebugMessenger(MemoryStack stack) {
        debugCallback = VkDebugUtilsMessengerCallbackEXT.create((severity, type, pData, pUser) -> {
            var data = VkDebugUtilsMessengerCallbackDataEXT.create(pData);
            if ((severity & EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT) != 0)
                LOGGER.error("[VK] {}", data.pMessageString());
            else
                LOGGER.warn("[VK] {}", data.pMessageString());
            return VK_FALSE;
        });
        var dbgCreateInfo = VkDebugUtilsMessengerCreateInfoEXT.calloc(stack)
            .sType(EXTDebugUtils.VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT)
            .messageSeverity(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT
                | EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT)
            .messageType(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT
                | EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT
                | EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT)
            .pfnUserCallback(debugCallback);
        var pMessenger = stack.mallocLong(1);
        if (EXTDebugUtils.vkCreateDebugUtilsMessengerEXT(instance, dbgCreateInfo, null, pMessenger) == VK_SUCCESS)
            debugMessenger = pMessenger.get(0);
    }

    private void createSurface(MemoryStack stack) {
        var pSurface = stack.mallocLong(1);
        vkCheck(glfwCreateWindowSurface(instance, window, null, pSurface), "Failed to create window surface");
        surface = pSurface.get(0);
    }

    private void pickPhysicalDevice(MemoryStack stack) {
        var pCount = stack.ints(0);
        vkEnumeratePhysicalDevices(instance, pCount, null);
        int count = pCount.get(0);
        if (count == 0) throw new RuntimeException("No GPU with Vulkan support");

        var pDevices = stack.mallocPointer(count);
        vkEnumeratePhysicalDevices(instance, pCount, pDevices);
        graphicsQueueFamily = -1;

        for (int i = 0; i < count && graphicsQueueFamily < 0; i++) {
            physicalDevice = new VkPhysicalDevice(pDevices.get(i), instance);
            var pQFCount = stack.ints(0);
            vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, pQFCount, null);
            int qfCount = pQFCount.get(0);
            var qfProps = VkQueueFamilyProperties.calloc(qfCount, stack);
            vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, pQFCount, qfProps);

            for (int j = 0; j < qfCount; j++) {
                if ((qfProps.get(j).queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0) {
                    var pPresent = stack.ints(0);
                    vkGetPhysicalDeviceSurfaceSupportKHR(physicalDevice, j, surface, pPresent);
                    if (pPresent.get(0) == VK_TRUE) {
                        graphicsQueueFamily = j;
                        break;
                    }
                }
            }
        }
        if (graphicsQueueFamily < 0) throw new RuntimeException("No suitable queue family");
    }

    private String queryVersion(MemoryStack stack) {
        var props = VkPhysicalDeviceProperties.calloc(stack);
        vkGetPhysicalDeviceProperties(physicalDevice, props);
        int api = props.apiVersion();
        return "Vulkan %d.%d.%d on %s".formatted(
            VK_VERSION_MAJOR(api), VK_VERSION_MINOR(api), VK_VERSION_PATCH(api),
            props.deviceNameString());
    }

    private void createLogicalDevice(MemoryStack stack) {
        var queuePriority = stack.floats(1.0f);
        var queueCreateInfo = VkDeviceQueueCreateInfo.calloc(1, stack)
            .sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
            .queueFamilyIndex(graphicsQueueFamily)
            .pQueuePriorities(queuePriority);
        VkDeviceQueueCreateInfo.nqueueCount(queueCreateInfo.address(), 1);

        var deviceExtensions = stack.pointers(stack.UTF8(VK_KHR_SWAPCHAIN_EXTENSION_NAME));

        var createInfo = VkDeviceCreateInfo.calloc(stack)
            .sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
            .pQueueCreateInfos(queueCreateInfo)
            .ppEnabledExtensionNames(deviceExtensions);

        var pDevice = stack.mallocPointer(1);
        vkCheck(vkCreateDevice(physicalDevice, createInfo, null, pDevice), "Failed to create VkDevice");
        device = new VkDevice(pDevice.get(0), physicalDevice, createInfo);

        var pQueue = stack.mallocPointer(1);
        vkGetDeviceQueue(device, graphicsQueueFamily, 0, pQueue);
        graphicsQueue = new VkQueue(pQueue.get(0), device);
    }

    private void createSwapchain(MemoryStack stack) {
        var caps = VkSurfaceCapabilitiesKHR.calloc(stack);
        vkGetPhysicalDeviceSurfaceCapabilitiesKHR(physicalDevice, surface, caps);

        var pFmtCount = stack.ints(0);
        vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, pFmtCount, null);
        int nFormats = pFmtCount.get(0);
        var formats = VkSurfaceFormatKHR.calloc(nFormats, stack);
        vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, pFmtCount, formats);

        swapchainFormat = VK_FORMAT_B8G8R8A8_UNORM;
        int colorSpace = VK_COLOR_SPACE_SRGB_NONLINEAR_KHR;
        for (int i = 0; i < nFormats; i++) {
            var f = formats.get(i);
            if (f.format() == VK_FORMAT_B8G8R8A8_UNORM) {
                swapchainFormat = f.format();
                colorSpace = f.colorSpace();
                break;
            }
            if (f.format() == VK_FORMAT_R8G8B8A8_UNORM) {
                swapchainFormat = f.format();
                colorSpace = f.colorSpace();
            }
        }

        swapchainWidth = caps.currentExtent().width();
        swapchainHeight = caps.currentExtent().height();
        if (swapchainWidth == 0xFFFFFFFF) {
            swapchainWidth = Math.max(1, fboWidth);
            swapchainHeight = Math.max(1, fboHeight);
        }
        swapchainWidth = Math.max(1, swapchainWidth);
        swapchainHeight = Math.max(1, swapchainHeight);

        int imageCount = Math.max(caps.minImageCount(), 2);
        if (caps.maxImageCount() > 0 && imageCount > caps.maxImageCount())
            imageCount = caps.maxImageCount();

        var pModeCount = stack.ints(0);
        vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, surface, pModeCount, null);
        var presentModes = stack.mallocInt(pModeCount.get(0));
        vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, surface, pModeCount, presentModes);
        int presentMode = VK_PRESENT_MODE_FIFO_KHR;
        for (int i = 0; i < presentModes.capacity(); i++) {
            int pm = presentModes.get(i);
            if (pm == VK_PRESENT_MODE_IMMEDIATE_KHR) { presentMode = pm; break; }
            if (pm == VK_PRESENT_MODE_MAILBOX_KHR) presentMode = pm;
        }

        var info = VkSwapchainCreateInfoKHR.calloc(stack)
            .sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
            .surface(surface)
            .minImageCount(imageCount)
            .imageFormat(swapchainFormat)
            .imageColorSpace(colorSpace)
            .imageExtent(e -> e.set(swapchainWidth, swapchainHeight))
            .imageArrayLayers(1)
            .imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT)
            .imageSharingMode(VK_SHARING_MODE_EXCLUSIVE)
            .preTransform(caps.currentTransform())
            .compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR)
            .presentMode(presentMode)
            .clipped(true);

        var pSwapchain = stack.mallocLong(1);
        vkCheck(vkCreateSwapchainKHR(device, info, null, pSwapchain), "Failed to create swapchain");
        swapchain = pSwapchain.get(0);

        var pImageCount = stack.ints(0);
        vkGetSwapchainImagesKHR(device, swapchain, pImageCount, null);
        swapchainImages = new long[pImageCount.get(0)];
        var pImages = stack.mallocLong(swapchainImages.length);
        vkGetSwapchainImagesKHR(device, swapchain, pImageCount, pImages);
        for (int i = 0; i < swapchainImages.length; i++)
            swapchainImages[i] = pImages.get(i);

        swapchainOutOfDate = false;
    }

    private void createImageViews(MemoryStack stack) {
        swapchainImageViews = new long[swapchainImages.length];
        for (int i = 0; i < swapchainImages.length; i++) {
            var info = VkImageViewCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO)
                .image(swapchainImages[i])
                .viewType(VK_IMAGE_VIEW_TYPE_2D)
                .format(swapchainFormat)
                .components(c -> {})
                .subresourceRange(r -> r
                    .aspectMask(VK_IMAGE_ASPECT_COLOR_BIT)
                    .baseMipLevel(0).levelCount(1)
                    .baseArrayLayer(0).layerCount(1));
            var pView = stack.mallocLong(1);
            vkCheck(vkCreateImageView(device, info, null, pView), "Failed to create swapchain image view");
            swapchainImageViews[i] = pView.get(0);
        }
    }

    private void createRenderPass(MemoryStack stack) {
        var colorAttachment = VkAttachmentDescription.calloc(1, stack)
            .format(swapchainFormat)
            .samples(VK_SAMPLE_COUNT_1_BIT)
            .loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR)
            .storeOp(VK_ATTACHMENT_STORE_OP_STORE)
            .stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE)
            .stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE)
            .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
            .finalLayout(VK_IMAGE_LAYOUT_PRESENT_SRC_KHR);

        var colorAttachmentRef = VkAttachmentReference.calloc(1, stack)
            .attachment(0)
            .layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);

        var subpass = VkSubpassDescription.calloc(1, stack)
            .pipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS)
            .colorAttachmentCount(1)
            .pColorAttachments(colorAttachmentRef);

        var dependency = VkSubpassDependency.calloc(1, stack)
            .srcSubpass(VK_SUBPASS_EXTERNAL)
            .dstSubpass(0)
            .srcStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT)
            .srcAccessMask(0)
            .dstStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT)
            .dstAccessMask(VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT);

        var info = VkRenderPassCreateInfo.calloc(stack)
            .sType(VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO)
            .pAttachments(colorAttachment)
            .pSubpasses(subpass)
            .pDependencies(dependency);

        var pRenderPass = stack.mallocLong(1);
        vkCheck(vkCreateRenderPass(device, info, null, pRenderPass), "Failed to create render pass");
        renderPass = pRenderPass.get(0);
    }

    private void createFramebuffers(MemoryStack stack) {
        framebuffers = new long[swapchainImageViews.length];
        for (int i = 0; i < swapchainImageViews.length; i++) {
            var info = VkFramebufferCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO)
                .renderPass(renderPass)
                .pAttachments(stack.longs(swapchainImageViews[i]))
                .width(swapchainWidth)
                .height(swapchainHeight)
                .layers(1);
            var pFB = stack.mallocLong(1);
            vkCheck(vkCreateFramebuffer(device, info, null, pFB), "Failed to create framebuffer");
            framebuffers[i] = pFB.get(0);
        }
    }

    private void createCommandPool(MemoryStack stack) {
        var info = VkCommandPoolCreateInfo.calloc(stack)
            .sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO)
            .flags(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT)
            .queueFamilyIndex(graphicsQueueFamily);
        var pPool = stack.mallocLong(1);
        vkCheck(vkCreateCommandPool(device, info, null, pPool), "Failed to create command pool");
        commandPool = pPool.get(0);
    }

    private void createCommandBuffer(MemoryStack stack) {
        var info = VkCommandBufferAllocateInfo.calloc(stack)
            .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
            .commandPool(commandPool)
            .level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
            .commandBufferCount(1);
        var pCmd = stack.mallocPointer(1);
        vkCheck(vkAllocateCommandBuffers(device, info, pCmd), "Failed to allocate command buffer");
        commandBuffer = new VkCommandBuffer(pCmd.get(0), device);
    }

    private void createSyncObjects(MemoryStack stack) {
        var semInfo = VkSemaphoreCreateInfo.calloc(stack)
            .sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO);
        var fenceInfo = VkFenceCreateInfo.calloc(stack)
            .sType(VK_STRUCTURE_TYPE_FENCE_CREATE_INFO)
            .flags(VK_FENCE_CREATE_SIGNALED_BIT);

        var pSem = stack.mallocLong(1);
        vkCreateSemaphore(device, semInfo, null, pSem);
        imageAvailableSemaphore = pSem.get(0);
        renderFinishedSemaphores = new long[swapchainImages.length];
        for (int i = 0; i < swapchainImages.length; i++) {
            vkCreateSemaphore(device, semInfo, null, pSem);
            renderFinishedSemaphores[i] = pSem.get(0);
        }
        var pFence = stack.mallocLong(1);
        vkCreateFence(device, fenceInfo, null, pFence);
        inFlightFence = pFence.get(0);
    }

    private void createSampler(MemoryStack stack) {
        var info = VkSamplerCreateInfo.calloc(stack)
            .sType(VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO)
            .magFilter(VK_FILTER_LINEAR)
            .minFilter(VK_FILTER_LINEAR)
            .addressModeU(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
            .addressModeV(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
            .addressModeW(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE)
            .borderColor(VK_BORDER_COLOR_INT_OPAQUE_BLACK)
            .unnormalizedCoordinates(false)
            .compareEnable(false)
            .compareOp(VK_COMPARE_OP_ALWAYS)
            .mipmapMode(VK_SAMPLER_MIPMAP_MODE_LINEAR);
        var pSampler = stack.mallocLong(1);
        vkCheck(vkCreateSampler(device, info, null, pSampler), "Failed to create sampler");
        sampler = pSampler.get(0);
    }

    private void createDescriptorPool() {
        try (var stack = MemoryStack.stackPush()) {
            var poolSize = VkDescriptorPoolSize.calloc(1, stack)
                .type(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
                .descriptorCount(MAX_TEXTURES);
            var poolInfo = VkDescriptorPoolCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO)
                .maxSets(1)
                .pPoolSizes(poolSize);
            var pPool = stack.mallocLong(1);
            vkCheck(vkCreateDescriptorPool(device, poolInfo, null, pPool), "Failed to create descriptor pool");
            descriptorPool = pPool.get(0);

            var allocInfo = VkDescriptorSetAllocateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO)
                .descriptorPool(descriptorPool)
                .pSetLayouts(stack.longs(vkShader.descriptorSetLayout));
            var pSet = stack.mallocLong(1);
            vkCheck(vkAllocateDescriptorSets(device, allocInfo, pSet), "Failed to allocate descriptor set");
            descriptorSet = pSet.get(0);

            updateDescriptorSetAll();
        }
    }

    private void updateDescriptorSetAll() {
        if (descriptorSet == 0) return;
        try (var stack = MemoryStack.stackPush()) {
            var imageInfos = VkDescriptorImageInfo.calloc(MAX_TEXTURES, stack);
            for (int i = 0; i < MAX_TEXTURES; i++) {
                imageInfos.get(i)
                    .sampler(sampler)
                    .imageView(textureViews[i] != 0 ? textureViews[i] : placeholderView)
                    .imageLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);
            }
            var write = VkWriteDescriptorSet.calloc(1, stack)
                .sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET)
                .dstSet(descriptorSet)
                .dstBinding(0)
                .dstArrayElement(0)
                .descriptorType(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
                .descriptorCount(MAX_TEXTURES)
                .pImageInfo(imageInfos);
            vkUpdateDescriptorSets(device, write, null);
        }
    }

    private void createPlaceholderTexture() {
        ByteBuffer white = ByteBuffer.allocateDirect(4);
        white.put((byte) -1).put((byte) -1).put((byte) -1).put((byte) -1);
        white.flip();
        createTextureImage(white, 1, 1, -1, VK_FORMAT_R8G8B8A8_UNORM);
    }

    private void createIndexBuffer() {
        int size = MAX_QUADS * 6 * 4;
        try (var stack = MemoryStack.stackPush()) {
            var bufferInfo = VkBufferCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
                .size(size)
                .usage(VK_BUFFER_USAGE_INDEX_BUFFER_BIT)
                .sharingMode(VK_SHARING_MODE_EXCLUSIVE);
            var pBuffer = stack.mallocLong(1);
            vkCreateBuffer(device, bufferInfo, null, pBuffer);
            indexBuffer = pBuffer.get(0);

            var memReqs = VkMemoryRequirements.calloc(stack);
            vkGetBufferMemoryRequirements(device, indexBuffer, memReqs);
            int memType = findMemoryType(memReqs.memoryTypeBits(),
                VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
            var allocInfo = VkMemoryAllocateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                .allocationSize(memReqs.size())
                .memoryTypeIndex(memType);
            var pMem = stack.mallocLong(1);
            vkAllocateMemory(device, allocInfo, null, pMem);
            indexBufferMemory = pMem.get(0);
            vkBindBufferMemory(device, indexBuffer, indexBufferMemory, 0);

            var ppData = stack.mallocPointer(1);
            vkMapMemory(device, indexBufferMemory, 0, size, 0, ppData);
            long mapped = ppData.get(0);
            IntBuffer indices = MemoryUtil.memIntBuffer(mapped, MAX_QUADS * 6);
            for (int i = 0; i < MAX_QUADS; i++) {
                int base = i * 4;
                indices.put(base); indices.put(base + 1); indices.put(base + 2);
                indices.put(base + 1); indices.put(base + 3); indices.put(base + 2);
            }
            vkUnmapMemory(device, indexBufferMemory);
        }
    }

    // ===== Frame rendering =====

    @Override public void makeCurrent(long window) {}

    @Override
    public void beginFrame() {
        if (swapchainOutOfDate) {
            recreateSwapchain();
        }

        try (var stack = MemoryStack.stackPush()) {
            vkWaitForFences(device, inFlightFence, true, UINT64_MAX);
            vkResetFences(device, inFlightFence);

            // Previous frame's GPU work is complete; safe to reuse each builder's
            // per-frame vertex buffer region from the start again.
            for (var builder : vertexBuilders) builder.resetFrame();

            var pImageIndex = stack.ints(0);
            int err = vkAcquireNextImageKHR(device, swapchain, UINT64_MAX, imageAvailableSemaphore, VK_NULL_HANDLE, pImageIndex);
            if (err == VK_ERROR_OUT_OF_DATE_KHR) {
                swapchainOutOfDate = true;
                return;
            }
            if (err != VK_SUCCESS && err != VK_SUBOPTIMAL_KHR)
                throw new RuntimeException("Failed to acquire swapchain image: " + err);
            currentImageIndex = pImageIndex.get(0);

            vkResetCommandBuffer(commandBuffer, 0);
            var beginInfo = VkCommandBufferBeginInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);
            vkCheck(vkBeginCommandBuffer(commandBuffer, beginInfo), "Failed to begin command buffer");

            var clearValue = VkClearValue.calloc(1, stack);
            clearValue.color().float32(0, colourScheme.background().redf());
            clearValue.color().float32(1, colourScheme.background().greenf());
            clearValue.color().float32(2, colourScheme.background().bluef());
            clearValue.color().float32(3, 1f);

            var rpBegin = VkRenderPassBeginInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO)
                .renderPass(renderPass)
                .framebuffer(framebuffers[currentImageIndex])
                .renderArea(a -> a.extent().set(swapchainWidth, swapchainHeight))
                .clearValueCount(1)
                .pClearValues(clearValue);
            vkCmdBeginRenderPass(commandBuffer, rpBegin, VK_SUBPASS_CONTENTS_INLINE);

            var viewport = VkViewport.calloc(1, stack);
            viewport.y(0).x(0).width(swapchainWidth).height(swapchainHeight).minDepth(0).maxDepth(1);
            vkCmdSetViewport(commandBuffer, 0, viewport);

            var scissor = VkRect2D.calloc(1, stack);
            scissor.extent().set(swapchainWidth, swapchainHeight);
            vkCmdSetScissor(commandBuffer, 0, scissor);

            vkCmdBindPipeline(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, vkShader.pipeline);
            vkCmdBindDescriptorSets(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS,
                vkShader.pipelineLayout, 0, stack.longs(descriptorSet), null);

            pushScreenWidth = context.scaledWidth();
            pushScreenHeight = context.scaledHeight();
            pushRenderType = 0;
            pushTextureNumber = 0;
        }
    }

    @Override
    public void endFrame(int fbWidth, int fbHeight) {
        try (var stack = MemoryStack.stackPush()) {
            vkCmdEndRenderPass(commandBuffer);
            vkCheck(vkEndCommandBuffer(commandBuffer), "Failed to end command buffer");

            var submitInfo = VkSubmitInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
                .waitSemaphoreCount(1)
                .pWaitSemaphores(stack.longs(imageAvailableSemaphore))
                .pWaitDstStageMask(stack.ints(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT))
                .pCommandBuffers(stack.pointers(commandBuffer));
            VkSubmitInfo.ncommandBufferCount(submitInfo.address(), 1);
            VkSubmitInfo.nsignalSemaphoreCount(submitInfo.address(), 1);
            long renderFinishedSemaphore = renderFinishedSemaphores[currentImageIndex];
            submitInfo.pSignalSemaphores(stack.longs(renderFinishedSemaphore));
            vkCheck(vkQueueSubmit(graphicsQueue, submitInfo, inFlightFence), "Failed to submit queue");

            var presentInfo = VkPresentInfoKHR.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_PRESENT_INFO_KHR)
                .pWaitSemaphores(stack.longs(renderFinishedSemaphore))
                .pSwapchains(stack.longs(swapchain))
                .pImageIndices(stack.ints(currentImageIndex));
            VkPresentInfoKHR.nwaitSemaphoreCount(presentInfo.address(), 1);
            presentInfo.swapchainCount(1);
            int result = vkQueuePresentKHR(graphicsQueue, presentInfo);
            if (result == VK_ERROR_OUT_OF_DATE_KHR || result == VK_SUBOPTIMAL_KHR) {
                swapchainOutOfDate = true;
                return;
            }
            if (result != VK_SUCCESS) throw new RuntimeException("Failed to present: " + result);
        }
    }

    private void recreateSwapchain() {
        int[] w = new int[1], h = new int[1];
        glfwGetFramebufferSize(window, w, h);
        if (w[0] == 0 || h[0] == 0) { swapchainOutOfDate = true; return; }

        vkDeviceWaitIdle(device);
        destroySwapchainResources();
        try (var stack = MemoryStack.stackPush()) {
            createSwapchain(stack);
            createImageViews(stack);
            createFramebuffers(stack);
            if (renderFinishedSemaphores == null || renderFinishedSemaphores.length != swapchainImages.length) {
                if (renderFinishedSemaphores != null)
                    for (long sem : renderFinishedSemaphores)
                        if (sem != 0) vkDestroySemaphore(device, sem, null);
                var semInfo = VkSemaphoreCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO);
                var pSem = stack.mallocLong(1);
                renderFinishedSemaphores = new long[swapchainImages.length];
                for (int i = 0; i < swapchainImages.length; i++) {
                    vkCreateSemaphore(device, semInfo, null, pSem);
                    renderFinishedSemaphores[i] = pSem.get(0);
                }
            }
        }
    }

    private void destroySwapchainResources() {
        if (device != null && framebuffers != null) {
            for (var fb : framebuffers) vkDestroyFramebuffer(device, fb, null);
            framebuffers = null;
        }
        if (device != null && swapchainImageViews != null) {
            for (var view : swapchainImageViews) vkDestroyImageView(device, view, null);
            swapchainImageViews = null;
        }
        if (device != null && swapchain != 0) {
            vkDestroySwapchainKHR(device, swapchain, null);
            swapchain = 0;
        }
    }

    @Override public void releaseCurrent() {}
    @Override public void setVsync(boolean enabled) {}
    @Override public void beginOverlay(int alpha) {}
    @Override public void endOverlay() {}
    @Override public void bindTexture(long handle) {}
    @Override public void unbindTexture() {}

    void pushConstants(VkCommandBuffer cmd) {
        try (var stack = MemoryStack.stackPush()) {
            var pc = stack.malloc(16);
            pc.putFloat(0, pushScreenWidth);
            pc.putFloat(4, pushScreenHeight);
            pc.putInt(8, pushRenderType);
            pc.putInt(12, pushTextureNumber);
            vkCmdPushConstants(cmd, vkShader.pipelineLayout,
                VK_SHADER_STAGE_VERTEX_BIT | VK_SHADER_STAGE_FRAGMENT_BIT, 0, pc);
        }
    }

    // ===== Texture upload =====

    @Override
    public void uploadTexture(ByteBuffer pixels, int width, int height, int slot) {
        createTextureImage(pixels, width, height, slot, VK_FORMAT_R8G8B8A8_UNORM);
    }

    @Override
    public void uploadFontTexture(ByteBuffer alphaBitmap, int width, int height, int slot) {
        createTextureImage(alphaBitmap, width, height, slot, VK_FORMAT_R8_UNORM);
    }

    private void createTextureImage(ByteBuffer pixels, int width, int height, int slot, int format) {
        int pixelSize = format == VK_FORMAT_R8_UNORM ? 1 : 4;
        long imageSize = (long) width * height * pixelSize;
        boolean isPlaceholder = (slot < 0);

        try (var stack = MemoryStack.stackPush()) {
            var stagingInfo = VkBufferCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
                .size(imageSize)
                .usage(VK_BUFFER_USAGE_TRANSFER_SRC_BIT)
                .sharingMode(VK_SHARING_MODE_EXCLUSIVE);
            var pBuffer = stack.mallocLong(1);
            vkCreateBuffer(device, stagingInfo, null, pBuffer);
            long stagingBuffer = pBuffer.get(0);

            var memReqs = VkMemoryRequirements.calloc(stack);
            vkGetBufferMemoryRequirements(device, stagingBuffer, memReqs);
            int memType = findMemoryType(memReqs.memoryTypeBits(),
                VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
            var allocInfo = VkMemoryAllocateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                .allocationSize(memReqs.size())
                .memoryTypeIndex(memType);
            var pMem = stack.mallocLong(1);
            vkAllocateMemory(device, allocInfo, null, pMem);
            long stagingMemory = pMem.get(0);
            vkBindBufferMemory(device, stagingBuffer, stagingMemory, 0);

            var ppData = stack.mallocPointer(1);
            vkMapMemory(device, stagingMemory, 0, imageSize, 0, ppData);
            MemoryUtil.memCopy(MemoryUtil.memAddress(pixels), ppData.get(0), imageSize);
            vkUnmapMemory(device, stagingMemory);

            var imageInfo = VkImageCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO)
                .imageType(VK_IMAGE_TYPE_2D)
                .format(format)
                .extent(e -> e.set(width, height, 1))
                .mipLevels(1).arrayLayers(1)
                .samples(VK_SAMPLE_COUNT_1_BIT)
                .tiling(VK_IMAGE_TILING_OPTIMAL)
                .usage(VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_SAMPLED_BIT)
                .sharingMode(VK_SHARING_MODE_EXCLUSIVE)
                .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED);
            vkCheck(vkCreateImage(device, imageInfo, null, pBuffer), "Failed to create texture image");
            long image = pBuffer.get(0);

            var imgMemReqs = VkMemoryRequirements.calloc(stack);
            vkGetImageMemoryRequirements(device, image, imgMemReqs);
            int imgMemType = findMemoryType(imgMemReqs.memoryTypeBits(), VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);
            var imgAllocInfo = VkMemoryAllocateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                .allocationSize(imgMemReqs.size())
                .memoryTypeIndex(imgMemType);
            vkAllocateMemory(device, imgAllocInfo, null, pMem);
            long memory = pMem.get(0);
            vkBindImageMemory(device, image, memory, 0);

            var cmd = beginSingleTimeCommands();
            transitionImageLayout(cmd, image, VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL);
            var region = VkBufferImageCopy.calloc(1, stack)
                .bufferOffset(0).bufferRowLength(0).bufferImageHeight(0)
                .imageSubresource(s -> s.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT).mipLevel(0).baseArrayLayer(0).layerCount(1))
                .imageOffset(o -> o.set(0, 0, 0))
                .imageExtent(e -> e.set(width, height, 1));
            vkCmdCopyBufferToImage(cmd, stagingBuffer, image, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, region);
            transitionImageLayout(cmd, image, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);
            endSingleTimeCommands(cmd);

            vkDestroyBuffer(device, stagingBuffer, null);
            vkFreeMemory(device, stagingMemory, null);

            var viewInfo = VkImageViewCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO)
                .image(image).viewType(VK_IMAGE_VIEW_TYPE_2D).format(format)
                .components(c -> {})
                .subresourceRange(r -> r.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT).baseMipLevel(0).levelCount(1).baseArrayLayer(0).layerCount(1));
            vkCheck(vkCreateImageView(device, viewInfo, null, pBuffer), "Failed to create texture image view");
            long view = pBuffer.get(0);

            if (isPlaceholder) {
                placeholderImage = image; placeholderMemory = memory; placeholderView = view;
            } else {
                textureImages[slot] = image; textureMemory[slot] = memory; textureViews[slot] = view;
            }
            updateDescriptorSetAll();
        }
    }

    private void transitionImageLayout(VkCommandBuffer cmd, long image, int oldLayout, int newLayout) {
        try (var stack = MemoryStack.stackPush()) {
            var barrier = VkImageMemoryBarrier.calloc(1, stack)
                .sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER)
                .oldLayout(oldLayout).newLayout(newLayout)
                .srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED).dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                .image(image)
                .subresourceRange(r -> r.aspectMask(VK_IMAGE_ASPECT_COLOR_BIT).baseMipLevel(0).levelCount(1).baseArrayLayer(0).layerCount(1));

            int srcStage, dstStage;
            if (oldLayout == VK_IMAGE_LAYOUT_UNDEFINED && newLayout == VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL) {
                barrier.srcAccessMask(0); barrier.dstAccessMask(VK_ACCESS_TRANSFER_WRITE_BIT);
                srcStage = VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT; dstStage = VK_PIPELINE_STAGE_TRANSFER_BIT;
            } else {
                barrier.srcAccessMask(VK_ACCESS_TRANSFER_WRITE_BIT); barrier.dstAccessMask(VK_ACCESS_SHADER_READ_BIT);
                srcStage = VK_PIPELINE_STAGE_TRANSFER_BIT; dstStage = VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT;
            }
            vkCmdPipelineBarrier(cmd, srcStage, dstStage, 0, null, null, barrier);
        }
    }

    private VkCommandBuffer beginSingleTimeCommands() {
        try (var stack = MemoryStack.stackPush()) {
            var info = VkCommandBufferAllocateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
                .commandPool(commandPool).level(VK_COMMAND_BUFFER_LEVEL_PRIMARY).commandBufferCount(1);
            var pCmd = stack.mallocPointer(1);
            vkAllocateCommandBuffers(device, info, pCmd);
            var cmd = new VkCommandBuffer(pCmd.get(0), device);
            var beginInfo = VkCommandBufferBeginInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO)
                .flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
            vkBeginCommandBuffer(cmd, beginInfo);
            return cmd;
        }
    }

    private void endSingleTimeCommands(VkCommandBuffer cmd) {
        vkEndCommandBuffer(cmd);
        try (var stack = MemoryStack.stackPush()) {
            var submitInfo = VkSubmitInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
                .pCommandBuffers(stack.pointers(cmd));
            VkSubmitInfo.ncommandBufferCount(submitInfo.address(), 1);
            vkQueueSubmit(graphicsQueue, submitInfo, VK_NULL_HANDLE);
            vkQueueWaitIdle(graphicsQueue);
        }
        vkFreeCommandBuffers(device, commandPool, cmd);
    }

    int findMemoryType(int typeFilter, int properties) {
        try (var stack = MemoryStack.stackPush()) {
            var memProps = VkPhysicalDeviceMemoryProperties.calloc(stack);
            vkGetPhysicalDeviceMemoryProperties(physicalDevice, memProps);
            for (int i = 0; i < memProps.memoryTypeCount(); i++) {
                if ((typeFilter & (1 << i)) != 0 &&
                    (memProps.memoryTypes(i).propertyFlags() & properties) == properties)
                    return i;
            }
            throw new RuntimeException("Failed to find suitable memory type");
        }
    }

    static void vkCheck(int err, String msg) {
        if (err != VK_SUCCESS) throw new RuntimeException(msg + ": VkResult " + err);
    }

    @Override public BaseShader createShader() { return vkShader; }

    @Override
    public BaseFramebuffer createFramebuffer(int width, int height, int scale, ColourScheme colours) {
        return new BaseFramebuffer() {
            public void activate() {} public void deactivate() {} public void draw(int w, int h) {}
            public long getTextureHandle() { return 0; } public void close() {}
        };
    }

    @Override public VertexDataBuilder createBufferBuilder() {
        var builder = new VulkanVertexBuilder(this);
        vertexBuilders.add(builder);
        return builder;
    }

    void unregisterBuilder(VulkanVertexBuilder builder) { vertexBuilders.remove(builder); }
    @Override public void prepareHandoff(long window) { close(); }
    @Override public String getVersion() { return version; }
    @Override public long getFramebufferTextureHandle() { return 0; }

    @Override
    public void close() {
        if (closed) return;
        closed = true;

        if (device != null) vkDeviceWaitIdle(device);

        for (int i = 0; i < MAX_TEXTURES; i++) {
            if (textureViews[i] != 0 && device != null) vkDestroyImageView(device, textureViews[i], null);
            if (textureImages[i] != 0 && device != null) vkDestroyImage(device, textureImages[i], null);
            if (textureMemory[i] != 0 && device != null) vkFreeMemory(device, textureMemory[i], null);
        }
        if (placeholderView != 0 && device != null) vkDestroyImageView(device, placeholderView, null);
        if (placeholderImage != 0 && device != null) vkDestroyImage(device, placeholderImage, null);
        if (placeholderMemory != 0 && device != null) vkFreeMemory(device, placeholderMemory, null);

        if (indexBuffer != 0 && device != null) vkDestroyBuffer(device, indexBuffer, null);
        if (indexBufferMemory != 0 && device != null) vkFreeMemory(device, indexBufferMemory, null);
        if (sampler != 0 && device != null) vkDestroySampler(device, sampler, null);
        if (descriptorPool != 0 && device != null) vkDestroyDescriptorPool(device, descriptorPool, null);

        if (vkShader != null) { vkShader.close(); vkShader = null; }

        destroySwapchainResources();

        if (imageAvailableSemaphore != 0 && device != null) vkDestroySemaphore(device, imageAvailableSemaphore, null);
        if (renderFinishedSemaphores != null && device != null)
            for (long sem : renderFinishedSemaphores)
                if (sem != 0) vkDestroySemaphore(device, sem, null);
        if (inFlightFence != 0 && device != null) vkDestroyFence(device, inFlightFence, null);
        if (commandPool != 0 && device != null) vkDestroyCommandPool(device, commandPool, null);
        if (device != null) vkDestroyDevice(device, null);
        if (surface != 0 && instance != null) vkDestroySurfaceKHR(instance, surface, null);
        if (debugMessenger != 0 && instance != null)
            EXTDebugUtils.vkDestroyDebugUtilsMessengerEXT(instance, debugMessenger, null);
        if (instance != null) vkDestroyInstance(instance, null);
        if (debugCallback != null) debugCallback.free();
    }
}
