package de.oppermann.pomutils.commands;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;

import de.oppermann.pomutils.select.SelectionStrategy;

public class SelectionStrategyConverter implements IStringConverter<SelectionStrategy> {

	@Override
	public SelectionStrategy convert(String value) {
		if (value == null) {
			return SelectionStrategy.OUR;
		}

		try {
			return SelectionStrategy.valueOf(value.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new ParameterException("--select must be one of 'our', 'their', or 'prompt'");
		}
	}

}
