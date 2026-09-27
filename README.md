# POS Test App

Aplicativo Android em Kotlin para validar funções básicas de um terminal POS Android.

## Testes incluídos

- Exibição de fabricante, modelo e versão do Android
- Beep usando `ToneGenerator`
- Vibração usando `Vibrator`
- Geração de uma prévia de cupom pelo Android Print Framework

## Executar

1. Abra o repositório no Android Studio atualizado.
2. Aguarde a sincronização do Gradle.
3. Ative a depuração USB no POS e conecte-o ao computador, ou use um emulador.
4. Execute a configuração `app`.

Também é possível gerar o APK com:

```bash
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

O Print Framework é uma implementação genérica. Impressoras térmicas integradas ou ESC/POS Bluetooth normalmente exigem o SDK específico do fabricante.
