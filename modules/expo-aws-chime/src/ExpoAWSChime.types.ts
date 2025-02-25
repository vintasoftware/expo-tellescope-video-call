export type MeetingInfo = {
  MeetingId: string;
  ExternalMeetingId: string;
  MediaRegion: string;
  MediaPlacement: {
    AudioFallbackUrl: string;
    AudioHostUrl: string;
    SignalingUrl: string;
    TurnControlUrl: string;
  };
};

export type AttendeeInfo = {
  AttendeeId: string;
  ExternalUserId: string;
  JoinToken: string;
};

export type VideoTileEvent = {
  tileId: number;
  attendeeId: string;
  isLocal: boolean;
  isScreenShare: boolean;
  pauseState?: number;
  videoStreamContentHeight?: number;
  videoStreamContentWidth?: number;
};

export type ChimeEventMap = {
  onMeetingStart: {
    timestamp: number;
  };
  onMeetingEnd: {
    sessionStatus: number;
    timestamp: number;
  };
  onAttendeesJoin: {
    attendeeIds: string[];
    externalUserIds: string[];
  };
  onAttendeesLeave: {
    attendeeIds: string[];
    externalUserIds: string[];
  };
  onAttendeesMute: {
    attendeeIds: string[];
  };
  onAttendeesUnmute: {
    attendeeIds: string[];
  };
  onAddVideoTile: VideoTileEvent;
  onRemoveVideoTile: {
    tileId: number;
    attendeeId: string;
    isLocal: boolean;
    isScreenShare: boolean;
  };
  onError: {
    error: string;
  };
};

export type ChimeEventName = keyof ChimeEventMap;
export type ChimeEventCallback<K extends ChimeEventName> = (params: ChimeEventMap[K]) => void;
