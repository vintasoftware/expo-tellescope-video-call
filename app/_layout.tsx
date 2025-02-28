import "@/global.css";
import "expo-dev-client";

import { UserProvider, WithSession } from "@tellescope/react-components";
import { Stack } from "expo-router";
import * as SplashScreen from "expo-splash-screen";
import { StatusBar } from "expo-status-bar";
import { useColorScheme } from "nativewind";
import { useEffect } from "react";
import { GestureHandlerRootView } from "react-native-gesture-handler";
import {
  initialWindowMetrics,
  SafeAreaProvider,
  SafeAreaView,
} from "react-native-safe-area-context";

import { GluestackUIProvider } from "@/components/gluestack-ui-provider";
import { ChimeMeetingProvider } from "@/modules/expo-aws-chime";

export const unstable_settings = {
  initialRouteName: "/(app)",
};

SplashScreen.preventAutoHideAsync();

export default function RootLayout() {
  useEffect(() => {
    // Prevents flickering:
    requestAnimationFrame(SplashScreen.hideAsync);
  }, []);

  const { colorScheme } = useColorScheme();
  return (
    <WithSession sessionOptions={{ host: process.env.EXPO_PUBLIC_TELLESCOPE_API_BASE_URL }}>
      <UserProvider>
        <ChimeMeetingProvider>
          <GluestackUIProvider mode={colorScheme}>
            <SafeAreaProvider initialMetrics={initialWindowMetrics}>
              <StatusBar />
              <SafeAreaView className="h-full bg-background-0 md:w-full">
                <GestureHandlerRootView className="flex-1">
                  <Stack
                    screenOptions={{
                      headerShown: false,
                      // Prevents flickering:
                      animation: "none",
                    }}
                  />
                </GestureHandlerRootView>
              </SafeAreaView>
            </SafeAreaProvider>
          </GluestackUIProvider>
        </ChimeMeetingProvider>
      </UserProvider>
    </WithSession>
  );
}
