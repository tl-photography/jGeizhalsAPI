/*
 * @author Thomas Leber
 */
package at.tlphotography.jgeizhalsapi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.KeyStore.Entry;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;

/**
 * The Class GeizhalsApi.
 */
public class GeizhalsApi {

	/** The Constant PRICE_FIND_REGEX. */
	private static final String PRICE_FIND_REGEX = "<span class=\\\"gh_price\\\"[^>]*?>[^;]*?; ([^<]*?)<[^>]*?>[^>]*?>[^\\\"]*?\\\"[^\\\"]*?\\\"[^\\\"]*?\\\"[^\\\"]*?\\\"[^\\\"]*?\\\"[^\\\"]*?\\\"[^\\\"]*?\\\"[^\\\"]*?\\\"[^\\\"]*?\\\"\\/redir\\.cgi\\?h=[^&]*?&amp;loc=([^&]*?)&";

	/** The Constant SEARCH_PAGE_REGEX. */
	private static final String SEARCH_PAGE_REGEX = "<td class=\"gh_fsimg\"[^>]*?><a href=\"(\\w[^\"]*?)\">[^>]*?>[^>]*?>[^>]*?>[^>]*?>[^']*?[^>]*?>[^>]*?>([^<]*?)<";

	/** The logger. */
	public static final Logger LOGGER = LogManager.getLogger(GeizhalsApi.class);

	/** The Constant GEIZHALS_SEARCH_URL. */
	private static final String GEIZHALS_SEARCH_URL = "http://geizhals.at/?fs=";

	/**
	 * Instantiates a new geizhals api.
	 */
	private GeizhalsApi() {

	}

	/**
	 * Gets the price.
	 *
	 * @param searchstring the string to search for a product at geizhals
	 * @return the price as {@link PriceList}
	 * @throws GeizhalsApiException the geizhals api exception
	 */
	public static List<PriceList> getPrice(String searchstring) throws GeizhalsApiException {
		String serachURL = getSearchString(searchstring);
		return search(searchstring, serachURL);
	}

	/**
	 * Gets the price.
	 *
	 * @param searchstring the searchstring
	 * @param n the n
	 * @return the price
	 * @throws GeizhalsApiException the geizhals api exception
	 */
	public static List<PriceList> getPrice(String searchstring, int n) throws GeizhalsApiException {
		String serachURL = getSearchString(searchstring);
		return search(searchstring, serachURL, n);
	}

	/**
	 * Gets the price from categorie.
	 *
	 * @param searchstring the searchstring
	 * @param cat the cat
	 * @return the price from categorie
	 * @throws GeizhalsApiException the geizhals api exception
	 */
	public static List<PriceList> getPriceFromCategorie(String searchstring, int cat) throws GeizhalsApiException {
		String serachURL = setSearchStringWithCategory(searchstring, cat);
		return search(searchstring, serachURL);
	}

	/**
	 * Gets the price.
	 *
	 * @param searchstring the string to search for a product at geizhals
	 * @param cat the Category of geizhals
	 * @param n the n
	 * @return the price as {@link PriceList}
	 * @throws GeizhalsApiException the geizhals api exception
	 */
	public static List<PriceList> getPriceFromCategorie(String searchstring, int cat, int n) throws GeizhalsApiException {
		String serachURL = setSearchStringWithCategory(searchstring, cat);
		return search(searchstring, serachURL, n);
	}

	/**
	 * Gets the search string.
	 *
	 * @param searchstring the searchstring
	 * @return the search string
	 */
	private static String getSearchString(String searchstring) {
		return GEIZHALS_SEARCH_URL + searchstring.replace(' ', '+');
	}

	/**
	 * Sets the search string with category.
	 *
	 * @param searchstring the searchstring
	 * @param cat the cat
	 * @return the string
	 */
	private static String setSearchStringWithCategory(String searchstring, int cat) {
		return GEIZHALS_SEARCH_URL + searchstring.replace(' ', '+') + "&in=" + cat;
	}

	/**
	 * Search.
	 *
	 * @param searchstring the searchstring
	 * @param serachURL the serach URL
	 * @return the list
	 * @throws GeizhalsApiException the geizhals api exception
	 */
	private static List<PriceList> search(String searchstring, String serachURL) throws GeizhalsApiException {
		String searchHtml = getSearchResult(serachURL);
		String searchResult = searchProduct(searchstring, searchHtml);
		return getPrices(searchstring, new SimpleEntry<String, String>(searchstring, searchResult));
	}

	/**
	 * Search.
	 *
	 * @param searchstring the searchstring
	 * @param serachURL the serach URL
	 * @param n the n
	 * @return the list
	 * @throws GeizhalsApiException the geizhals api exception
	 */
	private static List<PriceList> search(String searchstring, String serachURL, int n) throws GeizhalsApiException {
		String searchHtml = getSearchResult(serachURL);

		ArrayList<PriceList> priceList = new ArrayList<>();

		ArrayList<SimpleEntry<String, String>> possibleResults = searchProduct(searchstring, searchHtml, n);
		for (SimpleEntry<String, String> searchResult : possibleResults) {
			priceList.addAll(getPrices(searchstring, searchResult));
		}

		return priceList;
	}

