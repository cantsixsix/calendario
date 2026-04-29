# Calendario

App Android nativo em Java para calendario mensal com eventos locais.

## Abrir no Android Studio

1. Abra o Android Studio no Windows.
2. Escolha `Open` e selecione esta pasta: `calendario`.
3. Aguarde o Gradle Sync baixar o Android Gradle Plugin e o SDK necessario.
4. Rode o app em um emulador ou celular conectado usando `Run`.

## Testes

No Android Studio:

- Clique com o botao direito em `app/src/test/java`.
- Escolha `Run 'Tests in ...'`.

Ou pelo terminal do Android Studio:

```bash
gradle testDebugUnitTest
```

No Windows PowerShell:

```powershell
gradle testDebugUnitTest
```

Se o Android Studio gerar o Gradle Wrapper para o projeto, use `.\gradlew.bat testDebugUnitTest`
no lugar de `gradle testDebugUnitTest`.

## Gerar APK instalavel

No Android Studio:

- `Build > Build Bundle(s) / APK(s) > Build APK(s)`

O APK debug fica em:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Para instalar em um celular conectado:

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Gerar APK pelo GitHub

Depois de subir o projeto para o GitHub:

1. Abra o repositorio no GitHub.
2. Entre em `Actions`.
3. Abra o workflow `Android`.
4. Clique em `Run workflow`.
5. Quando terminar, baixe o artefato `calendario-debug-apk`.

O arquivo dentro do artefato e o APK instalavel `app-debug.apk`.

## O que ja existe

- Grade mensal com inicio na segunda-feira.
- Navegacao entre meses.
- Selecao de dia.
- Cadastro de eventos por data.
- Persistencia local com `SharedPreferences`.
- Testes unitarios para regras de calendario e serializacao de eventos.
