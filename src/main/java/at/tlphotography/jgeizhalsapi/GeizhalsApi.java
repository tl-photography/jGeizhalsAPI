/*
 * @author Thomas Leber
 */
package at.tlphotography.jgeizhalsapi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;

/**
 * The Class GeizhalsApi.
 */
public class GeizhalsApi {

	/** The logger. */
	public static final Logger LOGGER = LogManager.getLogger(GeizhalsApi.class);

	private static final String GEIZHALS_SEARCH_URL = "http://geizhals.at/?fs=";

	/**
	 * Gets the price.
	 *
	 * @param string
	 *            the string
	 * @return
	 * @return the price
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static ArrayList<PriceList> getPrice(String string) throws IOException, InterruptedException {
		String html = Jsoup.connect(GEIZHALS_SEARCH_URL + string.replace(' ', '+')).userAgent("Mozilla").get().html();
		// LOGGER.debug(html);

		String searchResult = searchProduct(string, html);

		if (searchResult == null) {
			ArrayList<PriceList> temp = new ArrayList<>();
			temp.add(new PriceList(string, "00.00", "not found"));
			LOGGER.warn("not found...");
			return temp;
		}
		// LOGGER.debug(html);
		String html2 = Jsoup.connect("http://geizhals.at/" + searchResult + "?hloc=at&hloc=de").userAgent("Mozilla").get().html();
		// LOGGER.debug(html2);
		LOGGER.debug("http://geizhals.at/" + searchResult);

		ArrayList<PriceList> temp = searchPrices(string, html2);

		return temp;
	}

	/**
	 * Search product.
	 *
	 * @param string
	 *            the string
	 * @param html
	 *            the html
	 * @return the string
	 */
	private static String searchProduct(String string, String html) {
		final String regex = "<td class=\"gh_fsimg\"[^>]*?><a href=\"(\\w[^\"]*?)\">[^>]*?>[^>]*?>[^>]*?>[^>]*?>[^']*?[^>]*?>[^>]*?>([^<]*?)<";
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(html);
		while (matcher.find()) {
			if (matcher.group(2).equalsIgnoreCase(string)) {
				LOGGER.debug("found match at : " + matcher.group(1));
				return matcher.group(1);
			}
		}
		return null;
	}

	/**
	 * Search prices.
	 *
	 * @param string
	 *            the string
	 * @param html
	 *            the html
	 * @param html2
	 * @return the array list
	 * @throws MalformedURLException
	 */
	private static ArrayList<PriceList> searchPrices(String string, String html) throws MalformedURLException {
		ArrayList<PriceList> list = new ArrayList<>();

		final String regex = "<span class=\\\"gh_price\\\"[^>]*?>[^;]*?; ([^<]*?)<[^>]*?>[^>]*?>[^\\\"]*?\\\"[^\\\"]*?\\\"[^\\\"]*?\\\"[^\\\"]*?\\\"[^\\\"]*?\\\"[^\\\"]*?\\\"[^\\\"]*?\\\"[^\\\"]*?\\\"[^\\\"]*?\\\"\\/redir\\.cgi\\?h=[^&]*?&amp;loc=([^&]*?)&";
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(html);
		// LOGGER.debug(html);
		while (matcher.find()) {

			list.add(new PriceList(string, matcher.group(1), URLDecoder.decode(matcher.group(2))));
			break;
		}
		return list;
	}
}
