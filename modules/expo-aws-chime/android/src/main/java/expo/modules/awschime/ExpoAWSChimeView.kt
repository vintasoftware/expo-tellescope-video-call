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
  private val logger = ConsoleLogger(LogLevel.INFO)

  companion object {
    private const val TAG = "ExpoAWSChimeView"
  }

  private val reactContext
    get() = requireNotNull(appContext.reactContext)

  internal val videoView = DefaultVideoRenderView(reactContext).also {
    addView(it)
  }

  fun setTileId(meetingSession: MeetingSession, tileId: Int) {
    meetingSession.audioVideo.bindVideoView(videoView, tileId)
    logger.info(TAG, "Successfully bound video view to tile $tileId")
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    videoView.release()
  }
}
