import os
import sys
import subprocess
import platform

VERSIONS = [
    "1.20.1",
    "1.20.4",
    "1.20.6",
    "1.21.4",
    "1.21.1",
    "1.21.8",
    "1.21.11",
    "26.1.2",
    "26.2"
]
PROPERTY_NAME = "active_mc_version"
# DATAGEN = False

def run_command(command, error_message):
    print(f"\n>>> Executing: {' '.join(command)}")
    result = subprocess.run(command)
    if result.returncode != 0:
        print(f"\n❌ ERROR: {error_message}")
        sys.exit(result.returncode)

def main():
    print("🚀 Starting Automated Multi-Version Build...")

    is_windows = platform.system().lower() == "windows"
    gradlew = "gradlew.bat" if is_windows else "./gradlew"

    for version in VERSIONS:
        print(f"\n" + "="*50)
        print(f"🔨 BUILDING VERSION: {version}")
        print("="*50)

        gradle_args = [
            gradlew,
            "build",
            f"-P{PROPERTY_NAME}={version}"
        ]

        run_command(gradle_args, f"Build failed for version {version}!")

    print("\n✅ All versions built successfully! Check your build/libs folders.")

if __name__ == "__main__":
    main()