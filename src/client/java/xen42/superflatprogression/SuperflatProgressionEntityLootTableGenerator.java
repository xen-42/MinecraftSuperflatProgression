package xen42.superflatprogression;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.LootingEnchantLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.Identifier;

public class SuperflatProgressionEntityLootTableGenerator extends SimpleFabricLootTableProvider {
    private final CompletableFuture<WrapperLookup> registryLookup;

    protected SuperflatProgressionEntityLootTableGenerator(FabricDataOutput dataOutput,
            CompletableFuture<WrapperLookup> registryLookup) {
        super(dataOutput, LootContextTypes.ENTITY);
        this.registryLookup = registryLookup;
    }

    @Override
    public String getName() {
        return "SuperflatProgressionEntityLootTableGenerator";
    }

    @Override
    public void accept(BiConsumer<Identifier, Builder> consumer) {
        consumer.accept(
            SuperflatProgression.PIXIE_ENTITY.getLootTableId(), 
            LootTable.builder()
            .pool(
                LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1.0F))
                    .with(
                        ItemEntry.builder(SuperflatProgressionItems.ESSENCE)
                            .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
                            .apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0F, 1.0F)))
                    )
            )
        );
    }
    
    protected final EntityPropertiesLootCondition.Builder createSmeltLootCondition() {
        return EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().flags(EntityFlagsPredicate.Builder.create().onFire(true).build()));
    }
}
