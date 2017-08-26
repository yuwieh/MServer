package de.mediathekview.mserver.base.config;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.mediathekview.mlib.config.ConfigDTO;
import de.mediathekview.mlib.daten.Sender;
import de.mediathekview.mlib.filmlisten.FilmlistFormats;

/**
 * A POJO with the configs for MServer.
 */
public class MServerConfigDTO extends MServerBasicConfigDTO implements ConfigDTO {
	/**
	 * The maximum amount of cpu threads to be used.
	 */
	private Integer maximumCpuThreads;
	/**
	 * The maximum duration in minutes the server should run.<br>
	 * If set to 0 the server runs without a time limit.
	 */
	private Integer maximumServerDurationInMinutes;
	private Map<Sender, MServerBasicConfigDTO> senderConfigurations;

	private Set<Sender> senderExcluded;
	private Set<Sender> senderIncluded;

	private Set<FilmlistFormats> filmlistSaveFormats;
	private Map<FilmlistFormats, String> filmlistSavePaths;
	private FilmlistFormats filmlistImportFormat;
	private String filmlistImportLocation;

	public MServerConfigDTO() {
		senderConfigurations = new EnumMap<>(Sender.class);
		senderExcluded = new HashSet<>();
		senderIncluded = new HashSet<>();
		filmlistSaveFormats = new HashSet<>();
		filmlistSavePaths = new EnumMap<>(FilmlistFormats.class);
	}

	public Map<Sender, MServerBasicConfigDTO> getSenderConfigurations() {
		return senderConfigurations;
	}

	public void setSenderConfigurations(final Map<Sender, MServerBasicConfigDTO> aSenderConfigurations) {
		senderConfigurations = aSenderConfigurations;
	}

	public Integer getMaximumCpuThreads() {
		return maximumCpuThreads;
	}

	public void setMaximumCpuThreads(final Integer aMaximumCpuThreads) {
		maximumCpuThreads = aMaximumCpuThreads;
	}

	public Integer getMaximumServerDurationInMinutes() {
		return maximumServerDurationInMinutes;
	}

	public void setMaximumServerDurationInMinutes(final Integer aMaximumServerDurationInMinutes) {
		maximumServerDurationInMinutes = aMaximumServerDurationInMinutes;
	}

	public Set<FilmlistFormats> getFilmlistSaveFormats() {
		return filmlistSaveFormats;
	}

	public Map<FilmlistFormats, String> getFilmlistSavePaths() {
		return filmlistSavePaths;
	}

	public void setFilmlistSaveFormats(Set<FilmlistFormats> filmlistSaveFormats) {
		this.filmlistSaveFormats = filmlistSaveFormats;
	}

	public void setFilmlistSavePaths(Map<FilmlistFormats, String> filmlistSavePaths) {
		this.filmlistSavePaths = filmlistSavePaths;
	}

	public FilmlistFormats getFilmlistImportFormat() {
		return filmlistImportFormat;
	}

	public void setFilmlistImportFormat(FilmlistFormats filmlistImportFormat) {
		this.filmlistImportFormat = filmlistImportFormat;
	}

	public String getFilmlistImportLocation() {
		return filmlistImportLocation;
	}

	public void setFilmlistImportLocation(String filmlistImportLocation) {
		this.filmlistImportLocation = filmlistImportLocation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((filmlistImportFormat == null) ? 0 : filmlistImportFormat.hashCode());
		result = prime * result + ((filmlistImportLocation == null) ? 0 : filmlistImportLocation.hashCode());
		result = prime * result + ((filmlistSaveFormats == null) ? 0 : filmlistSaveFormats.hashCode());
		result = prime * result + ((filmlistSavePaths == null) ? 0 : filmlistSavePaths.hashCode());
		result = prime * result + ((maximumCpuThreads == null) ? 0 : maximumCpuThreads.hashCode());
		result = prime * result
				+ ((maximumServerDurationInMinutes == null) ? 0 : maximumServerDurationInMinutes.hashCode());
		result = prime * result + ((senderConfigurations == null) ? 0 : senderConfigurations.hashCode());
		result = prime * result + ((senderExcluded == null) ? 0 : senderExcluded.hashCode());
		result = prime * result + ((senderIncluded == null) ? 0 : senderIncluded.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof MServerConfigDTO))
			return false;
		MServerConfigDTO other = (MServerConfigDTO) obj;
		if (filmlistImportFormat != other.filmlistImportFormat)
			return false;
		if (filmlistImportLocation == null) {
			if (other.filmlistImportLocation != null)
				return false;
		} else if (!filmlistImportLocation.equals(other.filmlistImportLocation))
			return false;
		if (filmlistSaveFormats == null) {
			if (other.filmlistSaveFormats != null)
				return false;
		} else if (!filmlistSaveFormats.equals(other.filmlistSaveFormats))
			return false;
		if (filmlistSavePaths == null) {
			if (other.filmlistSavePaths != null)
				return false;
		} else if (!filmlistSavePaths.equals(other.filmlistSavePaths))
			return false;
		if (maximumCpuThreads == null) {
			if (other.maximumCpuThreads != null)
				return false;
		} else if (!maximumCpuThreads.equals(other.maximumCpuThreads))
			return false;
		if (maximumServerDurationInMinutes == null) {
			if (other.maximumServerDurationInMinutes != null)
				return false;
		} else if (!maximumServerDurationInMinutes.equals(other.maximumServerDurationInMinutes))
			return false;
		if (senderConfigurations == null) {
			if (other.senderConfigurations != null)
				return false;
		} else if (!senderConfigurations.equals(other.senderConfigurations))
			return false;
		if (senderExcluded == null) {
			if (other.senderExcluded != null)
				return false;
		} else if (!senderExcluded.equals(other.senderExcluded))
			return false;
		if (senderIncluded == null) {
			if (other.senderIncluded != null)
				return false;
		} else if (!senderIncluded.equals(other.senderIncluded))
			return false;
		return true;
	}

	public Set<Sender> getSenderExcluded() {
		return senderExcluded;
	}

	public void setSenderExcluded(Set<Sender> senderExcluded) {
		this.senderExcluded = senderExcluded;
	}

	public Set<Sender> getSenderIncluded() {
		return senderIncluded;
	}

	public void setSenderIncluded(Set<Sender> senderIncluded) {
		this.senderIncluded = senderIncluded;
	}
	
}
