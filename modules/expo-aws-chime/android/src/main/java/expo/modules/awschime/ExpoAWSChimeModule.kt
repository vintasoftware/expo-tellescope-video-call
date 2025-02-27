package expo.modules.awschime

import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import com.amazonaws.services.chime.sdk.meetings.audiovideo.AudioVideoFacade
import com.amazonaws.services.chime.sdk.meetings.audiovideo.AudioVideoObserver
import com.amazonaws.services.chime.sdk.meetings.audiovideo.SignalUpdate
import com.amazonaws.services.chime.sdk.meetings.audiovideo.VolumeUpdate
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.RemoteVideoSource
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.VideoTileObserver
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.VideoTileState
import com.amazonaws.services.chime.sdk.meetings.realtime.RealtimeObserver
import com.amazonaws.services.chime.sdk.meetings.session.*
import com.amazonaws.services.chime.sdk.meetings.utils.logger.ConsoleLogger
import com.amazonaws.services.chime.sdk.meetings.utils.logger.LogLevel
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.amazonaws.services.chime.sdk.meetings.audiovideo.AttendeeInfo

class ExpoAWSChimeModule : Module() {
  private val logger = ConsoleLogger(LogLevel.INFO)
  private var meetingSession: MeetingSession? = null
  private val scope = CoroutineScope(Dispatchers.Main)

  companion object {
    private const val TAG = "ExpoAWSChimeModule"
  }

  private val reactContext
  get() = requireNotNull(appContext.reactContext)

  private fun defaultUrlRewriter(url: String): String {
    return url
  }

