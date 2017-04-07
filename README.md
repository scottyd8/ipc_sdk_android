# android-sdk

## Project Structure
- All modules are contained within the SDK project
- The `library` module contains the source code for the artifacts that will be distributed to other developers for integration purposes.
- The `miuraSdk` module contains the source code for the Miura PED driver.
- The `testHarness` module consists of two sub-modules. `miuratestapp` and `sdktestapp`
    - `miuratestapp` contains the test application for the Miura PED driver.
    - `sdktestapp` contains the test application for the EMV SDK
- Both the `miuraSdk` and `library` modules publish the generated binaries to the `artifact` directory.

## Getting started
- Before attempting to run the `sdktestapp` you must first create a `creds.gradle` file at `SDK/testHarness/sdktestapp/creds.gradle`. It should be structured as below:
```groovy
ext.SECURENET_ID = '"SecureNetIdGoesHere"';
ext.SECURENET_KEY = '"SecureNetKeyGoesHere"';

ext.MERCHANTPARTNERS_ID = '"MerchantPartnersIdGoesHere"';
ext.MERCHANTPARTNERS_PIN = '"MerchantPartnersPinGoesHere"';

ext.DEVELOPER_ID = '"AssignedDeveloperIdGoesHere"'
ext.APP_ID = '"AssignedApplicationIdGoesHere"'
```
