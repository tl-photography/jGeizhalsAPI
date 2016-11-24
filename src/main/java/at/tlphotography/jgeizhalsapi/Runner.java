/*
 * @author Thomas Leber
 */
package at.tlphotography.jgeizhalsapi;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * The Class Runner.
 */
public class Runner {

	/** The logger. */
	public static final Logger LOGGER = LogManager.getLogger(Runner.class);

	/**
	 * Run.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 */
	public void run() throws IOException, InterruptedException {

		ArrayList<PriceList> list = new ArrayList<>();

		Reader in = new FileReader("./test/test.csv");
		Iterable<CSVRecord> records = CSVFormat.newFormat(';').parse(in);
		for (CSVRecord record : records) {
			LOGGER.info("Record :" + record.get(0));

			list.addAll(GeizhalsApi.getPrice(record.get(0)));

		}

		in.close();

		FileWriter fileWriter = new FileWriter("./test/test2.csv");

		for (PriceList csvRecord : list) {
			fileWriter.write(csvRecord + "\n");
		}

		fileWriter.flush();
		fileWriter.close();

		return;

	}

}