  override fun definition() = ModuleDefinition {
    Name("ExpoAWSChime")

    Events(
      "onMeetingStart",
      "onMeetingEnd",
      "onAttendeesJoin",
      "onAttendeesLeave",
      "onAttendeesMute",
      "onAttendeesUnmute",
      "onAddVideoTile",
      "onRemoveVideoTile",
      "onError"
    )

    AsyncFunction("startMeeting") { meetingInfo: Map<String, Any>, attendeeInfo: Map<String, Any> ->
      try {
        logger.info(TAG, "Starting meeting with info: $meetingInfo and attendee info: $attendeeInfo")

        // Safely extract meeting info with null checks
        val meetingId = meetingInfo["MeetingId"]?.toString()
          ?: throw Exception("MeetingId is required")
        val externalMeetingId = meetingInfo["ExternalMeetingId"]?.toString()
          ?: throw Exception("ExternalMeetingId is required")
        val mediaRegion = meetingInfo["MediaRegion"]?.toString()
          ?: throw Exception("MediaRegion is required")

        // Safely extract media placement with null checks
        val mediaPlacement = meetingInfo["MediaPlacement"] as? Map<String, Any>
          ?: throw Exception("MediaPlacement is required")

        // Extract URLs with null checks
        val audioFallbackUrl = mediaPlacement["AudioFallbackUrl"]?.toString()
          ?: throw Exception("AudioFallbackUrl is required")
        val audioHostUrl = mediaPlacement["AudioHostUrl"]?.toString()
          ?: throw Exception("AudioHostUrl is required")
        val signalingUrl = mediaPlacement["SignalingUrl"]?.toString()
          ?: throw Exception("SignalingUrl is required")
        val turnControlUrl = mediaPlacement["TurnControlUrl"]?.toString()
          ?: throw Exception("TurnControlUrl is required")

        // Safely extract attendee info with null checks
        val attendeeId = attendeeInfo["AttendeeId"]?.toString()
          ?: throw Exception("AttendeeId is required")
        val externalUserId = attendeeInfo["ExternalUserId"]?.toString()
          ?: throw Exception("ExternalUserId is required")
        val joinToken = attendeeInfo["JoinToken"]?.toString()
          ?: throw Exception("JoinToken is required")

        logger.info(TAG, "Creating meeting session")
        val configuration = MeetingSessionConfiguration(
          meetingId,
          MeetingSessionCredentials(attendeeId, externalUserId, joinToken),
          MeetingSessionURLs(audioFallbackUrl, audioHostUrl, turnControlUrl, signalingUrl, ::defaultUrlRewriter)
        )
        meetingSession = DefaultMeetingSession(configuration, logger, reactContext)
        logger.info(TAG, "Meeting session created")

        meetingSession?.audioVideo?.let { audioVideo ->
          // Add audio/video observer
          audioVideo.addAudioVideoObserver(object : AudioVideoObserver {
            override fun onAudioSessionStarted(reconnecting: Boolean) {
              logger.info(TAG, "Audio session started with reconnecting: $reconnecting")
              if (!reconnecting) {
                scope.launch {
                  sendEvent("onMeetingStart", mapOf(
                    "timestamp" to System.currentTimeMillis()
                  ))
                  logger.info(TAG, "Sent onMeetingStart event")
                }
              }
            }

            override fun onAudioSessionStopped(sessionStatus: MeetingSessionStatus) {
              logger.info(TAG, "Audio session stopped with status: ${sessionStatus.statusCode}")
              scope.launch {
                val statusCode = sessionStatus.statusCode?.value ?: 0
                sendEvent("onMeetingEnd", mapOf(
                  "sessionStatus" to statusCode,
                  "timestamp" to System.currentTimeMillis()
                ))
                logger.info(TAG, "Sent onMeetingEnd event")
              }
            }

            override fun onAudioSessionCancelledReconnect() {}
            override fun onConnectionRecovered() {}
            override fun onConnectionBecamePoor() {}
            override fun onVideoSessionStarted(sessionStatus: MeetingSessionStatus) {}
            override fun onVideoSessionStopped(sessionStatus: MeetingSessionStatus) {}
            override fun onAudioSessionDropped() {}
            override fun onAudioSessionStartedConnecting(reconnecting: Boolean) {}
            override fun onCameraSendAvailabilityUpdated(available: Boolean) {}
            override fun onRemoteVideoSourceAvailable(sources: List<RemoteVideoSource>) {}
            override fun onRemoteVideoSourceUnavailable(sources: List<RemoteVideoSource>) {}
            override fun onVideoSessionStartedConnecting() {}
          })

          // Add realtime observer for attendee events
          audioVideo.addRealtimeObserver(object : RealtimeObserver {
            override fun onVolumeChanged(volumeUpdates: Array<VolumeUpdate>) {}
            override fun onSignalStrengthChanged(signalUpdates: Array<SignalUpdate>) {}

            override fun onAttendeesJoined(attendeeInfo: Array<AttendeeInfo>) {
              scope.launch {
                val attendeeIds = attendeeInfo.map { it.attendeeId }
                val externalUserIds = attendeeInfo.map { it.externalUserId }
                sendEvent("onAttendeesJoin", mapOf(
                  "attendeeIds" to attendeeIds,
                  "externalUserIds" to externalUserIds
                ))
                logger.info(TAG, "Sent onAttendeesJoin event")
              }
            }

            override fun onAttendeesLeft(attendeeInfo: Array<AttendeeInfo>) {
              scope.launch {
                val attendeeIds = attendeeInfo.map { it.attendeeId }
                val externalUserIds = attendeeInfo.map { it.externalUserId }
                sendEvent("onAttendeesLeave", mapOf(
                  "attendeeIds" to attendeeIds,
                  "externalUserIds" to externalUserIds
                ))
                logger.info(TAG, "Sent onAttendeesLeave event")
              }
            }

            override fun onAttendeesDropped(attendeeInfo: Array<AttendeeInfo>) {
              scope.launch {
                val attendeeIds = attendeeInfo.map { it.attendeeId }
                val externalUserIds = attendeeInfo.map { it.externalUserId }
                sendEvent("onAttendeesLeave", mapOf(
                  "attendeeIds" to attendeeIds,
                  "externalUserIds" to externalUserIds
                ))
                logger.info(TAG, "Sent onAttendeesLeave event for dropped attendees")
              }
            }

            override fun onAttendeesMuted(attendeeInfo: Array<AttendeeInfo>) {
              scope.launch {
                val attendeeIds = attendeeInfo.map { it.attendeeId }
                sendEvent("onAttendeesMute", mapOf("attendeeIds" to attendeeIds))
                logger.info(TAG, "Sent onAttendeesMute event")
              }
            }

            override fun onAttendeesUnmuted(attendeeInfo: Array<AttendeeInfo>) {
              scope.launch {
                val attendeeIds = attendeeInfo.map { it.attendeeId }
                sendEvent("onAttendeesUnmute", mapOf("attendeeIds" to attendeeIds))
                logger.info(TAG, "Sent onAttendeesUnmute event")
              }
            }
          })

          // Add video tile observer
          audioVideo.addVideoTileObserver(object : VideoTileObserver {
            override fun onVideoTileAdded(tileState: VideoTileState) {
              scope.launch {
                sendEvent(
                  "onAddVideoTile",
                  mapOf(
                    "tileId" to tileState.tileId,
                    "attendeeId" to tileState.attendeeId,
                    "isLocal" to tileState.isLocalTile,
                    "isScreenShare" to tileState.isContent,
                    "pauseState" to tileState.pauseState.ordinal,
                    "videoStreamContentHeight" to tileState.videoStreamContentHeight,
                    "videoStreamContentWidth" to tileState.videoStreamContentWidth
                  )
                )
                logger.info(TAG, "Sent onAddVideoTile event")
              }
            }

            override fun onVideoTileRemoved(tileState: VideoTileState) {
              scope.launch {
                sendEvent(
                  "onRemoveVideoTile",
                  mapOf(
                    "tileId" to tileState.tileId,
                    "attendeeId" to tileState.attendeeId,
                    "isLocal" to tileState.isLocalTile,
                    "isScreenShare" to tileState.isContent
                  )
                )
                logger.info(TAG, "Sent onRemoveVideoTile event")
              }
            }

            override fun onVideoTilePaused(tileState: VideoTileState) {}
            override fun onVideoTileResumed(tileState: VideoTileState) {}
            override fun onVideoTileSizeChanged(tileState: VideoTileState) {}
          })

          // Start audio and video
          logger.info(TAG, "Starting audio and video")
          audioVideo.start()
          audioVideo.startRemoteVideo()
          logger.info(TAG, "Audio and video started")

          // Force audio to route through speaker
          logger.info(TAG, "Forcing audio to route through speaker")
          val audioManager = reactContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
          audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
          audioManager.isSpeakerphoneOn = true
          logger.info(TAG, "Audio routed through speaker")
        }

        logger.info(TAG, "Meeting started successfully")
      } catch (e: Exception) {
        logger.error(TAG, "Error starting meeting: ${e.message}")
        scope.launch {
          sendEvent("onError", mapOf("error" to e.message))
        }
        throw e
      }
    }

    AsyncFunction("stopMeeting") {
      // Reset audio routing when stopping the meeting
      logger.info(TAG, "Stopping meeting")
      val audioManager = reactContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
      audioManager.mode = AudioManager.MODE_NORMAL
      audioManager.isSpeakerphoneOn = false

      // Code below addresses the SIGSEV error:
      // libamazon_chime_media_client.so (offset 0x1450000) (Java_com_xodee_client_video_VideoClient_doSetSending+100
      // that occurs when joining again a meeting right after stopping it.
      // The issue is probably caused by DefaultVideoClientController::stopAndDestroy
      // that conflicts with DefaultVideoClientController::startLocalVideo

      // Create a deferred that will be completed when cleanup is done
      val cleanupCompleted = kotlinx.coroutines.CompletableDeferred<Unit>()

      // Add a temporary observer to wait for video session to stop
      meetingSession?.audioVideo?.addAudioVideoObserver(object : AudioVideoObserver {
        override fun onVideoSessionStopped(sessionStatus: MeetingSessionStatus) {
          logger.info(TAG, "Video session stopped with status: ${sessionStatus.statusCode}")
          meetingSession = null
          logger.info(TAG, "Meeting stopped and cleaned up")
          cleanupCompleted.complete(Unit)
        }

        // Implement other required methods with empty bodies
        override fun onAudioSessionStarted(reconnecting: Boolean) {}
        override fun onAudioSessionStopped(sessionStatus: MeetingSessionStatus) {}
        override fun onAudioSessionCancelledReconnect() {}
        override fun onConnectionRecovered() {}
        override fun onConnectionBecamePoor() {}
        override fun onVideoSessionStarted(sessionStatus: MeetingSessionStatus) {}
        override fun onAudioSessionDropped() {}
        override fun onAudioSessionStartedConnecting(reconnecting: Boolean) {}
        override fun onCameraSendAvailabilityUpdated(available: Boolean) {}
        override fun onRemoteVideoSourceAvailable(sources: List<RemoteVideoSource>) {}
        override fun onRemoteVideoSourceUnavailable(sources: List<RemoteVideoSource>) {}
        override fun onVideoSessionStartedConnecting() {}
      })

      try {
        // First stop local video if it's running
        meetingSession?.audioVideo?.stopLocalVideo()

        // Then stop the meeting session which will trigger stopAndDestroy
        meetingSession?.audioVideo?.stop()

        // Wait for cleanup to complete with a timeout
        kotlinx.coroutines.runBlocking {
          kotlinx.coroutines.withTimeout(5000) {
            cleanupCompleted.await()
          }
        }
      } catch (e: Exception) {
        logger.error(TAG, "Error stopping meeting: ${e.message}")
        scope.launch {
          sendEvent("onError", mapOf("error" to e.message))
        }
      }

      // Clean the meeting session
      meetingSession = null

      return@AsyncFunction null
    }

    AsyncFunction("mute") {
      logger.info(TAG, "Muting local audio")
      meetingSession?.audioVideo?.realtimeLocalMute()
      logger.info(TAG, "Local audio muted")
      return@AsyncFunction null
    }

    AsyncFunction("unmute") {
      logger.info(TAG, "Unmuting local audio")
      meetingSession?.audioVideo?.realtimeLocalUnmute()
      logger.info(TAG, "Local audio unmuted")
      return@AsyncFunction null
    }

    AsyncFunction("startLocalVideo") {
      logger.info(TAG, "Starting local video")
      meetingSession?.audioVideo?.startLocalVideo()
      logger.info(TAG, "Local video started")
      return@AsyncFunction null
    }

    AsyncFunction("stopLocalVideo") {
      logger.info(TAG, "Stopping local video")
      meetingSession?.audioVideo?.stopLocalVideo()
      logger.info(TAG, "Local video stopped")
      return@AsyncFunction null
    }

    View(ExpoAWSChimeView::class) {
      Prop("tileId") { view: ExpoAWSChimeView, tileId: Int ->
        logger.info(TAG, "Binding tileId: $tileId")
        meetingSession?.let { session ->
          view.setTileId(session, tileId)
          logger.info(TAG, "tileId $tileId bound")
        }
      }

      Prop("isLocal") { view: ExpoAWSChimeView, isLocal: Boolean ->
        logger.info(TAG, "Setting isLocal: $isLocal")
        view.setIsLocal(isLocal)
        logger.info(TAG, "isLocal set to $isLocal")
      }

      OnViewDestroys {
        meetingSession?.let { session ->
          val tileId = it.tileId
          logger.info(TAG, "Unbinding tileId: $tileId")
          it.unsetTileId(session)
          logger.info(TAG, "tileId $tileId unbound")
        }
      }
    }
  }
}
