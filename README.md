# Expo Tellescope Video Calls Demo (Alpha)

⚠️ **Under heavy development**

<img src="https://github.com/user-attachments/assets/b96f9a6d-8113-4a60-b5a0-cfb0afa39b05" alt="Expo Chime Demo screenshot" width="200" />

Telehealth video calls in Expo/React Native using [Tellescope](https://tellescope.com/).

This project is a demonstration of how to implement a mobile app for telehealth video calls using [Tellescope](https://tellescope.com/), which are powered by [AWS Chime](https://aws.amazon.com/chime/). Instead of using the outdated [amazon-chime-react-native-demo](https://github.com/aws-samples/amazon-chime-react-native-demo/), this projects integrates with the AWS Chime SDK using the new [Expo Modules API](https://docs.expo.dev/versions/latest/sdk/modules/).

## Supported Platforms

- Android
- iOS (coming soon)

## Features

- Real-time video conferencing capabilities
- Audio/video controls (mute/unmute, video on/off)
- Multi-participant video grid with dynamic layout
- Local video preview
- Permissions handling for camera and microphone

## Technical Details
- Custom Expo native module (`expo-aws-chime`)
- Integrated with Tellescope SDK in a headless manner
- Tailwind CSS for styling with NativeWind
- gluestack-ui v2 components for UI elements

## Getting Started

### Prerequisites
- [Node.js](https://nodejs.org/)
- [Expo CLI](https://docs.expo.dev/get-started/installation/)

### Installation

1. Clone the repository:
```bash
git clone https://github.com/vintasoftware/expo-chime-demo.git
cd expo-chime-demo
```

2. Install dependencies:
```bash
npm install
```

3. Create an Patient (End User) in Tellescope staging env: https://staging.tellescope.com/endusers. This is the user that you'll start the video call with from the mobile app.

4. Configure the environment variables:
    1. Create the .env.local file in the root directory:
    ```bash
    cp .env.local.example .env.local
    ```
    2. Adapt to your values:
    ```
    EXPO_PUBLIC_TELLESCOPE_API_BASE_URL=https://staging-api.tellescope.com
    EXPO_PUBLIC_TELLESCOPE_USER_EMAIL=<your-tellescope-email>
    EXPO_PUBLIC_TELLESCOPE_USER_PASSWORD=<your-tellescope-password>
    EXPO_PUBLIC_TELLESCOPE_ENDUSER_ID=<check-readme>
    ```

### Running the App

Currently only Android is supported. AWS Chime SDK doesn't support x86 emulators, so you (probably) need to use a physical device:

1. Open Android Studio and connect your device. The easiest way is to pair your device with Wi-Fi using the ["Pair Devices Using Wi-Fi" option](https://developer.android.com/studio/run/device#wireless).


2. Run the app with:

```bash
npx expo run:android
```

3. Check your physical mobile device

4. Log in in Tellescope

5. Start a video call, it will create a Calendar Event and start a video call on Tellescope with the user specified as `EXPO_PUBLIC_TELLESCOPE_ENDUSER_ID`.

6. In your PC, open the Patient page for the end user you've just created on the Installation step. The URL will be like: https://staging.tellescope.com/endusers/<enduser-id>

7. Impersonate the Patient by clicking this icon:

![Image](https://github.com/user-attachments/assets/03b0f542-f2b3-4a60-b0d5-e5308f10b79e)

8. Look for the video call at My Events and join it.

## Project Structure

- `/app` - Main application code using Expo Router for file-based routing
- `/components` - Reusable React components
- `/components/ui` - gluestack-ui v2 components
- `/modules/expo-aws-chime` - AWS Chime SDK native module implementation
- `/assets` - Static assets like images and fonts

## Native Module Implementation

The AWS Chime SDK is integrated as a custom Expo native module using Expo Modules API in the `/modules/expo-aws-chime` directory. The module provides:

- Native bridge to AWS Chime SDK for Android
- React hooks for easy integration (`useChimeMeeting`)
- Context provider for state management (`ChimeMeetingProvider`)
- Native view component for rendering video tiles (`ExpoAWSChimeView`)

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the `LICENSE.txt` file for details.

## Commercial Support

[![alt text](https://avatars2.githubusercontent.com/u/5529080?s=80&v=4 "Vinta Logo")](https://www.vintasoftware.com/)

This is an open-source project maintained by [Vinta Software](https://www.vinta.com.br/). We are always looking for exciting work! If you need any commercial support, feel free to get in touch: contact@vinta.com.br
