package org.squiddev.plethora.integration.forestry;

import dan200.computercraft.api.lua.LuaException;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.config.Constants;
import org.squiddev.plethora.api.method.IContext;
import org.squiddev.plethora.api.method.wrapper.PlethoraMethod;
import org.squiddev.plethora.api.module.IModuleContainer;
import org.squiddev.plethora.utils.LuaList;

import java.util.Map;

import static forestry.api.genetics.AlleleManager.alleleRegistry;

public class MethodsAnalyzer {
	@PlethoraMethod(
		module = IntegrationForestry.analyzerMod, worldThread = false, modId = Constants.MOD_ID,
		doc = "-- Get a list of all species roots"
	)
	public static Map<Integer, String> getSpeciesRoots() {
		return new LuaList<>(alleleRegistry.getSpeciesRoot().keySet()).asMap();
	}

	private static ISpeciesRoot getSpeciesRoot(String uid) throws LuaException {
		ISpeciesRoot root = alleleRegistry.getSpeciesRoot(uid);
		if (root == null) throw new LuaException("Species root " + uid + " does not exist");
		return root;
	}

	@PlethoraMethod(
		module = IntegrationForestry.analyzerMod, worldThread = false, modId = Constants.MOD_ID,
		doc = "-- Get a list of all species in the given species root"
	)
	public static Map<Integer, Object> getSpeciesList(String root) throws LuaException {
		ISpeciesRoot rootObj = getSpeciesRoot(root);

		LuaList<Object> species = alleleRegistry.getRegisteredAlleles(rootObj.getSpeciesChromosomeType()).stream()
			.map(IAlleleSpecies.class::cast)
			.filter(s -> !s.isSecret())
			.map(MetaGenome::getAlleleMeta)
			.collect(LuaList.toLuaList());

		return species.asMap();
	}

	@PlethoraMethod(
		module = IntegrationForestry.analyzerMod, worldThread = false, modId = Constants.MOD_ID,
		doc = "-- Get a list of all mutations in the given species root"
	)
	public static Map<Integer, ?> getMutationsList(IContext<IModuleContainer> context, String root) throws LuaException {
		ISpeciesRoot rootObj = getSpeciesRoot(root);

		LuaList<Map<Object, Object>> mutations = rootObj.getMutations(false).stream()
			.filter(s -> !s.isSecret())
			.map(m -> context.makePartialChild(m).getMeta())
			.collect(LuaList.toLuaList());

		return mutations.asMap();
	}
}
