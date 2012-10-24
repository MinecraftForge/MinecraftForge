package cpw.mods.fml.common;

import java.io.File;
import java.util.List;

import com.google.common.collect.SetMultimap;

public class DuplicateModsFoundException extends LoaderException {

	public SetMultimap<ModContainer,File> dupes;

	public DuplicateModsFoundException(SetMultimap<ModContainer, File> dupes) {
		this.dupes = dupes;
	}

}
