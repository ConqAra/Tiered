package Andrew6rant.tiered.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InGameHud.class)
public interface TooltipFadeAccessor {
    @Accessor("heldItemTooltipFade")
    public void setHeldItemTooltipFade(int heldItemTooltipFade);
}
