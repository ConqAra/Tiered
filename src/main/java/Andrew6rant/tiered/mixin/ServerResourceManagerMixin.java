package Andrew6rant.tiered.mixin;

import Andrew6rant.tiered.Tiered;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.server.DataPackContents;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.registry.DynamicRegistryManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DataPackContents.class)
public class ServerResourceManagerMixin {

    @Shadow @Final private ReloadableResourceManagerImpl resourceManager;

    @Inject(at = @At("RETURN"), method = "<init>")
    private void onInit(DynamicRegistryManager registryManager, CommandManager.RegistrationEnvironment commandEnvironment, int functionPermissionLevel, CallbackInfo ci) {
        this.resourceManager.registerReloader(Tiered.ATTRIBUTE_DATA_LOADER);
    }
}
