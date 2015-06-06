package net.year4000.mapnodes;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import net.year4000.mapnodes.backend.Backend;
import net.year4000.utilities.URLBuilder;
import net.year4000.utilities.sdk.API;
import net.year4000.utilities.sdk.HttpFetcher;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@ToString
public class Settings {
    private static transient Settings inst = null;

    @NonNull
    @SerializedName("client_locales")
    private String clientLocales = "https://raw.githubusercontent.com/Year4000/Locales/master/net/year4000/mapnodes/locales/";

    @NonNull
    @SerializedName("system_locales")
    private String systemLocales = "https://raw.githubusercontent.com/Year4000/Locales/master/net/year4000/mapnodes/system/";

    @NonNull
    @SerializedName("load_maps")
    private Integer loadMaps = 0;

    @NonNull
    private JsonObject kits = new JsonObject();

    // todo remove this, was added for backwards compatibly
    private transient List<String> modes = Arrays.asList(System.getProperty("modes", ",").toLowerCase().split("(,|;)"));

    // todo remove this, was added for backwards compatibly
    public transient String key = System.getenv("Y4K_KEY");

    public static Settings get() {
        if (inst == null) {
            try {
                URLBuilder url = URLBuilder.builder(API.BASE_URL).addPath("configs").addPath("mapnodes");
                inst = HttpFetcher.get(url.build(), Settings.class);
            }
            catch (Exception e) {
                MapNodesPlugin.log(e, false);
                inst = new Settings();
            }
        }

        return inst;
    }
}
