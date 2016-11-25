/*
 * @author Thomas Leber
 */
package at.tlphotography.jgeizhalsapi.example;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import at.tlphotography.jgeizhalsapi.Categories;
import at.tlphotography.jgeizhalsapi.GeizhalsApi;
import at.tlphotography.jgeizhalsapi.GeizhalsApiException;

/**
 * The Class Runner.
 */
public class Runner {

	/** The logger. */
	public static final Logger LOGGER = LogManager.getLogger(Runner.class);

	/**
	 * Run.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 */
	public void run() {

		try {
			// search in a category and return the 5 best matches
			GeizhalsApi.getPriceFromCategorie("Tiltall BH-07", Categories.VIDEOFOTOTV, 5).forEach(LOGGER::info);
			
			// search in a category and return the perfect match (case insensitive)
			GeizhalsApi.getPriceFromCategorie("Tiltall BH-07", Categories.VIDEOFOTOTV).forEach(LOGGER::info);
			
			// search and return the 5 best matches
			GeizhalsApi.getPrice("Tiltall BH-07", 5).forEach(LOGGER::info);
			
			// search and return the perfect match (case insensitive)
			GeizhalsApi.getPrice("Tiltall BH-07").forEach(LOGGER::info);

		} catch (GeizhalsApiException e) {
			LOGGER.error("someting went wrong", e);
		}

	}

}
