import type { AttendeeInfo, MeetingInfo } from "./ExpoAWSChime.types";
import type { ChimeConfig } from "./utils/config";

export interface CreateMeetingResponse {
  Meeting: MeetingInfo;
  Attendee: AttendeeInfo;
}

export class ChimeAPI {
  private readonly apiUrl: string;
  private readonly region: string;

  constructor(config: ChimeConfig) {
    this.apiUrl = config.apiUrl;
    this.region = config.region;
  }

  async joinMeeting(title: string): Promise<CreateMeetingResponse> {
    const url = encodeURI(`${this.apiUrl}/join?title=${title}&name=Attendee&region=${this.region}`);
    const response = await fetch(url, { method: "POST" });

    if (!response.ok) {
      const responseText = await response.text();
      console.error(
        "Failed to join meeting: status=%d, response=%s",
        response.status,
        responseText,
      );
      throw new Error("Failed to join meeting. The meeting may have ended. Please try again.");
    }

    const data = await response.json();
    // The response structure should be:
    // {
    //   JoinInfo: {
    //     Meeting: {
    //       Meeting: { ... }
    //     },
    //     Attendee: {
    //       Attendee: { ... }
    //     }
    //   }
    // }
    const Meeting = data?.JoinInfo?.Meeting?.Meeting;
    const Attendee = data?.JoinInfo?.Attendee?.Attendee;
    if (!Meeting || !Attendee) {
      console.error("Invalid meeting response:", data);
      throw new Error("Invalid meeting data received from server");
    }
    return { Meeting, Attendee };
  }
}
