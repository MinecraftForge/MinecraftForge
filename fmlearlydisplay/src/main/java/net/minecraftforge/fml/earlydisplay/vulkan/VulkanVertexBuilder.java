/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.earlydisplay.vulkan;

import net.minecraftforge.fml.earlydisplay.VertexDataBuilder;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VkCommandBuffer;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.*;

/**
 * Vulkan implementation of {@link VertexDataBuilder}.
 * <p>
 * Uses a persistently mapped host-visible vertex buffer for zero-copy vertex upload.
 * The shared index buffer for quad rendering is owned by {@link VulkanRenderBackend}.
 * <p>
 * Matching the GL {@code SimpleBufferBuilder} draw path: begin fills CPU buffer,
 * draw copies to GPU-mapped buffer, pushes per-element uniforms, binds vertex/index
 * buffers, and issues the draw call.
 */
public class VulkanVertexBuilder extends VertexDataBuilder {
    private static final int VERTEX_BUFFER_SIZE = 65536;

    private final VulkanRenderBackend backend;
    private final long vertexBuffer;
    private final long vertexBufferMemory;
    private final long mappedMemory;

    /** Bump-allocation offset within the vertex buffer for the current frame. */
    private int writeOffset;

    /** Resets the per-frame bump allocator. Must be called at the start of each frame. */
    public void resetFrame() {
        writeOffset = 0;
    }

    public VulkanVertexBuilder(VulkanRenderBackend backend) {
        super(1);
        this.backend = backend;

        try (var stack = MemoryStack.stackPush()) {
            var bufferInfo = org.lwjgl.vulkan.VkBufferCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
                .size(VERTEX_BUFFER_SIZE)
                .usage(VK_BUFFER_USAGE_VERTEX_BUFFER_BIT)
                .sharingMode(VK_SHARING_MODE_EXCLUSIVE);

            var pBuffer = stack.mallocLong(1);
            vkCreateBuffer(backend.device, bufferInfo, null, pBuffer);
            this.vertexBuffer = pBuffer.get(0);

            var memReqs = org.lwjgl.vulkan.VkMemoryRequirements.calloc(stack);
            vkGetBufferMemoryRequirements(backend.device, vertexBuffer, memReqs);

            int memoryType = backend.findMemoryType(memReqs.memoryTypeBits(),
                VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);

            var allocInfo = org.lwjgl.vulkan.VkMemoryAllocateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO)
                .allocationSize(memReqs.size())
                .memoryTypeIndex(memoryType);

            var pMemory = stack.mallocLong(1);
            vkAllocateMemory(backend.device, allocInfo, null, pMemory);
            this.vertexBufferMemory = pMemory.get(0);

            vkBindBufferMemory(backend.device, vertexBuffer, vertexBufferMemory, 0);

            var ppData = stack.mallocPointer(1);
            vkMapMemory(backend.device, vertexBufferMemory, 0, VERTEX_BUFFER_SIZE, 0, ppData);
            this.mappedMemory = ppData.get(0);
        }
    }

    @Override
    public void draw() {
        if (!building) throw new IllegalStateException("Not building.");
        if (vertices == 0) { building = false; return; }

        VkCommandBuffer cmd = backend.commandBuffer;

        try (var stack = MemoryStack.stackPush()) {
            // Align the write offset to 16 bytes so each draw's vertex data is contiguous
            // and does not straddle a boundary that could confuse the driver.
            int offset = (writeOffset + 15) & ~15;
            if (offset + index > VERTEX_BUFFER_SIZE) {
                // Not enough room left this frame; wrap to start. This may overwrite
                // earlier data, but prevents an out-of-bounds copy.
                offset = 0;
            }

            MemoryUtil.memCopy(bufferAddr, mappedMemory + offset, index);
            writeOffset = offset + index;

            backend.pushConstants(cmd);

            LongBuffer pBuffers = stack.mallocLong(1);
            pBuffers.put(0, vertexBuffer);
            LongBuffer pOffsets = stack.mallocLong(1);
            pOffsets.put(0, offset);
            vkCmdBindVertexBuffers(cmd, 0, pBuffers, pOffsets);

            if (mode == Mode.QUADS) {
                vkCmdBindIndexBuffer(cmd, backend.indexBuffer, 0, VK_INDEX_TYPE_UINT32);
                int indices = vertices + vertices / 2;
                vkCmdDrawIndexed(cmd, indices, 1, 0, 0, 0);
            } else {
                vkCmdDraw(cmd, vertices, 1, 0, 0);
            }
        } finally {
            building = false;
            vertices = 0;
            index = 0;
        }
    }

    @Override
    public void close() {
        super.close();
        backend.unregisterBuilder(this);
        vkUnmapMemory(backend.device, vertexBufferMemory);
        vkDestroyBuffer(backend.device, vertexBuffer, null);
        vkFreeMemory(backend.device, vertexBufferMemory, null);
    }
}
