export interface ChimeConfig {
  apiUrl: string;
  region: string;
}

export function getChimeConfig(): ChimeConfig {
  const apiUrl = process.env.EXPO_PUBLIC_CHIME_API_URL;
  const region = process.env.EXPO_PUBLIC_CHIME_API_REGION || "us-east-1";

  if (!apiUrl) {
    throw new Error("EXPO_PUBLIC_CHIME_API_URL environment variable is not set");
  }

  return { apiUrl, region };
}
