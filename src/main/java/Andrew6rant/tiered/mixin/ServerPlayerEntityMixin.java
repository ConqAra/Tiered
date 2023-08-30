package Andrew6rant.tiered.mixin;

import com.mojang.authlib.GameProfile;
import Andrew6rant.tiered.Tiered;
import Andrew6rant.tiered.api.AttributeTemplate;
import Andrew6rant.tiered.api.ModifierUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    private DefaultedList<ItemStack> mainCopy = null;

    private ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        // if main copy is null, set it to player inventory and check each stack
        if(mainCopy == null) {
            mainCopy = copyDefaultedList(((InventoryAccessor)this).getInventory().main);
        }

        // if main copy =/= inventory, run check and set mainCopy to inventory
        if (!((InventoryAccessor)this).getInventory().main.equals(mainCopy)) {
            mainCopy = copyDefaultedList(((InventoryAccessor)this).getInventory().main);
        }
        // this will update the player's health when switching items
        if (this.getHealth() != this.getMaxHealth()) {
            //System.out.println("Health: " + this.getHealth());
            this.setHealth(this.getHealth());
        }
    }

    @Unique
    private DefaultedList<ItemStack> copyDefaultedList(DefaultedList<ItemStack> list) {
        DefaultedList<ItemStack> newList = DefaultedList.ofSize(36, ItemStack.EMPTY);

        for (int i = 0; i < list.size(); i++) {
            newList.set(i, list.get(i));
        }

        return newList;
    }
}
