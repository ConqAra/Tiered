package Andrew6rant.tiered;

import Andrew6rant.tiered.api.CustomEntityAttributes;
import Andrew6rant.tiered.data.AttributeDataLoader;
import Andrew6rant.tiered.api.PotentialAttribute;
import Andrew6rant.tiered.mixin.client.ItemStackClientMixin;
import com.anthonyhilyard.iceberg.events.RenderTickEvents;
import com.anthonyhilyard.iceberg.events.RenderTooltipEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TieredClient implements ClientModInitializer {

    // map for storing attributes before logging into a server
    public static final Map<Identifier, PotentialAttribute> CACHED_ATTRIBUTES = new HashMap<>();

    @Override
    public void onInitializeClient() {
        registerAttributeSyncHandler();
        //RenderTooltipEvents.PRE.register(TieredClient::onPreTooltipEvent);
        //RenderTooltipEvents.COLOR.register(TieredClient::onTooltipColorEvent);
        RenderTooltipEvents.POST.register(TieredClient::onPostTooltipEvent);
        //RenderTickEvents.START.register(TieredClient::onRenderTick);
    }

    public static void registerAttributeSyncHandler() {
        ClientPlayNetworking.registerGlobalReceiver(Tiered.ATTRIBUTE_SYNC_PACKET, (client, play, packet, packetSender) -> {
            // save old attributes
            CACHED_ATTRIBUTES.putAll(Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes());
            Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().clear();

            // for each id/attribute pair, load it
            int size = packet.readInt();
            for(int i = 0; i < size; i++) {
                Identifier id = new Identifier(packet.readString());
                PotentialAttribute pa = AttributeDataLoader.GSON.fromJson(packet.readString(), PotentialAttribute.class);
                Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().put(id, pa);
            }
        });
    }
    public static float getSize(LivingEntity entity, float f) {
        EntityAttributeInstance instance = entity.getAttributeInstance(CustomEntityAttributes.SIZE);

        if(instance != null) {
            for (EntityAttributeModifier modifier : instance.getModifiers()) {
                float amount = (float) modifier.getValue();

                if (modifier.getOperation() == EntityAttributeModifier.Operation.ADDITION) {
                    f += amount;
                } else {
                    f *= (amount + 1);
                }
            }
        }

        return f;
    }

    public static void onPostTooltipEvent(ItemStack stack, List<TooltipComponent> components, MatrixStack matrixStack, int x, int y, TextRenderer font, int width, int height, boolean comparison) {
        if(stack.getSubNbt(Tiered.NBT_SUBTAG_KEY) != null) {
            Tooltip.drawBorder(matrixStack, x, y, width, height, stack, components, font, Tiered.testing(stack), comparison); // just testing for now
        }
    }
}
