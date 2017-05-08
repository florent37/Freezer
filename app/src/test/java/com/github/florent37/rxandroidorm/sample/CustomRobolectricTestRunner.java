package com.github.florent37.rxandroidorm.sample;

import android.os.Build;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.internal.bytecode.InstrumentationConfiguration;
import org.robolectric.internal.bytecode.ShadowMap;
import org.robolectric.manifest.AndroidManifest;

import java.io.File;
import java.util.Properties;

public class CustomRobolectricTestRunner extends RobolectricTestRunner {

    public static final String PATH_ASSET = "../../../../assets/" + BuildConfig.FLAVOR + "/" + BuildConfig.BUILD_TYPE;
    public static final String PATH_RESOURCE = "../../../../res/merged/" + BuildConfig.FLAVOR + "/" + BuildConfig.BUILD_TYPE;
    public static final String PATH_MANIFEST = "build/intermediates/manifests/full/" + BuildConfig.FLAVOR + "/" + BuildConfig.BUILD_TYPE + "/AndroidManifest.xml";

    public static final String CONFIG_MANIFEST = "manifest";
    public static final String CONFIG_ASSET_DIR = "assetDir";
    public static final String CONFIG_RESOURCE_DIR = "resourceDir";
    public static final String CONFIG_PACKAGE_NAME = "packageName";
    public static final String CONFIG_SDK = "sdk";

    public static final String PACKAGE_NAME = "net.ilius.android.resources";

    public static final String PATH_PREFIX = "app/";

    public CustomRobolectricTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {
        String path = PATH_MANIFEST;

        // android studio has a different execution root for tests than pure gradle
        // so we avoid here manual effort to get them running inside android studio
        if (!new File(path).exists()) {
            path = PATH_PREFIX + path;
        }

        config = overwriteConfig(config, CONFIG_MANIFEST, path);
        config = overwriteConfig(config, CONFIG_ASSET_DIR, PATH_ASSET);
        config = overwriteConfig(config, CONFIG_RESOURCE_DIR, PATH_RESOURCE);
        config = overwriteConfig(config, CONFIG_PACKAGE_NAME, PACKAGE_NAME);

        return super.getAppManifest(config);
    }

    @Override
    protected int pickSdkVersion(Config config, AndroidManifest manifest) {
        config = overwriteConfig(config, CONFIG_SDK, String.valueOf(Build.VERSION_CODES.JELLY_BEAN));
        return super.pickSdkVersion(config, manifest);
    }

    @Override
    public InstrumentationConfiguration createClassLoaderConfig() {
        InstrumentationConfiguration.Builder builder = InstrumentationConfiguration.newBuilder();
        return builder.build();
    }

    protected Config.Implementation overwriteConfig(Config config, String key, String value) {
        Properties properties = new Properties();
        properties.setProperty(key, value);
        return new Config.Implementation(config, Config.Implementation.fromProperties(properties));
    }
}
