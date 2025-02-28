import { ConfigContext, ExpoConfig } from "expo/config";

export default ({ config }: ConfigContext): ExpoConfig => ({
  ...config,
  name: "tellescope-chime-demo-app",
  slug: "tellescope-chime-demo-app",
  version: "1.0.0",
  orientation: "portrait",
  icon: "./assets/images/icon.png",
  scheme: "myapp",
  userInterfaceStyle: "automatic",
  newArchEnabled: true,
  jsEngine: "hermes",
  notification: {
    androidMode: "collapse",
    androidCollapsedTitle: "New messages",
  },
  ios: {
    supportsTablet: true,
    config: {
      usesNonExemptEncryption: false,
    },
    bundleIdentifier: "com.vinta.healthapp",
    infoPlist: {
      UIBackgroundModes: ["remote-notification"],
    },
  },
  android: {
    adaptiveIcon: {
      foregroundImage: "./assets/images/adaptive-icon.png",
      backgroundColor: "#ffffff",
    },
    permissions: [
      "android.permission.RECEIVE_BOOT_COMPLETED",
      "android.permission.SCHEDULE_EXACT_ALARM",
      "android.permission.POST_NOTIFICATIONS",
      "android.permission.USE_FULL_SCREEN_INTENT",
      // Chime SDK required permissions
      "android.permission.CAMERA",
      "android.permission.MODIFY_AUDIO_SETTINGS",
      "android.permission.RECORD_AUDIO",
    ],
    package: "com.vinta.healthapp",
    googleServicesFile: process.env.GOOGLE_SERVICES_JSON || "./_dev/google-services.json",
  },
  web: {
    bundler: "metro",
    output: "static",
    favicon: "./assets/images/favicon.png",
  },
  plugins: [
    "expo-router",
    [
      "expo-splash-screen",
      {
        image: "./assets/images/splash-icon.png",
        imageWidth: 200,
        resizeMode: "contain",
        backgroundColor: "#ffffff",
      },
    ],
    [
      "expo-secure-store",
      {
        requireAuthentication: false,
      },
    ],
  ],
  experiments: {
    typedRoutes: true,
  },
  extra: {
    router: {
      origin: false,
    },
    eas: {
      projectId: "b90347a9-ca6d-4949-9545-82fcce6ed6aa",
    },
  },
});
