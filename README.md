# virtual e-bike with BLE

작업기간 24.9.2 ~ 9.20

> BLE(Bluetooth Low Energy)를 사용해서 스마트폰을 이용한 가상 자전거 만들기 project
- 스마트폰을 Bluetooth Server로 만들어 센서의 역할을 하게 한다.

https://github.com/user-attachments/assets/65abf589-f4b0-445d-a499-92f902e39b25





## Device
![Frame 5](https://github.com/user-attachments/assets/d80b16a4-7146-49cf-aa63-deb86b392aca)
- 안드로이드 스마트폰 기기 2개가 필요하다.
- 앱을 설치한 기기가 센서의 역할을 하며 정보를 생성하고 전달하는 `server`가 된다.
- 앱을 설치하지 않은 기기는 `Server`로 부터 정보를 받아오는 `client` 기기가 된다.

## Scenario

1. 블루투스 기기 광고를 한다. (advertising)
2. 블루투스 연결을 당한다. (connect)
3. app이 설치된 기기(`server`)에서 연결당한 `client` 기기에게 정보를 전송한다. (READ, WRITE, NOTIFY)
    - `BluetoothGattServerCallback` 안에 있는 `onConnectionStateChange`를 통해 연결 감지, 연결된 `client` 기기 정보(Address)를 불러온다.
    - `onCharacteristicReadRequest`를 통해 연결된 `client` 기기에게 정보를 전송한다.

## Package
```
com.eddy.mockebike
├── bluetooth
│   └── ...
├── presentation
│   ├── ui
│   │   ├── component
│   │   │   └── ...
│   │   ├── theme
│   │   │   └── ...
│   │   ├── BikeScreen
│   │   ├── BikeUiState
│   │   └── BikeViewModel
│   └── MainActivcity
└──── utils
```

## Trouble Shooting

### 1. Bluetooth 관심사 분리
> 블루투스 서비스가 거대해지는 것을 막기 위해서 단일 책임원칙에 따른 관심사 분리를 했습니다.

0. BluetoothService:
블루투스를 다루기 위한 것을 담당하고, 아래 xxxManager를 통해서 실제 작업을 수행합니다.

1. AdvertisingManager:
블루투스 LE 광고를 시작하고 중지하는 책임을 갖습니다.
startAdvertising(), stopAdvertising() 메서드를 포함합니다.

2. GattServerManager:
GATT 서버를 시작하고 관리하는 책임을 갖습니다.
startServer(), stopServer() 메서드와 GATT 서버 콜백을 포함합니다.
등록된 디바이스 목록을 관리합니다.

3. HeartRateNotificationManager:
심박수(이 경우 거리, 속도, 기어 정보) 알림을 관리합니다.
startNotifications(), stopNotifications() 메서드를 포함합니다.
주기적으로 알림을 보내는 로직을 처리합니다.

4. BluetoothStateReceiver:
블루투스 상태 변경을 감지하고 적절한 작업을 수행합니다.
register(), unregister() 메서드를 포함하여 리시버를 등록/해제합니다.

5. BluetoothGattServiceBuilder
블루투스 생성 과정을 빌더로 만든 클래스입니다.  
Bluetooth service, characteristic 등의 정보를 받아서 서비스를 생성합니다. 

### 2. Foreground service
🍀 **요구사항: 기기를 잠금하고 다시 돌아온 상태나 포커싱이 없는 상태에서도 subscriber에게 ble notification이 가도록 하라.**
> BluetoothService가 안드로이드 4대 컴포넌트인 `Service`를 상속받도록 수정하였습니다.  
> 포그라운드 서비스로 지정함으로써 사용자가 앱과 사용작용하지 않을 때도 계속 실행됩니다.
- Service는 `startForeground`를 통해 Foreground 서비스 시작
```
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification() // Notification 생성
        startForeground(1, notification) // Foreground 서비스 시작

        return START_STICKY // 서비스가 중단되었을 경우 자동으로 다시 시작
    }
```
### 3. 동적으로 애니메이션 속도 조정
🍀 **요구사항: uiState의 Speed가 변경됨에 따라 자전거 애니메이션의 속도가 바뀌도록 하여라.**
- 초안
   - rememberInfiniteTransition으로는 동적으로 속도를 조정할 수 없다.
   - durationMillis 파라미터는 동적으로 값의 변화를 인지하지 못하는 문제 발생.
```kotlin
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val currentFrame by infiniteTransition.animateValue(
        initialValue = 0,
        targetValue = frameCount - 1,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (frameCount * baseDuration / speed).toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )
```

- 해결 버전
  - speed는 항상 관찰이 되지만, 현재 프레임의 frameDuration
  - LaunchedEffect는 Compose에서 사이드 이펙트를 실행하기 위해 사용됩니다. 특정 키(speed)가 변경될 때마다 새로운 코루틴을 시작합니다. speed 값이 변경될 때마다 애니메이션 루프를 다시 시작하려고 시도합니다.
  - currentFrame.value를 1씩 증가시키며, frameCount로 나눈 나머지를 취하여 루프를 만듭니다.
  - 각 프레임이 표시되는 시간을 frameDuration만큼 지연시킵니다.


```kotlin
    // speed에 따른 frameDuration 계산

    val frameDuration = if (speed > 0) {
        (baseDuration * 3 / (speed / 3).coerceIn(1f, 9f)).toLong()
    } else {
        Long.MAX_VALUE // 매우 큰 값으로 설정하여 애니메이션 정지
    }

    // 현재 프레임 상태
    val currentFrame = remember { mutableStateOf(0) }

    // 애니메이션 업데이트를 위한 LaunchedEffect
    LaunchedEffect(speed) { // speed를 키로 사용
        while (speed > 0) { // speed가 0이 아닌 동안만 루프 실행
            // 프레임 업데이트
            currentFrame.value = (currentFrame.value + 1) % frameCount
            // 속도에 따른 프레임 지속시간 조정
            delay(frameDuration)

            Log.d("laun", "${currentFrame.value} $speed $frameDuration")
        }
    }

    // 애니메이션을 정지시키는 빈 이미지 또는 투명 이미지 설정
    val displayImage = if (speed > 0) {
        images[currentFrame.value]
    } else {
        painterResource(id = R.drawable.out_001)
    }

    Image(
        painter = displayImage,
        contentDescription = null,
        modifier = modifier
    )
```

## Library
| 이름 | 목적| 
|---|---|
| Bluetooth | Bluetooth Low Energy(BLE) 통신을 관리. [BLE 광고](https://developer.android.com/reference/android/bluetooth/le/BluetoothLeAdvertiser) 및 [GATT 서버](https://developer.android.com/reference/android/bluetooth/BluetoothGattServer) 관리에 사용
| Jetpack Compose | 선언적 UI 빌드 및 UI 상태 관리를 위한 라이브러리 |
| Timber | 로그 관리를 위한 라이브러리. 더 간결하고 유연한 로깅을 제공 |

## Architecture
### 안드로이드 권장 앱 아키텍쳐 

> View와 ViewModel의 구조로 관심사 분리를 합니다.
- UI -> **event** -> ViewModel
- ViewModel -> **uiState** -> UI

<img width="507" alt="image" src="https://github.com/user-attachments/assets/1efb3af7-4310-4f0f-85ac-466fcfc98218">

참고: [ViewModel and State in Compose](https://developer.android.com/codelabs/basic-android-kotlin-compose-viewmodel-and-state?hl=ko#4)
