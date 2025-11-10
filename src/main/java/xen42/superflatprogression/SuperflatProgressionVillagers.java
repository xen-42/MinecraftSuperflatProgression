package xen42.superflatprogression;

import java.rmi.registry.Registry;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;

public class SuperflatProgressionVillagers {
    public static void initialize() {
		TradeOfferHelper.registerWanderingTraderOffers(1, factories -> {
			factories.add((entity, random) -> new TradeOffer(
				new ItemStack(Items.EMERALD, 10),
				new ItemStack(Items.SNIFFER_EGG, 1), 12, 20, 0.05f));
			factories.add((entity, random) -> new TradeOffer(
				new ItemStack(Items.EMERALD, 3),
				new ItemStack(Items.TADPOLE_BUCKET, 1), 12, 20, 0.05f));
			factories.add((entity, random) -> new TradeOffer(
				new ItemStack(Items.EMERALD, 3),
				new ItemStack(Items.AXOLOTL_BUCKET, 1), 12, 20, 0.05f));
            factories.add((entity, random) -> new TradeOffer(
				new ItemStack(Items.EMERALD, 3),
				new ItemStack(Items.SEA_LANTERN, 1), 12, 20, 0.05f));
            factories.add((entity, random) -> new TradeOffer(
				new ItemStack(Items.EMERALD, 3),
				new ItemStack(Items.PRISMARINE_BRICKS, 1), 12, 20, 0.05f));
			factories.add((entity, random) -> new TradeOffer(
				new ItemStack(Items.EMERALD, 3),
				new ItemStack(Items.AMETHYST_SHARD, 1), 12, 20, 0.05f));
				
			if (FabricLoader.getInstance().isModLoaded("peaceful-items")) {
				factories.add((entity, random) -> new TradeOffer(
					new ItemStack(Items.EMERALD, 20),
					new ItemStack(Registries.ITEM.get(Identifier.of("peaceful-items", "effigy_altar")), 1),
						1, 20, 0.05f));
			}
		});
    }
}
