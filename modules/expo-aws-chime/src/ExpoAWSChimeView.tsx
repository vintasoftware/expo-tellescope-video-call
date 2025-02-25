import { requireNativeViewManager } from "expo-modules-core";
import * as React from "react";
import { View, ViewProps } from "react-native";

export type ExpoAWSChimeViewProps = {
  tileId: number;
} & ViewProps;

const NativeView: React.ComponentType<ExpoAWSChimeViewProps> =
  requireNativeViewManager("ExpoAWSChime");

export default function ExpoAWSChimeView(props: ExpoAWSChimeViewProps) {
  const { tileId, ...rest } = props;
  return (
    <View {...rest}>
      <NativeView style={{ width: "100%", height: "100%" }} tileId={tileId} />
    </View>
  );
}
