/*
 * @author Thomas Leber
 */
package at.tlphotography.jgeizhalsapi;

import java.io.IOException;
import java.text.ParseException;

/**
 * The Class Main.
 */
public class Main {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 * @throws ParseException
	 */
	public static void main(String[] args) throws IOException, InterruptedException, ParseException {

		System.setProperty("http.proxyHost", "127.0.0.1");
		System.setProperty("http.proxyPort", "3128");

		Runner runner = new Runner();
		runner.run();

	}

}
