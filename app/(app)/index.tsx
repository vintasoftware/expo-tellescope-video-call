import { useRouter } from "expo-router";
import React, { useCallback } from "react";

import { Button } from "@/components/ui/button";
import { Text } from "@/components/ui/text";
import { VStack } from "@/components/ui/vstack";

const ENDUSER_ID = process.env.EXPO_PUBLIC_TELLESCOPE_ENDUSER_ID;

export default function HomeScreen() {
  const router = useRouter();

  const handleStartMeeting = useCallback(() => {
    router.push("/meeting");
  }, [router]);

  if (!ENDUSER_ID) {
    return (
      <VStack space="xl" className="flex-1 p-4">
        <Text>Please set the EXPO_PUBLIC_TELLESCOPE_ENDUSER_ID environment variable</Text>
      </VStack>
    );
  }

  return (
    <VStack space="xl" className="flex-1 p-4">
      <VStack space="md">
        <Text size="2xl" bold>
          Video Calls
        </Text>
        <Text size="sm" className="text-typography-500">
          Join a secure video call powered by Tellescope with End User: {ENDUSER_ID}
        </Text>
      </VStack>

      <VStack space="md" className="flex-1">
        <Button size="lg" variant="solid" className="bg-primary-500" onPress={handleStartMeeting}>
          <Text className="text-typography-white">Start Video Call</Text>
        </Button>
      </VStack>
    </VStack>
  );
}
