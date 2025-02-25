import { EventSubscription, requireNativeModule } from "expo-modules-core";

import type {
  AttendeeInfo,
  ChimeEventCallback,
  ChimeEventName,
  MeetingInfo,
} from "./ExpoAWSChime.types";

interface ExpoAWSChimeModuleType {
  startMeeting(meetingInfo: MeetingInfo, attendeeInfo: AttendeeInfo): Promise<string>;
  stopMeeting(): Promise<string>;
  mute(): Promise<string>;
  unmute(): Promise<string>;
  startLocalVideo(): Promise<string>;
  stopLocalVideo(): Promise<string>;
  addListener<K extends ChimeEventName>(
    event: K,
    callback: ChimeEventCallback<K>,
  ): EventSubscription;
}

const ExpoAWSChimeModule = requireNativeModule<ExpoAWSChimeModuleType>("ExpoAWSChime");

export async function startMeeting(
  meetingInfo: MeetingInfo,
  attendeeInfo: AttendeeInfo,
): Promise<string> {
  return await ExpoAWSChimeModule.startMeeting(meetingInfo, attendeeInfo);
}

export async function stopMeeting(): Promise<string> {
  return await ExpoAWSChimeModule.stopMeeting();
}

export async function mute(): Promise<string> {
  return await ExpoAWSChimeModule.mute();
}

export async function unmute(): Promise<string> {
  return await ExpoAWSChimeModule.unmute();
}

export async function startLocalVideo(): Promise<string> {
  return await ExpoAWSChimeModule.startLocalVideo();
}

export async function stopLocalVideo(): Promise<string> {
  return await ExpoAWSChimeModule.stopLocalVideo();
}

export function addListener<K extends ChimeEventName>(event: K, callback: ChimeEventCallback<K>) {
  return ExpoAWSChimeModule.addListener(event, callback);
}
