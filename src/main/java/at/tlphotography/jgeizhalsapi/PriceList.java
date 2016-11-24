/*
 * @author Thomas Leber
 */
package at.tlphotography.jgeizhalsapi;

import java.net.MalformedURLException;

/**
 * The Class PriceList.
 */
public class PriceList {

	/** The price. */
	private float price;

	/** The url. */
	private String url;

	/** The name. */
	private String name;

	/**
	 * Instantiates a new price list.
	 *
	 * @param string the string
	 * @param group the group
	 * @param group2 the group 2
	 * @throws MalformedURLException the malformed URL exception
	 */
	public PriceList(String string, String group, String group2) {
		price = Float.parseFloat(group.replace(',', '.').replace('-', '0'));
		url = group2;
		setName(string);
	}

	/**
	 * Gets the price.
	 *
	 * @return the price
	 */
	public float getPrice() {
		return price;
	}

	/**
	 * Sets the price.
	 *
	 * @param price the new price
	 */
	public void setPrice(float price) {
		this.price = price;
	}

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the url.
	 *
	 * @param url the new url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name + ";" + price + ";" + url;
	}
}
