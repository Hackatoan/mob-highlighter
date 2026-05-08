package com.hackatoa.mobhighlighter.client;

import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class HighlightBoxRenderer {

    // Filled box render pipeline — depth always passes (visible through walls), no depth write
    private static final RenderPipeline FILLED_PIPELINE = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath("mob_highlighter", "pipeline/highlight_filled"))
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .withCull(false)
            .build();

    private static final RenderType FILLED_BOX = RenderType.create(
            "mob_highlighter_filled_box",
            RenderSetup.builder(FILLED_PIPELINE)
                    .sortOnUpload()
                    .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                    .createRenderSetup()
    );

    // Outline render pipeline — same depth rules, lines mode
    private static final RenderPipeline OUTLINE_PIPELINE = RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath("mob_highlighter", "pipeline/highlight_outline"))
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .withCull(false)
            .build();

    private static final RenderType OUTLINE_BOX = RenderType.create(
            "mob_highlighter_outline_box",
            RenderSetup.builder(OUTLINE_PIPELINE)
                    .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                    .createRenderSetup()
    );

    private HighlightBoxRenderer() {}

    public static void renderHighlights() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        EntityType<?> selected = MobHighlightManager.INSTANCE.getSelectedType();
        if (selected == null) return;

        Vec3 camPos = mc.getEntityRenderDispatcher().camera.position();
        float tickDelta = mc.getDeltaTracker().getGameTimeDeltaPartialTick(true);
        Matrix4f matrix = mc.gameRenderer.getGameRenderState().levelRenderState.cameraRenderState.viewRotationMatrix;

        double rangeSq = (double) MobHighlightManager.RANGE * MobHighlightManager.RANGE;

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!entity.isAlive()) continue;
            if (entity.getType() != selected) continue;
            if (entity.distanceToSqr(mc.player) > rangeSq) continue;

            double ex = Mth.lerp(tickDelta, entity.xOld, entity.getX()) - camPos.x;
            double ey = Mth.lerp(tickDelta, entity.yOld, entity.getY()) - camPos.y;
            double ez = Mth.lerp(tickDelta, entity.zOld, entity.getZ()) - camPos.z;

            float hw = entity.getBbWidth() / 2.0f + 0.05f;
            float h  = entity.getBbHeight() + 0.05f;

            float minX = (float) ex - hw;
            float minY = (float) ey - 0.025f;
            float minZ = (float) ez - hw;
            float maxX = (float) ex + hw;
            float maxY = (float) ey + h;
            float maxZ = (float) ez + hw;

            // Semi-transparent green fill
            drawFilledBox(matrix, minX, minY, minZ, maxX, maxY, maxZ, 0x3300FF44);
            // Bright green outline
            drawOutlineBox(matrix, minX, minY, minZ, maxX, maxY, maxZ, 0xFF00FF44);
        }
    }

    private static void drawFilledBox(Matrix4f m,
                                      float x0, float y0, float z0,
                                      float x1, float y1, float z1,
                                      int color) {
        BufferBuilder buf = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        // Bottom
        buf.addVertex(m, x0, y0, z0).setColor(color);
        buf.addVertex(m, x1, y0, z0).setColor(color);
        buf.addVertex(m, x1, y0, z1).setColor(color);
        buf.addVertex(m, x0, y0, z1).setColor(color);
        // Top
        buf.addVertex(m, x0, y1, z0).setColor(color);
        buf.addVertex(m, x0, y1, z1).setColor(color);
        buf.addVertex(m, x1, y1, z1).setColor(color);
        buf.addVertex(m, x1, y1, z0).setColor(color);
        // Front
        buf.addVertex(m, x0, y0, z0).setColor(color);
        buf.addVertex(m, x0, y1, z0).setColor(color);
        buf.addVertex(m, x1, y1, z0).setColor(color);
        buf.addVertex(m, x1, y0, z0).setColor(color);
        // Back
        buf.addVertex(m, x0, y0, z1).setColor(color);
        buf.addVertex(m, x1, y0, z1).setColor(color);
        buf.addVertex(m, x1, y1, z1).setColor(color);
        buf.addVertex(m, x0, y1, z1).setColor(color);
        // Left
        buf.addVertex(m, x0, y0, z0).setColor(color);
        buf.addVertex(m, x0, y0, z1).setColor(color);
        buf.addVertex(m, x0, y1, z1).setColor(color);
        buf.addVertex(m, x0, y1, z0).setColor(color);
        // Right
        buf.addVertex(m, x1, y0, z0).setColor(color);
        buf.addVertex(m, x1, y1, z0).setColor(color);
        buf.addVertex(m, x1, y1, z1).setColor(color);
        buf.addVertex(m, x1, y0, z1).setColor(color);
        FILLED_BOX.draw(buf.buildOrThrow());
    }

    private static void drawOutlineBox(Matrix4f m,
                                       float x0, float y0, float z0,
                                       float x1, float y1, float z1,
                                       int color) {
        BufferBuilder buf = Tesselator.getInstance().begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH);
        float thickness = 1.5f;
        // Bottom edges
        line(buf, m, x0, y0, z0, x1, y0, z0, color, thickness);
        line(buf, m, x1, y0, z0, x1, y0, z1, color, thickness);
        line(buf, m, x1, y0, z1, x0, y0, z1, color, thickness);
        line(buf, m, x0, y0, z1, x0, y0, z0, color, thickness);
        // Top edges
        line(buf, m, x0, y1, z0, x1, y1, z0, color, thickness);
        line(buf, m, x1, y1, z0, x1, y1, z1, color, thickness);
        line(buf, m, x1, y1, z1, x0, y1, z1, color, thickness);
        line(buf, m, x0, y1, z1, x0, y1, z0, color, thickness);
        // Vertical edges
        line(buf, m, x0, y0, z0, x0, y1, z0, color, thickness);
        line(buf, m, x1, y0, z0, x1, y1, z0, color, thickness);
        line(buf, m, x1, y0, z1, x1, y1, z1, color, thickness);
        line(buf, m, x0, y0, z1, x0, y1, z1, color, thickness);
        OUTLINE_BOX.draw(buf.buildOrThrow());
    }

    private static void line(BufferBuilder buf, Matrix4f m,
                              float x1, float y1, float z1,
                              float x2, float y2, float z2,
                              int color, float thickness) {
        float dx = x2 - x1, dy = y2 - y1, dz = z2 - z1;
        float len = Mth.sqrt(dx * dx + dy * dy + dz * dz);
        if (len == 0) return;
        Vector3f normal = new Vector3f(dx / len, dy / len, dz / len);
        buf.addVertex(m, x1, y1, z1).setColor(color).setNormal(normal.x, normal.y, normal.z).setLineWidth(thickness);
        buf.addVertex(m, x2, y2, z2).setColor(color).setNormal(normal.x, normal.y, normal.z).setLineWidth(thickness);
    }
}
