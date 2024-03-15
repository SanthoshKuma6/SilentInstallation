# MDM/MAM Silent Installation 

## Overview
The Silent installation refers to the process of installing applications without user interaction, providing a discreet and automated experience. On the other hand, silent uninstallation involves the removal of applications without user intervention.

## Triggering Actions on Device Boot
It is commonly used to trigger actions after the device has completed the boot process. The command you provided is using the am broadcast command to send the broadcast intent.

![Screenshot 2024-03-04 at 3 33 24 PM](https://git.t3.daimlertruck.com/storage/user/16950/files/a4847247-cd27-4763-b41f-ce3a9615ac6c)

#### am: 
Activity Manager command-line tool for Android system interaction.

#### broadcast: 
Command to send a broadcast intent.

#### -a android.intent.action.BOOT_COMPLETED: 
Sets the intent action to "BOOT_COMPLETED".

#### -n com.daimler.silentinstallation/.service.BootCompleteService:
Specifies the component to receive the broadcast; it's a service named BootCompleteService in the package com.daimler.silentinstallation.
When the device boots up, the system sends out a "BOOT_COMPLETED" broadcast intent. If you've registered a receiver for this intent in your AndroidManifest.xml


### Here's an example of how you might register a receiver in the AndroidManifest.xml:

![Screenshot 2024-03-04 at 3 33 41 PM](https://git.t3.daimlertruck.com/storage/user/16950/files/92b6e658-fbca-410e-bd7d-60739b2ad8d5)

![Screenshot 2024-03-04 at 3 33 53 PM](https://git.t3.daimlertruck.com/storage/user/16950/files/881587a8-31f3-4009-b847-383e73face01)


## Boot Receiver for Device Initialisation
This Kotlin class, BootCompleteService, extends BroadcastReceiver and is responsible for handling the "BOOT_COMPLETED" broadcast intent. Upon receiving the intent, it initiates the ForegroundService, retrieves the device Id, and logs relevant information.

![Screenshot 2024-03-04 at 3 34 48 PM](https://git.t3.daimlertruck.com/storage/user/16950/files/4a80a915-e17d-49c6-9eb1-f0cb1fe119cf)


## Foreground Service for App Package Handling
The ForegroundService class, extending Service, initiates a foreground service for app package operations, utilizing Executors.newSingleThreadScheduledExecutor() to create a single- threaded executor that periodically runs the appPackageImpl.appPackageDetail() function every 60 seconds.

![Screenshot 2024-03-04 at 3 35 00 PM](https://git.t3.daimlertruck.com/storage/user/16950/files/41e281b1-9d62-41ba-bb5a-cd9bfbbcce2a)


## App Package Detail Handler
The AppPackageDetailImpl class is designed to handle the appPackageDetail API service. The appPackageDetail function manages the installation and uninstallation of application packages on an Android device based on information fetched from an external API. It handles asynchronous execution, error handling, and dispatches tasks for silent installation or uninstallation based on package details.

![Screenshot 2024-03-04 at 3 41 12 PM](https://git.t3.daimlertruck.com/storage/user/16950/files/1cc80b59-92da-4477-960a-3db77b304297)


## Package Installation Handler
The installPackage function is designed to handle the installation of a package from an InputStream. It utilizes the PackageInstaller to create a session, write the package content, and commit the installation.

![Screenshot 2024-03-04 at 3 41 26 PM](https://git.t3.daimlertruck.com/storage/user/16950/files/b8dc2fb1-c22c-4935-ae86-56d049a43079)


### Here's a brief description:
This code snippet demonstrates a package installation handler. You need to create an instance and call the installPackage function with the appropriate parameters.

### Demo Video
Link: http://bit.ly/3Oj5pBd
