import { useSession } from "@tellescope/react-components";
import { useRouter } from "expo-router";
import { useState } from "react";
import { View } from "react-native";

import { Button } from "@/components/ui/button";
import { ButtonText } from "@/components/ui/button";
import {
  FormControl,
  FormControlError,
  FormControlErrorText,
  FormControlHelper,
  FormControlHelperText,
  FormControlLabel,
  FormControlLabelText,
} from "@/components/ui/form-control";
import { Input } from "@/components/ui/input";
import { InputField } from "@/components/ui/input";
import { Text } from "@/components/ui/text";
import { VStack } from "@/components/ui/vstack";

interface LoginFormState {
  email: string;
  password: string;
}

export default function LoginScreen() {
  const router = useRouter();
  const session = useSession();
  const [formState, setFormState] = useState<LoginFormState>({
    email: process.env.EXPO_PUBLIC_TELLESCOPE_USER_EMAIL || "",
    password: process.env.EXPO_PUBLIC_TELLESCOPE_USER_PASSWORD || "",
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleLogin = async () => {
    try {
      setIsLoading(true);
      setError(null);
      const { email, password } = formState;
      await session.authenticate(email, password);
      router.replace("/(app)");
    } catch (err) {
      console.error("Login error", err);
      setError("Invalid email or password");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <View className="flex-1 items-center justify-center bg-background-0 p-4">
      <VStack space="lg" className="w-full max-w-md">
        <Text size="2xl" className="text-center font-bold">
          Tellescope + AWS Chime Demo
        </Text>
        <Text size="sm" className="text-center text-typography-400">
          Please log in with your Tellescope account
        </Text>

        <VStack space="md">
          <FormControl isRequired isInvalid={!!error}>
            <FormControlLabel>
              <FormControlLabelText>Email</FormControlLabelText>
            </FormControlLabel>
            <Input>
              <InputField
                type="text"
                placeholder="Enter your email"
                value={formState.email}
                onChangeText={(text) => setFormState((s) => ({ ...s, email: text }))}
                autoCapitalize="none"
                keyboardType="email-address"
              />
            </Input>
            <FormControlHelper>
              <FormControlHelperText>Enter your Tellescope email address</FormControlHelperText>
            </FormControlHelper>
          </FormControl>

          <FormControl isRequired isInvalid={!!error}>
            <FormControlLabel>
              <FormControlLabelText>Password</FormControlLabelText>
            </FormControlLabel>
            <Input>
              <InputField
                type="password"
                placeholder="Enter your password"
                value={formState.password}
                onChangeText={(text) => setFormState((s) => ({ ...s, password: text }))}
                secureTextEntry
              />
            </Input>
            {error && (
              <FormControlError>
                <FormControlErrorText>{error}</FormControlErrorText>
              </FormControlError>
            )}
          </FormControl>

          <Button
            variant="solid"
            onPress={handleLogin}
            isDisabled={isLoading || !formState.email || !formState.password}
          >
            <ButtonText>{isLoading ? "Logging in..." : "Login"}</ButtonText>
          </Button>
        </VStack>
      </VStack>
    </View>
  );
}
