<!-- 5L0P-F1NG3RPR1NT: AI-assisted Android wrapper scaffold for the user's fork. -->
# Penumbra Tools for Android

This directory adds a small Android wrapper around the existing `antumbra` Rust CLI. The Penumbra core and existing commands are intentionally left unchanged.

## Requirements

- Android Studio with SDK/NDK and Gradle 8.x.
- Rust nightly or the toolchain required by the root workspace.
- `cargo-ndk` and the `aarch64-linux-android` Rust target.
- An Android device with USB host/OTG support.
- Root granted by Magisk/KernelSU if `su` execution is required.

## Build

```bash
rustup target add aarch64-linux-android
cargo install cargo-ndk
./android/build-rust.sh
gradle -p android assembleDebug
```

Install `android/app/build/outputs/apk/debug/app-debug.apk`, connect the MediaTek device through a real OTG host setup, grant USB permission, and grant root when the root manager asks.

The app executes the packaged `antumbra` binary with `su -c`. It does not root a device, unlock a bootloader, bypass Android security, or guarantee compatibility with every MediaTek model. Test flashing/readback commands only on hardware and firmware you understand.

## Architecture

The Android activity handles UI, USB enumeration, and Android permission prompts. The existing Rust CLI remains the device protocol implementation. The first build target is `arm64-v8a`; other ABIs can be added after testing the corresponding Rust/libusb build.
