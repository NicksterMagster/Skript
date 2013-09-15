/*
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 * Copyright 2011, 2012 Peter Güttinger
 * 
 */

package ch.njol.skript.expressions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.iterator.EmptyIterator;

/**
 * @author Peter Güttinger
 */
@SuppressWarnings("serial")
@Name("Items In")
@Description({"All items in an inventory. Useful for looping or storing in a list variable.",
		"Please note that the positions of the items in the inventory are not saved, only their order is preserved."})
@Examples({"loop all items in the player's inventory:",
		"	loop-item is enchanted" +
				"	remove loop-item from the player",
		"set {inventory.%player%} to items in the player's inventory"})
@Since("2.0")
public class ExprItemsIn extends SimpleExpression<ItemStack> {// TODO return slots to be able to change the loop-item
	static {
		Skript.registerExpression(ExprItemsIn.class, ItemStack.class, ExpressionType.PROPERTY,
				"[all] items (in|of|contained in|out of) %inventories%");
	}
	
	Expression<Inventory> invis;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		invis = (Expression<Inventory>) exprs[0];
		return true;
	}
	
	@Override
	protected ItemStack[] get(final Event e) {
		final ArrayList<ItemStack> r = new ArrayList<ItemStack>();
		for (final Inventory invi : invis.getArray(e)) {
			for (final ItemStack is : invi) {
				if (is != null)
					r.add(is);
			}
		}
		return r.toArray(new ItemStack[r.size()]);
	}
	
	@Override
	public Iterator<? extends ItemStack> iterator(final Event e) {
		final Inventory[] is = invis.getArray(e);
		if (is.length == 0)
			return EmptyIterator.get();
		return new Iterator<ItemStack>() {
			int isi = 0;
			Inventory current = is[0];
			int i = 0;
			
			@Override
			public boolean hasNext() {
				while (i < current.getSize() && current.getItem(i) == null)
					i++;
				while (i >= current.getSize() && isi < is.length - 1) {
					current = is[++isi];
					i = 0;
					while (i < current.getSize() && current.getItem(i) == null)
						i++;
				}
				return isi <= is.length;
			}
			
			@Override
			public ItemStack next() {
				if (!hasNext())
					throw new NoSuchElementException();
				return current.getItem(i++); // new Slot(current, i++);
			}
			
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	@Override
	public boolean isLoopOf(final String s) {
		return s.equalsIgnoreCase("item");
	}
	
	@Override
	public String toString(final Event e, final boolean debug) {
		return "items in " + invis.toString(e, debug);
	}
	
	@Override
	public boolean isSingle() {
		return false;
	}
	
	@Override
	public Class<? extends ItemStack> getReturnType() {
		return ItemStack.class;
	}
	
}