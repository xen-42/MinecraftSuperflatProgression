package xen42.superflatprogression;

import java.util.List;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;

public class SuperflatProgressionVillagers {
    public static void initialize() {
    	TradeOfferHelper.registerVillagerOffers(VillagerProfession.ARMORER, 2, factories -> {
    	    factories.add(new RandomTrimTradeFactory(12, 1, 12, 5, 0.2F));
    	});

    	// Ordinary
		TradeOfferHelper.registerWanderingTraderOffers(1, factories -> {
			factories.add(new TradeOffers.SellItemFactory(Items.SNIFFER_EGG, 10, 1, 12, 1));
			factories.add(new TradeOffers.SellItemFactory(Items.TADPOLE_BUCKET, 3, 1, 12, 1));
			factories.add(new TradeOffers.SellItemFactory(Items.AXOLOTL_BUCKET, 3, 1, 12, 1));
			factories.add(new TradeOffers.SellItemFactory(Items.SEA_LANTERN, 3, 1, 12, 1));
			factories.add(new TradeOffers.SellItemFactory(Items.PRISMARINE_BRICKS, 3, 1, 12, 1));
			factories.add(new TradeOffers.SellItemFactory(Items.AMETHYST_SHARD, 3, 1, 12, 1));
			factories.add(new TradeOffers.SellItemFactory(Items.COBWEB, 3, 1, 12, 1));

			if (SuperflatProgression.isModLoaded(SuperflatProgression.PEACEFUL_PROGRESSION)) {
				var effigyAltar = Registries.ITEM.get(Identifier.of(SuperflatProgression.PEACEFUL_PROGRESSION, "effigy_altar"));
				factories.add(new TradeOffers.SellItemFactory(effigyAltar, 20, 1, 1, 1));
			}
		});

		// Special
		TradeOfferHelper.registerWanderingTraderOffers(2, factories -> {
			factories.add(new TradeOffers.SellItemFactory(Items.MYCELIUM, 3, 3, 6, 1));
		});
    }

	public static final List<Item> ARMORER_TRIMS = List.of(
	    Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE,
	    Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE,
	    Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE,
	    Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE,
	    Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE,
	    Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE,
	    Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE,
	    Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE,
	    Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE,
	    Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE,
	    Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE,
	    Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE,
	    Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE,
	    Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE,
	    Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE,
	    Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE
	);

    public static class RandomTrimTradeFactory implements TradeOffers.Factory {
        private final int price;
        private final int count;
        private final int maxUses;
        private final int experience;
        private final float multiplier;

		public RandomTrimTradeFactory(int price, int count) {
			this(price, count, 1);
		}

		public RandomTrimTradeFactory(int price, int count, int experience) {
			this(price, count, 12, experience);
		}

		public RandomTrimTradeFactory(int price, int count, int maxUses, int experience) {
			this(price, count, maxUses, experience, 0.05F);
		}

        public RandomTrimTradeFactory(int price, int count, int maxUses, int experience, float multiplier) {
            this.price = price;
            this.count = count;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = multiplier;
        }

        @Override
        public TradeOffer create(Entity entity, Random random) {
        	int index = random.nextInt(ARMORER_TRIMS.size());
            Item trim = ARMORER_TRIMS.get(index);
            ItemStack soldStack = new ItemStack(trim, this.count);

			return new TradeOffer(
				new ItemStack(Items.EMERALD, this.price),
				soldStack,
                this.maxUses,
                this.experience,
                this.multiplier
			);
        }
    }
}
