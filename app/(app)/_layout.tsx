import { useSession } from "@tellescope/react-components";
import { Redirect, Stack } from "expo-router";

export default function AppLayout() {
  const session = useSession();

  // Redirect to login if not authenticated
  if (!session?.authToken) {
    return <Redirect href="/login" />;
  }

  return (
    <Stack
      screenOptions={{
        headerShown: false,
        // Prevents flickering:
        animation: "none",
      }}
    />
  );
}
