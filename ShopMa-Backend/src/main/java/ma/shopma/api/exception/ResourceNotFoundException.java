package ma.shopma.api.exception;

/**
 * Levee lorsqu'une ressource demandee n'existe pas (HTTP 404).
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
