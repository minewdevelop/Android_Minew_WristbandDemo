# ChangeLog

This refers to the changelog of WristbandKit SDK

## v1.0.0

1. startScan(): Start scanning. Each start scan will clear all the results of the last scan;
2. stopScan(): stop scanning;
3. setAwakenTime(long timeMillis): Set the wake-up time, the default is 10 seconds, the setting will take effect before `startAwaken(String macAddress)` is called;
4. getAwakenTime(): Get the wake-up duration previously set;
5. startAwaken(String macAddress): Start to wake up the device;
6. stopAwaken(): Stop to wake up;
7. isAwakened(): Whether to wake up the device;
8. connect(Context context, WristbandModule module): connect the device;
9. disConnect(String macAddress): Disconnect;
10. setOnConnStateListener(OnConnStateListener listener): Set the connection state, this must be called;
11. sendPassword(String macAddress, String password): Write password;