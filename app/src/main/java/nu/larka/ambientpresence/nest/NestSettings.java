package nu.larka.ambientpresence.nest;

import android.content.Context;
import android.content.SharedPreferences;
import com.nestapi.lib.API.AccessToken;

public class NestSettings {

    private static final String TOKEN_KEY = "token";
    private static final String EXPIRATION_KEY = "expiration";

    public static void saveAuthToken(Context context, AccessToken token) {
        getPrefs(context).edit()
                .putString(TOKEN_KEY, token.getToken())
                .putLong(EXPIRATION_KEY, token.getExpiresIn())
                .commit();
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(AccessToken.class.getSimpleName(), 0);
    }

    public static AccessToken loadAuthToken(Context context) {
        final SharedPreferences prefs = getPrefs(context);
        final String token = prefs.getString(TOKEN_KEY, null);
        final long expirationDate = prefs.getLong(EXPIRATION_KEY, -1);

        if(token == null || expirationDate == -1) {
            return null;
        }

        return new AccessToken.Builder()
                .setToken(token)
                .setExpiresIn(expirationDate)
                .build();
    }

    public static boolean hasAuthToken(Context context) {
        final SharedPreferences prefs = getPrefs(context);
        final String token = prefs.getString(TOKEN_KEY, null);
        final long expirationDate = prefs.getLong(EXPIRATION_KEY, -1);
        return (token != null || expirationDate != -1);
    }

    public static void removeAuthToken(Context context) {
        getPrefs(context).edit()
                .putString(TOKEN_KEY, null)
                .putLong(EXPIRATION_KEY, -1)
                .commit();
    }

    public static void saveNestEnvironment(Context context, NestEnvironment env) {
        getPrefs(context).edit().putBoolean(env.getName(), env.isEnabled()).commit();
    }

    public static boolean getNestEnvironment(Context context, String environment) {
        final SharedPreferences prefs = getPrefs(context);
        return prefs.getBoolean(environment, false);
    }
}
