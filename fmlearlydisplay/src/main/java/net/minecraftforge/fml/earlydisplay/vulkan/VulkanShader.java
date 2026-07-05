/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.earlydisplay.vulkan;

import net.minecraftforge.fml.earlydisplay.BaseShader;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.vulkan.VK10.*;

/**
 * Vulkan shader implementation for the early loading screen.
 * <p>
 * Loads pre-compiled SPIR-V shader bytecode from classpath resources,
 * creates shader modules, descriptor set layout for 16 combined image samplers,
 * push constant range for per-draw uniforms, and a graphics pipeline matching
 * the GL element shader semantics.
 */
public class VulkanShader extends BaseShader {
    private final VulkanRenderBackend backend;
    private final VkDevice device;
    private final long renderPass;

    long pipeline;
    long pipelineLayout;
    long descriptorSetLayout;
    private long vertModule;
    private long fragModule;

    public VulkanShader(VulkanRenderBackend backend, VkDevice device, long renderPass) {
        this.backend = backend;
        this.device = device;
        this.renderPass = renderPass;
    }

    @Override
    public void init() {
        try (var stack = MemoryStack.stackPush()) {
            vertModule = createShaderModule(loadSpirv("shaders/element.vert.spv"));
            fragModule = createShaderModule(loadSpirv("shaders/element.frag.spv"));

            var binding = VkDescriptorSetLayoutBinding.calloc(1, stack)
                .binding(0)
                .descriptorType(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
                .descriptorCount(16)
                .stageFlags(VK_SHADER_STAGE_FRAGMENT_BIT);
            var dsLayoutInfo = VkDescriptorSetLayoutCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO)
                .pBindings(binding);
            var pDSLayout = stack.mallocLong(1);
            vkCreateDescriptorSetLayout(device, dsLayoutInfo, null, pDSLayout);
            descriptorSetLayout = pDSLayout.get(0);

            var pushRange = VkPushConstantRange.calloc(1, stack)
                .stageFlags(VK_SHADER_STAGE_VERTEX_BIT | VK_SHADER_STAGE_FRAGMENT_BIT)
                .offset(0)
                .size(16);
            var layoutInfo = VkPipelineLayoutCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO)
                .pSetLayouts(stack.longs(descriptorSetLayout))
                .pPushConstantRanges(pushRange);
            var pLayout = stack.mallocLong(1);
            vkCreatePipelineLayout(device, layoutInfo, null, pLayout);
            pipelineLayout = pLayout.get(0);

            createPipeline();
        }
    }

    private void createPipeline() {
        try (var stack = MemoryStack.stackPush()) {
            var vertStage = VkPipelineShaderStageCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO)
                .stage(VK_SHADER_STAGE_VERTEX_BIT)
                .module(vertModule)
                .pName(stack.UTF8("main"));
            var fragStage = VkPipelineShaderStageCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO)
                .stage(VK_SHADER_STAGE_FRAGMENT_BIT)
                .module(fragModule)
                .pName(stack.UTF8("main"));
            var stages = VkPipelineShaderStageCreateInfo.calloc(2, stack);
            stages.put(0, vertStage);
            stages.put(1, fragStage);

            // Vertex layout: pos(2f, 0), tex(2f, 8), colour(4ub_unorm, 16) → stride 20
            var bindingDesc = VkVertexInputBindingDescription.calloc(1, stack)
                .binding(0).stride(20).inputRate(VK_VERTEX_INPUT_RATE_VERTEX);
            var attrDescs = VkVertexInputAttributeDescription.calloc(3, stack);
            attrDescs.get(0).location(0).binding(0).format(VK_FORMAT_R32G32_SFLOAT).offset(0);
            attrDescs.get(1).location(1).binding(0).format(VK_FORMAT_R32G32_SFLOAT).offset(8);
            attrDescs.get(2).location(2).binding(0).format(VK_FORMAT_R8G8B8A8_UNORM).offset(16);
            var vertexInput = VkPipelineVertexInputStateCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO)
                .pVertexBindingDescriptions(bindingDesc)
                .pVertexAttributeDescriptions(attrDescs);

            var inputAssembly = VkPipelineInputAssemblyStateCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO)
                .topology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST)
                .primitiveRestartEnable(false);

            var viewportState = VkPipelineViewportStateCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO)
                .viewportCount(1).scissorCount(1);

            var rasterizer = VkPipelineRasterizationStateCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO)
                .depthClampEnable(false).rasterizerDiscardEnable(false)
                .polygonMode(VK_POLYGON_MODE_FILL)
                .cullMode(VK_CULL_MODE_NONE)
                .frontFace(VK_FRONT_FACE_COUNTER_CLOCKWISE)
                .lineWidth(1f);

            var multisampling = VkPipelineMultisampleStateCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO)
                .rasterizationSamples(VK_SAMPLE_COUNT_1_BIT);

            var blendAttachment = VkPipelineColorBlendAttachmentState.calloc(1, stack)
                .blendEnable(true)
                .srcColorBlendFactor(VK_BLEND_FACTOR_SRC_ALPHA)
                .dstColorBlendFactor(VK_BLEND_FACTOR_ONE_MINUS_SRC_ALPHA)
                .colorBlendOp(VK_BLEND_OP_ADD)
                .srcAlphaBlendFactor(VK_BLEND_FACTOR_ONE)
                .dstAlphaBlendFactor(VK_BLEND_FACTOR_ZERO)
                .alphaBlendOp(VK_BLEND_OP_ADD)
                .colorWriteMask(VK_COLOR_COMPONENT_R_BIT | VK_COLOR_COMPONENT_G_BIT
                    | VK_COLOR_COMPONENT_B_BIT | VK_COLOR_COMPONENT_A_BIT);
            var colorBlending = VkPipelineColorBlendStateCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO)
                .logicOpEnable(false)
                .pAttachments(blendAttachment);

            var dynamicStates = stack.ints(VK_DYNAMIC_STATE_VIEWPORT, VK_DYNAMIC_STATE_SCISSOR);
            var dynamicState = VkPipelineDynamicStateCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_PIPELINE_DYNAMIC_STATE_CREATE_INFO)
                .pDynamicStates(dynamicStates);

            var pipelineInfo = VkGraphicsPipelineCreateInfo.calloc(1, stack)
                .sType(VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO)
                .pStages(stages)
                .pVertexInputState(vertexInput)
                .pInputAssemblyState(inputAssembly)
                .pViewportState(viewportState)
                .pRasterizationState(rasterizer)
                .pMultisampleState(multisampling)
                .pColorBlendState(colorBlending)
                .pDynamicState(dynamicState)
                .layout(pipelineLayout)
                .renderPass(renderPass)
                .subpass(0);

            var pPipeline = stack.mallocLong(1);
            int err = vkCreateGraphicsPipelines(device, VK_NULL_HANDLE, pipelineInfo, null, pPipeline);
            if (err != VK_SUCCESS) throw new RuntimeException("Failed to create graphics pipeline: " + err);
            pipeline = pPipeline.get(0);
        }
    }

    private long createShaderModule(ByteBuffer spirv) {
        try (var stack = MemoryStack.stackPush()) {
            var info = VkShaderModuleCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO)
                .pCode(spirv);
            var pModule = stack.mallocLong(1);
            vkCreateShaderModule(device, info, null, pModule);
            return pModule.get(0);
        }
    }

    private ByteBuffer loadSpirv(String path) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) throw new RuntimeException("SPIR-V shader not found: " + path);
            byte[] bytes = is.readAllBytes();
            return ByteBuffer.allocateDirect(bytes.length).put(bytes).flip();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load SPIR-V: " + path, e);
        }
    }

    @Override public void activate() {}
    @Override public void clear() {}

    @Override public void updateTextureUniform(int n) {
        backend.pushTextureNumber = n;
    }

    @Override public void updateRenderTypeUniform(RenderType t) {
        backend.pushRenderType = t.ordinal();
    }

    @Override public void updateScreenSizeUniform(int w, int h) {
        backend.pushScreenWidth = w;
        backend.pushScreenHeight = h;
    }

    @Override
    public void close() {
        vkDestroyPipeline(device, pipeline, null);
        vkDestroyPipelineLayout(device, pipelineLayout, null);
        vkDestroyDescriptorSetLayout(device, descriptorSetLayout, null);
        vkDestroyShaderModule(device, fragModule, null);
        vkDestroyShaderModule(device, vertModule, null);
    }
}
