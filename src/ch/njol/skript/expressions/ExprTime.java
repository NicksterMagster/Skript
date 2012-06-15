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

import org.bukkit.World;
import org.bukkit.event.Event;

import ch.njol.skript.Skript;
import ch.njol.skript.api.Changer.ChangeMode;
import ch.njol.skript.api.Getter;
import ch.njol.skript.classes.DefaultChangers;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SimpleExpression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Time;

/**
 * 
 * @author Peter Güttinger
 * 
 */
public class ExprTime extends SimpleExpression<Time> {
	
	static {
		Skript.registerExpression(ExprTime.class, Time.class, "[the] time [(in|of) %worlds%]");
	}
	
	private Expression<World> worlds = null;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(final Expression<?>[] vars, final int matchedPattern, final ParseResult parser) {
		worlds = (Expression<World>) vars[0];
		return true;
	}
	
	@Override
	protected Time[] getAll(final Event e) {
		return worlds.getArray(e, Time.class, new Getter<Time, World>() {
			@Override
			public Time get(final World w) {
				return new Time((int) w.getTime());
			}
		});
	}
	
	@Override
	public Class<?> acceptChange(final ChangeMode mode) {
		return DefaultChangers.timeChanger.acceptChange(mode);
	}
	
	@Override
	public void change(final Event e, final Object delta, final ChangeMode mode) {
		DefaultChangers.timeChanger.change(worlds.getArray(e), delta, mode);
	}
	
	@Override
	public Class<Time> getReturnType() {
		return Time.class;
	}
	
	@Override
	public String getDebugMessage(final Event e) {
		if (e == null)
			return "time in " + worlds.getDebugMessage(e);
		return Skript.getDebugMessage(getAll(e));
	}
	
	@Override
	public String toString() {
		return "the time in " + worlds;
	}
	
	@Override
	public boolean isSingle() {
		return worlds.isSingle();
	}
	
}