	/**
	 * Gets the prices.
	 *
	 * @param searchstring the searchstring
	 * @param searchResult the search result
	 * @return the prices
	 * @throws GeizhalsApiException the geizhals api exception
	 */
	private static List<PriceList> getPrices(String searchstring, SimpleEntry<String, String> searchResult) throws GeizhalsApiException {
		if (searchResult.getValue() == null) {
			ArrayList<PriceList> temp = new ArrayList<>();
			temp.add(new PriceList(searchstring, "00.00", "not found"));
			LOGGER.warn("not found...");
			return temp;
		}
		String productUrl = "http://geizhals.at/" + searchResult.getValue() + "?hloc=at&hloc=de";
		String resultHtml;
		try {
			resultHtml = Jsoup.connect(productUrl).userAgent("Mozilla").get().html();
		} catch (IOException e) {
			LOGGER.error("Could not fetch html code from " + productUrl);
			throw new GeizhalsApiException(e);
		}
		LOGGER.debug("http://geizhals.at/" + searchResult);

		return searchPrices(resultHtml, searchResult);
	}

	/**
	 * Gets the search result.
	 *
	 * @param serachURL the serach URL
	 * @return the search result
	 * @throws GeizhalsApiException the geizhals api exception
	 */
	private static String getSearchResult(String serachURL) throws GeizhalsApiException {
		String searchHtml = null;
		try {
			searchHtml = Jsoup.connect(serachURL).userAgent("Mozilla").get().html();
		} catch (IOException e) {
			LOGGER.error("Could not fetch html code from " + GEIZHALS_SEARCH_URL);
			throw new GeizhalsApiException(e);
		}
		return searchHtml;
	}

	/**
	 * Search product.
	 *
	 * @param searchString the string
	 * @param html the html
	 * @param n the n
	 * @return the string
	 * @throws GeizhalsApiException the geizhals api exception
	 */
	private static ArrayList<SimpleEntry<String, String>> searchProduct(String searchString, String html, int n) throws GeizhalsApiException {
		final Matcher matcher = getMatcher(html);

		TreeMap<Double, SimpleEntry<String, String>> resultMap = new TreeMap<>();

		while (matcher.find()) {
			double jaroWinklerDistance = StringUtils.getJaroWinklerDistance(searchString, matcher.group(2));
			LOGGER.debug("Found " + matcher.group(2) + " search String: " + searchString + " distance :" + jaroWinklerDistance);

			resultMap.put(jaroWinklerDistance, new SimpleEntry<>(matcher.group(2), matcher.group(1)));
		}

		getFirstNEntries(resultMap, n).forEach((k, v) -> LOGGER.info(k + " " + v));
		return new ArrayList<>(getFirstNEntries(resultMap, n).values());

	}

	/**
	 * Search product.
	 *
	 * @param searchString the search string
	 * @param html the html
	 * @return the string
	 * @throws GeizhalsApiException the geizhals api exception
	 */
	private static String searchProduct(String searchString, String html) throws GeizhalsApiException {
		final Matcher matcher = getMatcher(html);

		while (matcher.find()) {
			if (matcher.group(2).equalsIgnoreCase(searchString)) {
				LOGGER.debug("found match at : " + matcher.group(1));
				return matcher.group(1);
			}
		}
		return null;
	}

	/**
	 * Gets the code.
	 *
	 * @param html the html
	 * @return the code
	 */
	private static Matcher getMatcher(String html) {
		final String regex = SEARCH_PAGE_REGEX;
		final Pattern pattern = Pattern.compile(regex);
		return pattern.matcher(html);
	}

	/**
	 * Gets the first N entries.
	 *
	 * @param resultMap the result map
	 * @param i the i
	 * @return the first N entries
	 * @throws GeizhalsApiException the geizhals api exception
	 */
	private static NavigableMap<Double, SimpleEntry<String, String>> getFirstNEntries(TreeMap<Double, SimpleEntry<String, String>> resultMap, int i) throws GeizhalsApiException {

		int newi = i;

		if(resultMap.size() == 0)
		{
			resultMap.put(0.00, new SimpleEntry<>(null, null));
			return resultMap;
		}
		
		if (i == 0) {
			throw new GeizhalsApiException("Number of Entries cannot be zero");
		}

		if (i > resultMap.size()) {
			newi = resultMap.size();
		}
		
		

		Double toKey = null;
		int j = 0;
		for (Double key : resultMap.descendingMap().keySet()) {
			toKey = key;
			if (j++ > newi) {
				break;
			}
		}
		return resultMap.descendingMap().headMap(toKey, true);
	}

	/**
	 * Search prices.
	 *
	 * @param string the string
	 * @param html the html
	 * @param searchResult
	 * @return the array list
	 */
	private static ArrayList<PriceList> searchPrices(String html, SimpleEntry<String, String> searchResult) {
		ArrayList<PriceList> list = new ArrayList<>();

		final String regex = PRICE_FIND_REGEX;
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(html);
		while (matcher.find()) {

			try {
				list.add(new PriceList(searchResult.getKey(), matcher.group(1), URLDecoder.decode(matcher.group(2), "UTF-8")));
			} catch (UnsupportedEncodingException e) {
				LOGGER.error("Encoding UTF8 is not supported", e);
			}
			break;
		}
		return list;
	}
}
