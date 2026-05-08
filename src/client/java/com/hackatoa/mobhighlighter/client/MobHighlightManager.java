package com.hackatoa.mobhighlighter.client;

import org.jspecify.annotations.Nullable;

import net.minecraft.world.entity.EntityType;

public final class MobHighlightManager {

    public static final MobHighlightManager INSTANCE = new MobHighlightManager();
    public static final int RANGE = 64;

    @Nullable
    private EntityType<?> selectedType = null;

    private MobHighlightManager() {}

    public void toggleType(@Nullable EntityType<?> type) {
        selectedType = (type != null && type == selectedType) ? null : type;
    }

    public void clearSelection() {
        selectedType = null;
    }

    public boolean isSelectedType(EntityType<?> type) {
        return selectedType != null && selectedType == type;
    }

    @Nullable
    public EntityType<?> getSelectedType() {
        return selectedType;
    }
}
