package ma.shopma.app.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton Retrofit.
 * BASE_URL pointe sur le backend Spring Boot (emulateur → 10.0.2.2:8080).
 * Pour basculer sur FakeStoreAPI, commenter BASE_URL et dé-commenter BASE_URL_FAKESTORE.
 */
public class RetrofitClient {

    // Backend Spring Boot (emulateur Android → machine hote)
    public static final String BASE_URL_SPRINGBOOT = "http://10.0.2.2:8080/";

    // Fallback FakeStoreAPI
    public static final String BASE_URL_FAKESTORE = "https://fakestoreapi.com/";

    // URL active (changer ici pour basculer)
    public static final String BASE_URL = BASE_URL_SPRINGBOOT;

    private static Retrofit instance;

    public static ApiService getService() {
        if (instance == null) {
            instance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return instance.create(ApiService.class);
    }
}
