package info.u_team.enhanced_anvil.data.provider;

import static info.u_team.enhanced_anvil.init.EnhancedAnvilBlocks.CHIPPED_ENHANCED_ANVIL;
import static info.u_team.enhanced_anvil.init.EnhancedAnvilBlocks.DAMAGED_ENHANCED_ANVIL;
import static info.u_team.enhanced_anvil.init.EnhancedAnvilBlocks.ENHANCED_ANVIL;

import java.util.function.BiConsumer;

import info.u_team.u_team_core.data.CommonLootTablesProvider;
import info.u_team.u_team_core.data.GenerationData;
import net.minecraft.loot.LootTable;
import net.minecraft.util.ResourceLocation;

public class EnhancedAnvilLootTablesProvider extends CommonLootTablesProvider {
	
	public EnhancedAnvilLootTablesProvider(GenerationData data) {
		super(data);
	}
	
	@Override
	protected void registerLootTables(BiConsumer<ResourceLocation, LootTable> consumer) {
		registerBlock(ENHANCED_ANVIL, addBasicBlockLootTable(ENHANCED_ANVIL.get()), consumer);
		registerBlock(CHIPPED_ENHANCED_ANVIL, addBasicBlockLootTable(CHIPPED_ENHANCED_ANVIL.get()), consumer);
		registerBlock(DAMAGED_ENHANCED_ANVIL, addBasicBlockLootTable(DAMAGED_ENHANCED_ANVIL.get()), consumer);
	}
	
}
