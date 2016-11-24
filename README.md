# jGeizhalsAPI

This is an Java API for the price compare portal geizhals.at. 

# Usage

			// search in a category and return the 5 best matches
			GeizhalsApi.getPriceFromCategorie("Tiltall BH-07", Categories.VIDEOFOTOTV, 5).forEach(LOGGER::info);
			
			// search in a category and return the perfect match (case insensitive)
			GeizhalsApi.getPriceFromCategorie("Tiltall BH-07", Categories.VIDEOFOTOTV).forEach(LOGGER::info);
			
			// search and return the 5 best matches
			GeizhalsApi.getPrice("Tiltall BH-07", 5).forEach(LOGGER::info);
			
			// search and return the perfect match (case insensitive)
			GeizhalsApi.getPrice("Tiltall BH-07").forEach(LOGGER::info);
      
See also: [src/main/java/at/tlphotography/jgeizhalsapi/Runner.java](src/main/java/at/tlphotography/jgeizhalsapi/Runner.java)
