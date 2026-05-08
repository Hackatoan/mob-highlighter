package com.hackatoa.mobhighlighter.client;

import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.EntityHitResult;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;

public class MobHighlighterClient implements ClientModInitializer {

    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("mob_highlighter", "category")
    );

    public static final KeyMapping SELECT_KEY = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                    "key.mob_highlighter.select",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_H,
                    CATEGORY
            )
    );

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
        LevelRenderEvents.END_MAIN.register(context -> HighlightBoxRenderer.renderHighlights());
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.BOSS_BAR,
                Identifier.fromNamespaceAndPath("mob_highlighter", "tracker_hud"),
                this::renderHud
        );
    }

    private void onTick(Minecraft client) {
        if (client.player == null) return;

        while (SELECT_KEY.consumeClick()) {
            if (client.hitResult instanceof EntityHitResult hit) {
                Entity entity = hit.getEntity();
                EntityType<?> prev = MobHighlightManager.INSTANCE.getSelectedType();
                MobHighlightManager.INSTANCE.toggleType(entity.getType());
                EntityType<?> next = MobHighlightManager.INSTANCE.getSelectedType();

                if (next != null) {
                    client.player.sendSystemMessage(
                            Component.translatable("msg.mob_highlighter.tracking",
                                    Component.translatable(entity.getType().getDescriptionId()))
                    );
                } else {
                    client.player.sendSystemMessage(
                            Component.translatable("msg.mob_highlighter.cleared")
                    );
                }
            } else {
                if (MobHighlightManager.INSTANCE.getSelectedType() != null) {
                    MobHighlightManager.INSTANCE.clearSelection();
                    client.player.sendSystemMessage(
                            Component.translatable("msg.mob_highlighter.cleared")
                    );
                }
            }
        }
    }

    private void renderHud(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        EntityType<?> selected = MobHighlightManager.INSTANCE.getSelectedType();
        if (selected == null) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        // Count nearby entities of selected type
        int count = 0;
        if (mc.level != null) {
            double rangeSq = (double) MobHighlightManager.RANGE * MobHighlightManager.RANGE;
            for (Entity entity : mc.level.entitiesForRendering()) {
                if (entity.isAlive() && entity.getType() == selected
                        && entity.distanceToSqr(mc.player) <= rangeSq) {
                    count++;
                }
            }
        }

        String name = Component.translatable(selected.getDescriptionId()).getString();
        String text = name + " ×" + count + "  (within " + MobHighlightManager.RANGE + "m)";
        int textWidth = mc.font.width(text);
        int x = (graphics.guiWidth() - textWidth) / 2;
        int y = graphics.guiHeight() - 55;

        graphics.fill(x - 4, y - 3, x + textWidth + 4, y + 11, 0x88000000);
        graphics.text(mc.font, text, x, y, CommonColors.WHITE);
    }
}
