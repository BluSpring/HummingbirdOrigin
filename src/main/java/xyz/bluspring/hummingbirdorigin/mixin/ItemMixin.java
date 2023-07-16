package xyz.bluspring.hummingbirdorigin.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.bluspring.hummingbirdorigin.HummingbirdOrigin;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Shadow @Final private Holder.Reference<Item> builtInRegistryHolder;
    @Mutable
    @Shadow @Final @Nullable private FoodProperties foodProperties;

    @Shadow @Nullable public abstract FoodProperties getFoodProperties();

    @Unique
    private static final TagKey<Item> SEEDS = TagKey.create(Registries.ITEM, new ResourceLocation("c", "seeds"));

    @Inject(method = "isEdible", at = @At("HEAD"), cancellable = true)
    public void markAsConsumable(CallbackInfoReturnable<Boolean> cir) {
        if (isSeed())
            cir.setReturnValue(true);
    }

    @Unique
    private boolean isSeed() {
        var tag = BuiltInRegistries.ITEM.getTag(SEEDS);
        return tag.map(holders -> holders.contains(this.builtInRegistryHolder)).orElse(false);
    }

    @Inject(method = "getFoodProperties", at = @At("HEAD"))
    public void updateFoodProperties(CallbackInfoReturnable<FoodProperties> cir) {
        if (this.isSeed() && this.foodProperties == null) {
            this.foodProperties = new FoodProperties.Builder()
                    .fast()
                    .nutrition(3)
                    .saturationMod(2f)
                    .build();
        }
    }

    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;isEdible()Z"))
    public boolean disableConsumptionForNonSeedEaters(Item instance, Level level, Player player) {
        if (isSeed() && HummingbirdOrigin.Companion.getFloralConsumption().isActive(player)) {
            this.getFoodProperties();
            return true;
        } else
            return instance.isEdible();
    }
}
