package Andrew6rant.tiered.mixin.client;

import Andrew6rant.tiered.Tiered;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Environment(EnvType.CLIENT)
@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin extends EntityRenderer<ItemEntity> {

    protected ItemEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    public void preRender(ItemEntity itemEntity, float f, float g, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {
        NbtCompound size1 = new NbtCompound(); size1.putString("Tier","tiered:tiny");
        NbtCompound size2 = new NbtCompound(); size2.putString("Tier","tiered:small");
        NbtCompound size3 = new NbtCompound(); size3.putString("Tier","tiered:large");
        NbtCompound size4 = new NbtCompound(); size4.putString("Tier","tiered:massive");
        ItemStack item = itemEntity.getStack();
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
        }
        matrices.scale(scale, scale, scale);
        matrices.push();
    }
    @Inject(at = @At("RETURN"), method = "render", cancellable = true)
    public void postRender(ItemEntity itemEntity, float f, float g, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {
        matrices.pop();
        matrices.pop();
    }

}