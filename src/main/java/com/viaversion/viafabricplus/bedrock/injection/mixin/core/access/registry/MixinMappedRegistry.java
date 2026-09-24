/*
 * This file is part of ViaFabricPlus Bedrock - https://github.com/florianreuth/viafabricplus-bedrock
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.viaversion.viafabricplus.bedrock.injection.mixin.core.access.registry;

import com.viaversion.viafabricplus.bedrock.injection.access.registry.IMappedRegistry;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import java.util.IdentityHashMap;
import java.util.Map;

@Mixin(MappedRegistry.class)
public class MixinMappedRegistry<T> implements IMappedRegistry {
    @Shadow
    @Final
    private ObjectList<Holder.Reference<T>> byId;

    @Shadow
    @Final
    private Reference2IntMap<T> toId;

    @Shadow
    @Final
    private Map<Identifier, Holder.Reference<T>> byLocation;

    @Shadow
    @Final
    private Map<ResourceKey<T>, Holder.Reference<T>> byKey;

    @Shadow
    @Final
    private Map<T, Holder.Reference<T>> byValue;

    @Shadow
    @Final
    private Map<ResourceKey<T>, RegistrationInfo> registrationInfos;

    @Shadow
    private @Nullable Map<T, Holder.Reference<T>> unregisteredIntrusiveHolders;

    @Shadow
    private boolean frozen;

    @Override
    public void viaFabricPlus$unfreeze() {
        this.unregisteredIntrusiveHolders = new IdentityHashMap<>();
        this.frozen = false;
    }

    @Override
    public void viaFabricPlus$refreeze() {
        this.unregisteredIntrusiveHolders = null;
        this.frozen = true;
    }

    @Override
    public void viaFabricPlus$unregister(final ResourceKey key) {
        Holder.Reference<@NotNull T> reference = this.byKey.remove(key);
        this.byLocation.remove(key.identifier());
        this.byValue.remove(reference.value());
        this.byId.remove(reference);
        this.toId.remove(reference.value());
        this.registrationInfos.remove(key);
    }
}
