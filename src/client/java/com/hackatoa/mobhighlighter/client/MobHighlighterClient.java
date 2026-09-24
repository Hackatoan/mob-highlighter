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
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.BOSS_BAR,
                Identifier.fromNamespaceAndPath("mob_highlighter", "tracker_hud"),
                this::renderHud
        );
    }

    // Nearby-entity count for the HUD, refreshed once per game tick (20/s) instead of
    // every render frame, since frame rates can run well above tick rate and the HUD
    // text doesn't need sub-tick precision.
    private int cachedNearbyCount = 0;

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

        updateNearbyCount(client);
    }

    private void updateNearbyCount(Minecraft client) {
        EntityType<?> selected = MobHighlightManager.INSTANCE.getSelectedType();
        if (selected == null || client.level == null) {
            cachedNearbyCount = 0;
            return;
        }

        int count = 0;
        double rangeSq = (double) MobHighlightManager.RANGE * MobHighlightManager.RANGE;
        for (Entity entity : client.level.entitiesForRendering()) {
            if (entity.isAlive() && entity.getType() == selected
                    && entity.distanceToSqr(client.player) <= rangeSq) {
                count++;
            }
        }
        cachedNearbyCount = count;
    }

    private void renderHud(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        EntityType<?> selected = MobHighlightManager.INSTANCE.getSelectedType();
        if (selected == null) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        String name = Component.translatable(selected.getDescriptionId()).getString();
        String text = name + " ×" + cachedNearbyCount + "  (within " + MobHighlightManager.RANGE + "m)";
        int textWidth = mc.font.width(text);
        int x = (graphics.guiWidth() - textWidth) / 2;
        int y = graphics.guiHeight() - 55;

        graphics.fill(x - 4, y - 3, x + textWidth + 4, y + 11, 0x88000000);
        graphics.text(mc.font, text, x, y, CommonColors.WHITE);
    }
}
