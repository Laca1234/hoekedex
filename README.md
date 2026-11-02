# Hoekedex Android

Offline, dark mode only Android profilkezelő alkalmazás a V3 tervezet alapján.

## Fő funkciók

- Teljesen offline működés
- Helyi SQLite adatbázis
- Képek mentése az app privát tárhelyére
- Dark/glassmorphism Compose UI
- Kliens által megadott dark mode paletta használata:
  `#4A0006`, `#311414`, `#181314`, `#4A3A3A`, `#8C5A5E`, `#A86A45`
- Lebegő keresősáv, amely görgetéskor eltűnik és visszacsúszik
- Profil grid 2-3 oszlopos adaptív elrendezéssel
- Profil létrehozás, megnyitás, szerkesztés, törlés
- Név alapú keresés
- Kombinálható szűrők: életkor, rating, nemzetiség
- Galéria több képpel, az első kép automatikus thumbnailként
- Képek megtekintése teljes képes nézetben

## Build

1. Nyisd meg a `HoekedexAndroid` mappát Android Studio-ban.
2. Várd meg, amíg a Gradle szinkron lefut.
3. APK készítés Android Studio-ból:

```text
Build > Build Bundle(s) / APK(s) > Build APK(s)
```

Ha van külön telepített Gradle a gépen, terminálból is futtatható:

```powershell
gradle assembleDebug
```

## APK helye

Sikeres debug build után:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Ezt a fájlt át lehet másolni telefonra, majd fájlkezelőből telepíteni. A telefonon engedélyezni kell az "ismeretlen forrásból" vagy "ismeretlen alkalmazások telepítése" opciót ahhoz a fájlkezelőhöz, amivel megnyitod az APK-t.

## Megjegyzés

Az app nem kér internet-hozzáférést, nincs regisztráció, nincs bejelentkezés és nincs cloud sync. Az adatok a készüléken maradnak.
