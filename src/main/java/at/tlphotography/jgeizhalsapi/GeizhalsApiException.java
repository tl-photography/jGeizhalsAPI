/*
 * @author Thomas Leber
 */
package at.tlphotography.jgeizhalsapi;

/**
 * The Class GeizhalsApiException.
 */
public class GeizhalsApiException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3956366918563609463L;

	/**
	 * Instantiates a new geizhals api exception.
	 *
	 * @param e the e
	 */
	public GeizhalsApiException(Exception e) {
		super(e);
	}

	/**
	 * Instantiates a new geizhals api exception.
	 *
	 * @param string the string
	 */
	public GeizhalsApiException(String string) {
		super(string);
	}

}
