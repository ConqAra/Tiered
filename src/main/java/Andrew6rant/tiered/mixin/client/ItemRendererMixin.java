package Andrew6rant.tiered.mixin.client;

import Andrew6rant.tiered.Tiered;
import Andrew6rant.tiered.api.CustomEntityAttributes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemRenderer.class, priority = 1010)
public class ItemRendererMixin
{
    @Inject(
            method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V",
            at = @At(
                    value = "HEAD"
            )
    )
    private void onRenderItemPreRender(@Nullable LivingEntity entity, ItemStack item, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, @Nullable World world, int light, int overlay, int seed, CallbackInfo info) {
        //EntityAttribute attribute = (CustomEntityAttributes.SIZE);
        NbtCompound size1 = new NbtCompound(); size1.putString("Tier","tiered:tiny");
        NbtCompound size2 = new NbtCompound(); size2.putString("Tier","tiered:small");
        NbtCompound size3 = new NbtCompound(); size3.putString("Tier","tiered:large");
        NbtCompound size4 = new NbtCompound(); size4.putString("Tier","tiered:massive");
        NbtCompound size5 = new NbtCompound(); size4.putString("Tier","tiered:gargantuan");
        matrices.push();
        float scale = 1f;
            if (size1.equals(item.getSubNbt(Tiered.NBT_SUBTAG_KEY))) {
                scale = .5f;
            } else if (size2.equals(item.getSubNbt(Tiered.NBT_SUBTAG_KEY))) {
                scale = .75f;
            } else if (size3.equals(item.getSubNbt(Tiered.NBT_SUBTAG_KEY))) {
                scale = 1.25f;
            } else if (size4.equals(item.getSubNbt(Tiered.NBT_SUBTAG_KEY))) {
                scale = 1.5f;
            } else if (size5.equals(item.getSubNbt(Tiered.NBT_SUBTAG_KEY))) {
                scale = 1.75f;
            }
            matrices.scale(scale, scale, scale);
        matrices.push();
    }

    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V", at = @At(value = "RETURN"))
    private void onRenderItemPostRender(@Nullable LivingEntity entity, ItemStack item, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, @Nullable World world, int light, int overlay, int seed, CallbackInfo info)
    {
        matrices.pop();
        matrices.pop();
    }
}