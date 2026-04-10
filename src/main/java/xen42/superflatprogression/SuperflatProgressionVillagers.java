package xen42.superflatprogression;

import java.rmi.registry.Registry;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;

public class SuperflatProgressionVillagers {
    public static void initialize() {
		TradeOfferHelper.registerWanderingTraderOffers(1, factories -> {
			factories.add(new TradeOffers.SellItemFactory(Items.SNIFFER_EGG, 10, 1, 12, 1));
			factories.add(new TradeOffers.SellItemFactory(Items.TADPOLE_BUCKET, 3, 1, 12, 1));
			factories.add(new TradeOffers.SellItemFactory(Items.AXOLOTL_BUCKET, 3, 1, 12, 1));
			factories.add(new TradeOffers.SellItemFactory(Items.SEA_LANTERN, 3, 1, 12, 1));
			factories.add(new TradeOffers.SellItemFactory(Items.PRISMARINE_BRICKS, 3, 1, 12, 1));
			factories.add(new TradeOffers.SellItemFactory(Items.AMETHYST_SHARD, 3, 1, 12, 1));
			factories.add(new TradeOffers.SellItemFactory(Items.COBWEB, 3, 1, 12, 1));
				
			if (FabricLoader.getInstance().isModLoaded("peaceful-items")) {
				var effigyAltar = Registries.ITEM.get(Identifier.of("peaceful-items", "effigy_altar"));
				factories.add(new TradeOffers.SellItemFactory(effigyAltar, 20, 1, 1, 1));
			}
		});
    }
}
