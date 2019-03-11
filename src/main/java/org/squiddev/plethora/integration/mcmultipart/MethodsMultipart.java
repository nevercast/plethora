package org.squiddev.plethora.integration.mcmultipart;

import com.google.common.collect.Maps;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.lua.LuaException;
import mcmultipart.MCMultiPart;
import mcmultipart.api.container.IMultipartContainer;
import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.slot.IPartSlot;
import org.squiddev.plethora.api.method.IContext;
import org.squiddev.plethora.api.method.wrapper.FromTarget;
import org.squiddev.plethora.api.method.wrapper.PlethoraMethod;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import static org.squiddev.plethora.integration.mcmultipart.IntegrationMcMultipart.getBasicMeta;

public class MethodsMultipart {
	@PlethoraMethod(modId = MCMultiPart.MODID, doc = "-- Get a list of all parts in the multipart.")
	public static Map<Integer, ?> listParts(@FromTarget IMultipartContainer container) {
		Collection<? extends IPartInfo> parts = container.getParts().values();

		int i = 0;
		Map<Integer, Map<Object, Object>> out = Maps.newHashMap();
		for (IPartInfo part : parts) {
			out.put(++i, getBasicMeta(part));
		}

		return out;
	}

	@PlethoraMethod(modId = MCMultiPart.MODID, doc = "-- Get a lookup of slot to parts.")
	public static Map<String, ?> listSlottedParts(@FromTarget IMultipartContainer container) {
		Map<String, Map<Object, Object>> parts = Maps.newHashMap();

		for (Map.Entry<IPartSlot, ? extends IPartInfo> slot : container.getParts().entrySet()) {
			parts.put(slot.getKey().getRegistryName().toString().toLowerCase(Locale.ENGLISH), getBasicMeta(slot.getValue()));
		}

		return parts;
	}

	@Nullable
	@PlethoraMethod(modId = MCMultiPart.MODID, doc = "-- Get a reference to the part in the specified slot.")
	public static ILuaObject getSlottedPart(final IContext<IMultipartContainer> context, IPartSlot slot) {
		// TODO: final IPartSlot slot = MCMultiPart.slotRegistry.getValue(new ResourceLocation(slotName));
		//  if (slot == null) throw new LuaException("Bad name '" + slotName + "' for argument 1");

		IMultipartContainer container = context.getTarget();

		IPartInfo part = container.get(slot).orElse(null);
		return part == null
			? null
			: context.makeChild(part, new ReferenceMultipart(container, part)).getObject();
	}

	@Nullable
	@PlethoraMethod(modId = MCMultiPart.MODID, doc = "-- Get the metadata of the part in the specified slot.")
	public static Map<Object, Object> getSlottedPartMeta(final IContext<IMultipartContainer> context, IPartSlot slot) throws LuaException {
		IMultipartContainer container = context.getTarget();

		IPartInfo part = container.get(slot).orElse(null);
		return part == null
			? null
			: context.makePartialChild(part).getMeta();
	}
}
