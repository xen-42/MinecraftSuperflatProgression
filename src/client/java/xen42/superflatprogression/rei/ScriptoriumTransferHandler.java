package xen42.superflatprogression.rei;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import me.shedaniel.rei.api.client.registry.transfer.simple.SimpleTransferHandler;
import me.shedaniel.rei.api.common.transfer.info.stack.SlotAccessor;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import xen42.superflatprogression.screen.ScrollCraftingScreenHandler;

public class ScriptoriumTransferHandler implements SimpleTransferHandler {

	public ScrollCraftingScreenHandler getMenu(Context context) {
		if (context.getMenu() instanceof ScrollCraftingScreenHandler menu) {
			return menu;
		}
		else {
			return null;
		}
	}

	@Override
	public Iterable<SlotAccessor> getInputSlots(Context context) {
		var menu = getMenu(context);
		List<Slot> slots = menu.getInputSlots();
		List<SlotAccessor> accessors = new ArrayList<SlotAccessor>();
		for (Slot slot : slots) {
			accessors.add(SlotAccessor.fromSlot(slot));
		}
		return accessors;
	}
	
	@Override
	public Iterable<SlotAccessor> getInventorySlots(Context context) {
		ClientPlayerEntity player = context.getMinecraft().player;
		PlayerInventory inventory = player.getInventory();
		return IntStream.range(0, inventory.main.size())
				.mapToObj(index -> SlotAccessor.fromPlayerInventory(player, index))
				.collect(Collectors.toList());
	}
}
