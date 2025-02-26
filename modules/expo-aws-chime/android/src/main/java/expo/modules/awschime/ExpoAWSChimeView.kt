package expo.modules.awschime

import android.content.Context
import android.view.ViewGroup
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.DefaultVideoRenderView
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.gl.EglCoreFactory
import com.amazonaws.services.chime.sdk.meetings.audiovideo.video.gl.DefaultEglCoreFactory
import com.amazonaws.services.chime.sdk.meetings.session.MeetingSession
import expo.modules.kotlin.AppContext
import expo.modules.kotlin.views.ExpoView
import com.amazonaws.services.chime.sdk.meetings.utils.logger.ConsoleLogger
import com.amazonaws.services.chime.sdk.meetings.utils.logger.LogLevel

class ExpoAWSChimeView(context: Context, appContext: AppContext) : ExpoView(context, appContext) {
  var tileId: Int? = null
  private val logger = ConsoleLogger(LogLevel.INFO)

  companion object {
    private const val TAG = "ExpoAWSChimeView"
  }

  private val reactContext
    get() = requireNotNull(appContext.reactContext)

  internal val videoView = DefaultVideoRenderView(reactContext).also {
    logger.info(TAG, "Creating video view for ExpoAWSChimeView")
    addView(
      it,
      ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
      )
    )
    logger.info(TAG, "Video view created and added to ExpoAWSChimeView")
  }

  fun setTileId(meetingSession: MeetingSession, tileId: Int) {
    logger.info(TAG, "Binding video view to tile $tileId")
    this.tileId = tileId
    meetingSession.audioVideo.bindVideoView(videoView, tileId)
    logger.info(TAG, "Successfully bound video view to tile $tileId")
  }

  fun unsetTileId(meetingSession: MeetingSession) {
    val currentTileId = tileId
    if (currentTileId != null) {
      logger.info(TAG, "Unbinding video view from tile $currentTileId")
      meetingSession.audioVideo.unbindVideoView(currentTileId)
      this.tileId = null
      logger.info(TAG, "Successfully unbound video view from tile $currentTileId")
    }
  }
}
