package uk.co.progger.alienFXLite.led;

public class AlienFXCommunicationException extends Exception {
	private static final long serialVersionUID = 1L;

	public AlienFXCommunicationException() {
		super();
	}

	public AlienFXCommunicationException(String message, Throwable cause) {
		super(message, cause);
	}

	public AlienFXCommunicationException(String message) {
		super(message);
	}

	public AlienFXCommunicationException(Throwable cause) {
		super(cause);
	}
}
