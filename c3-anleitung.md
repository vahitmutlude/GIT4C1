# Course 3 – Parkhaus: Kopieren & Einfügen

**So geht's:** Für **jede** Datei unten: Datei in VS Code öffnen (oder neu anlegen) → **Strg + A** → **Entf** → Code einfügen → **Strg + S**.

Ordner: `src/main/java/de/hsd/inflab/parkinggarage/parking/`

```text
parking/
├── Lot.java                        ← ersetzen (ganze Datei)
└── gate/
    ├── iface/
    │   ├── Entrance.java           ← ersetzen
    │   └── Exit.java               ← ersetzen
    └── impl/
        ├── EntranceGate.java       ← ersetzen (nur Signatur anders)
        ├── ExitGate.java           ← ersetzen (nur Signatur anders)
        ├── FullLotEntrance.java    ← NEU  (Regel 1)
        ├── SemiLimitEntrance.java  ← NEU  (Regel 2)
        ├── HourlyRateExit.java     ← NEU  (Regel 3)
        └── VehicleTypeExit.java    ← NEU  (Regel 4)
```

**Neue Datei anlegen:** Rechtsklick auf den Ordner `impl` → **New File…** → Namen mit `.java` eingeben (z. B. `FullLotEntrance.java`) → Code einfügen.

**Reihenfolge:** zuerst die 2 Interfaces, dann die 2 alten Gates, dann die 4 neuen Klassen, zuletzt Lot.java. Danach **File → Save All**.

## Überblick: Was haben wir insgesamt geändert?

| Datei | Änderung |
| --- | --- |
| `Entrance.java` | Methode `processIncoming` bekommt 3 Parameter |
| `Exit.java` | Methode `processOutgoing` bekommt 3 Parameter |
| `EntranceGate.java` | nur die Signatur an das Interface angepasst, Verhalten gleich |
| `ExitGate.java` | nur die Signatur an das Interface angepasst, Verhalten gleich |
| `FullLotEntrance.java` | neu: Regel 1 |
| `SemiLimitEntrance.java` | neu: Regel 2 |
| `HourlyRateExit.java` | neu: Regel 3 |
| `VehicleTypeExit.java` | neu: Regel 4 |
| `Lot.java` | Aufrufe in `enter` und `exit` übergeben die neuen Argumente, neue Klassen in die Listen eingetragen |

## 1. `Entrance.java` (Ordner `iface`) – Interface Einfahrt

```java
package de.hsd.inflab.parkinggarage.parking.gate.iface;

import de.hsd.inflab.parkinggarage.models.ticket.Ticket;
import de.hsd.inflab.parkinggarage.models.vehicles.VehicleTypes;

import java.time.LocalDateTime;

public interface Entrance {
    public Ticket processIncoming(VehicleTypes type, double occupancy, LocalDateTime now);
}
```

**Was wurde geändert?**

- **Vorher:** `public Ticket processIncoming();` – ohne Parameter. Die Einfahrt konnte nichts sehen und deshalb nichts entscheiden.
- **Jetzt:** 3 Parameter:
    - `VehicleTypes type` → Fahrzeugtyp, für Regel 2 (LKW ja oder nein)
    - `double occupancy` → Auslastung von 0.0 bis 1.0, für Regel 1 (voll) und Regel 2 (ab 80 %)
    - `LocalDateTime now` → aktuelle Sim-Uhrzeit, damit das Ticket die richtige Einfahrtszeit bekommt
- **Imports:** `VehicleTypes` statt `Vehicle` importiert, weil ich nur den Typ übergebe, nicht das ganze Fahrzeug („so wenig wie möglich“).

> 🇹🇷 Giriş görevlisine 3 bilgi veriyoruz: araç tipi, doluluk oranı, şu anki saat.

## 2. `Exit.java` (Ordner `iface`) – Interface Ausfahrt

```java
package de.hsd.inflab.parkinggarage.parking.gate.iface;

import de.hsd.inflab.parkinggarage.models.Receipt;
import de.hsd.inflab.parkinggarage.models.vehicles.VehicleTypes;

import java.time.LocalDateTime;

public interface Exit {
    public Receipt processOutgoing(VehicleTypes type, LocalDateTime entryTime, LocalDateTime now);
}
```

**Was wurde geändert?**

- **Vorher:** `public Receipt processOutgoing();` – ohne Parameter. Deshalb konnte die Ausfahrt nur einen festen Preis berechnen.
- **Jetzt:** 3 Parameter:
    - `VehicleTypes type` → für den Zuschlag in Regel 4 und für den Receipt
    - `LocalDateTime entryTime` → Einfahrtszeit aus dem Ticket (`ticket.getTimeStamp()`)
    - `LocalDateTime now` → aktuelle Sim-Uhrzeit, um die Parkdauer zu berechnen (Regel 3)
- **Imports:** `VehicleTypes` hinzugefügt. `Ticket` und `Vehicle` werden nicht mehr gebraucht, weil ich nur die Einfahrtszeit und den Typ übergebe.

> 🇹🇷 Çıkış görevlisine 3 bilgi veriyoruz: araç tipi, giriş saati, şu anki saat.

## 3. `EntranceGate.java` (Ordner `impl`) – Standard-Einfahrt

```java
package de.hsd.inflab.parkinggarage.parking.gate.impl;

import de.hsd.inflab.parkinggarage.models.ticket.Ticket;
import de.hsd.inflab.parkinggarage.models.vehicles.VehicleTypes;
import de.hsd.inflab.parkinggarage.parking.gate.iface.Entrance;

import java.time.LocalDateTime;

public class EntranceGate implements Entrance {

    public Ticket processIncoming(VehicleTypes type, double occupancy, LocalDateTime now){
        return Ticket.issue(LocalDateTime.of(2026, 10, 6, 12, 0));
    }


}
```

**Was wurde geändert?**

- **Nur die Signatur** von `processIncoming` an das neue Interface angepasst und `VehicleTypes` importiert.
- **Das Verhalten ist unverändert** (jedes Fahrzeug bekommt ein Ticket). Das verlangt die Aufgabe: „Lassen Sie EntranceGate (mit Ausnahme der Signatur) unverändert!“
- **Warum nötig?** EntranceGate implementiert das Interface Entrance. Wenn sich das Interface ändert, muss die Methode die gleiche Signatur haben, sonst gibt es einen Compilerfehler.

> 🇹🇷 Sadece metodun başlığı değişti, içi aynı.

## 4. `ExitGate.java` (Ordner `impl`) – Standard-Ausfahrt

```java
package de.hsd.inflab.parkinggarage.parking.gate.impl;

import de.hsd.inflab.parkinggarage.models.Receipt;
import de.hsd.inflab.parkinggarage.models.vehicles.VehicleTypes;
import de.hsd.inflab.parkinggarage.parking.gate.iface.Exit;

import java.time.Duration;
import java.time.LocalDateTime;

public class ExitGate implements Exit {
    public Receipt processOutgoing(VehicleTypes type, LocalDateTime entryTime, LocalDateTime now) {
        return new Receipt(500, Duration.ofMinutes(90), VehicleTypes.CAR);
    }
}
```

**Was wurde geändert?**

- **Nur die Signatur** von `processOutgoing` an das neue Interface angepasst und `LocalDateTime` importiert.
- **Das Verhalten ist unverändert** (fester Betrag von 500, feste Dauer von 90 Minuten).
- **Warum nötig?** Gleicher Grund wie bei EntranceGate: ExitGate implementiert das Interface Exit.

> 🇹🇷 Sadece metodun başlığı değişti, içi aynı.

## 5. `FullLotEntrance.java` (Ordner `impl`) – NEU, Regel 1

```java
package de.hsd.inflab.parkinggarage.parking.gate.impl;

import de.hsd.inflab.parkinggarage.models.ticket.Ticket;
import de.hsd.inflab.parkinggarage.models.vehicles.VehicleTypes;
import de.hsd.inflab.parkinggarage.parking.gate.iface.Entrance;

import java.time.LocalDateTime;

public class FullLotEntrance implements Entrance {

    @Override
    public Ticket processIncoming(VehicleTypes type, double occupancy, LocalDateTime now) {
        if (occupancy >= 1.0) {
            return Ticket.denyEntry("Parkhaus voll");
        }
        return Ticket.issue(now);
    }
}
```

**Was wurde geändert?**

- **Neue Klasse**, implementiert das Interface `Entrance`.
- **Regel 1:** Ist das Parkhaus voll (Auslastung ≥ 1.0, also 100 %), wird das Fahrzeug mit `Ticket.denyEntry(...)` abgewiesen.
- Sonst bekommt es mit `Ticket.issue(now)` ein gültiges Ticket mit der **aktuellen Sim-Uhrzeit**.

> 🇹🇷 Otopark %100 doluysa geri çevir, değilse şu anki saatle bilet ver.

## 6. `SemiLimitEntrance.java` (Ordner `impl`) – NEU, Regel 2

```java
package de.hsd.inflab.parkinggarage.parking.gate.impl;

import de.hsd.inflab.parkinggarage.models.ticket.Ticket;
import de.hsd.inflab.parkinggarage.models.vehicles.VehicleTypes;

import java.time.LocalDateTime;

public class SemiLimitEntrance extends FullLotEntrance {

    @Override
    public Ticket processIncoming(VehicleTypes type, double occupancy, LocalDateTime now) {
        if (type == VehicleTypes.SEMI && occupancy >= 0.8) {
            return Ticket.denyEntry("Ab 80 % keine LKW");
        }
        return super.processIncoming(type, occupancy, now);
    }
}
```

**Was wurde geändert?**

- **Neue Klasse**, **erbt von `FullLotEntrance`** (Regel 2 = „wie 1, zusätzlich …“).
- Ist das Fahrzeug ein LKW (`SEMI`) **und** die Auslastung ≥ 80 %, wird es abgewiesen.
- Sonst wird mit `super.processIncoming(...)` **Regel 1 aus der Oberklasse** ausgeführt. So muss ich Regel 1 nicht noch einmal schreiben.

> 🇹🇷 Kamyon ve %80+ doluysa geri çevir. Değilse Kural 1'e sor (super).

## 7. `HourlyRateExit.java` (Ordner `impl`) – NEU, Regel 3

```java
package de.hsd.inflab.parkinggarage.parking.gate.impl;

import de.hsd.inflab.parkinggarage.models.Receipt;
import de.hsd.inflab.parkinggarage.models.vehicles.VehicleTypes;
import de.hsd.inflab.parkinggarage.parking.gate.iface.Exit;

import java.time.Duration;
import java.time.LocalDateTime;

public class HourlyRateExit implements Exit {

    @Override
    public Receipt processOutgoing(VehicleTypes type, LocalDateTime entryTime, LocalDateTime now) {
        Duration parkingTime = Duration.between(entryTime, now);
        long minutes = parkingTime.toMinutes();

        if (minutes <= 15) {
            return new Receipt(0, parkingTime, type);
        }

        long hours = (minutes + 59) / 60;
        int amount = (int) hours * 2;
        return new Receipt(amount, parkingTime, type);
    }
}
```

**Was wurde geändert?**

- **Neue Klasse**, implementiert das Interface `Exit`.
- `Duration.between(entryTime, now)` berechnet die **Parkdauer**, `toMinutes()` macht daraus Minuten.
- Bis einschließlich **15 Minuten** kostet es **0 €** (Kulanz für Kurzparker).
- Sonst: **angefangene Stunden** zählen. `(minutes + 59) / 60` rundet auf, z. B. 61 Minuten → 2 Stunden. Pro Stunde **2 €**.
- Das Ergebnis wird als `new Receipt(betrag, parkdauer, typ)` zurückgegeben.

> 🇹🇷 Kaç dakika kaldı? 15 veya altıysa 0 €. Yoksa başlanan her saat 2 €.

## 8. `VehicleTypeExit.java` (Ordner `impl`) – NEU, Regel 4

```java
package de.hsd.inflab.parkinggarage.parking.gate.impl;

import de.hsd.inflab.parkinggarage.models.Receipt;
import de.hsd.inflab.parkinggarage.models.vehicles.VehicleTypes;

import java.time.LocalDateTime;

public class VehicleTypeExit extends HourlyRateExit {

    @Override
    public Receipt processOutgoing(VehicleTypes type, LocalDateTime entryTime, LocalDateTime now) {
        Receipt receipt = super.processOutgoing(type, entryTime, now);
        int amount = receipt.getAmount();

        if (amount == 0) {
            return receipt;
        }

        if (type == VehicleTypes.SEMI) {
            amount = amount + 5;
        } else if (type == VehicleTypes.MOTORCYCLE) {
            amount = amount - 1;
        }

        if (amount < 0) {
            amount = 0;
        }
        return new Receipt(amount, receipt.getParkingTime(), type);
    }
}
```

**Was wurde geändert?**

- **Neue Klasse**, **erbt von `HourlyRateExit`** (Regel 4 = „wie 3, zusätzlich …“).
- `super.processOutgoing(...)` berechnet zuerst den Preis nach **Regel 3**.
- War es kostenlos (0 €), **bleibt es kostenlos**: Der Zuschlag gilt nur für zahlungspflichtige Aufenthalte.
- Sonst: **LKW +5 €**, **Motorrad −1 €**, PKW unverändert.
- Der Preis wird **nie negativ** (mindestens 0 €).

> 🇹🇷 Önce Kural 3 hesaplasın. Bedavaysa bedava kalsın. Kamyon +5 €, motosiklet −1 €, asla eksi olmasın.

## 9. `Lot.java` (Ordner `parking`) – ganze Datei ersetzen

```java
package de.hsd.inflab.parkinggarage.parking;

import de.hsd.inflab.parkinggarage.parking.gate.iface.Entrance;
import de.hsd.inflab.parkinggarage.parking.gate.iface.Exit;
import de.hsd.inflab.parkinggarage.parking.gate.impl.EntranceGate;
import de.hsd.inflab.parkinggarage.models.Receipt;
import de.hsd.inflab.parkinggarage.models.ticket.Ticket;
import de.hsd.inflab.parkinggarage.parking.gate.impl.ExitGate;
import de.hsd.inflab.parkinggarage.parking.gate.impl.FullLotEntrance;
import de.hsd.inflab.parkinggarage.parking.gate.impl.SemiLimitEntrance;
import de.hsd.inflab.parkinggarage.parking.gate.impl.HourlyRateExit;
import de.hsd.inflab.parkinggarage.parking.gate.impl.VehicleTypeExit;
import de.hsd.inflab.parkinggarage.simulator.SimClock;
import de.hsd.inflab.parkinggarage.models.vehicles.Vehicle;

import java.util.List;

public class Lot {
    private Entrance entrance;
    private Exit exit;
    private SimClock clock;
    private int capacity;
    private ParkingSpot[] spots;

    public Lot(Entrance entrance, Exit exit, SimClock clock) {
        this.entrance = entrance;
        this.exit = exit;
        this.clock = clock;
        capacity = 100;
        spots = new ParkingSpot[capacity];
        for (int i=0; i<capacity; i++) {
            spots[i] = new ParkingSpot();
        }
    }

    public static Lot buildLot(SimClock clock) {
        // Standard = jeweils erste Implementierung aus den Katalog-Listen unten
        return new Lot(availableEntrances().get(0), availableExits().get(0), clock);
    }

    //  Tragen Sie hier Ihre eigenen Einfahrt- bzw. Ausfahrt-Klassen ein, damit sie
    //  in der GUI auswaehlbar werden. Die jeweils erste Klasse ist der Standard. ----
    public static List<Entrance> availableEntrances() {
        return List.of(new EntranceGate(), new FullLotEntrance(), new SemiLimitEntrance());
    }

    public static List<Exit> availableExits() {
        return List.of(new ExitGate(), new HourlyRateExit(), new VehicleTypeExit());
    }
    //-----------------------------------

    public Entrance getEntrance() {
        return entrance;
    }

    public void setEntrance(Entrance entrance) {
        this.entrance = entrance;
    }

    public Exit getExit() {
        return exit;
    }

    public void setExit(Exit exit) {
        this.exit = exit;
    }

    public Ticket enter(Vehicle vehicle) {
        Ticket ticket = entrance.processIncoming(vehicle.getVehicleType(), calculateOccupancy(), clock.now());
        if (ticket.isValid())
            assignSpot(vehicle);
        return ticket;
    }

    public Receipt exit(Ticket ticket, Vehicle vehicle) {
        Receipt receipt = exit.processOutgoing(vehicle.getVehicleType(), ticket.getTimeStamp(), clock.now());
        releaseSpot(vehicle);
        return receipt;
    }

    private void assignSpot(Vehicle vehicle) {
        for (ParkingSpot s : spots) {
            if(s.isFree()) {
                s.park(vehicle);
                return;
            }

        }
    }

    private void releaseSpot(Vehicle vehicle) {
        for (ParkingSpot s : spots) {
            if(s.getOccupant() == vehicle) {
                s.free();
                return;
            }
        }
    }

    public boolean isFull() {
        for (ParkingSpot s: spots) {
            if (s.isFree())
                return false;
        }
        return true;        //no free spaces in lot
    }

    public int getCapacity() {
        return capacity;
    }

    public ParkingSpot[] getSpots() {
        return spots;
    }

    public int countVehicles() {
        int vehiclesInLot = 0;
        for (ParkingSpot s : spots) {
            if (!s.isFree())
                vehiclesInLot++;
        }
        return vehiclesInLot;
    }

    public double calculateOccupancy() {
        return (double) countVehicles() / capacity;
    }
}
```

**Was wurde geändert?**

Nur **4 Stellen** sind geändert, alles andere ist original:

1. **Imports:** die 4 neuen Klassen importiert (`FullLotEntrance`, `SemiLimitEntrance`, `HourlyRateExit`, `VehicleTypeExit`).
2. **`availableEntrances()` / `availableExits()`:** die neuen Klassen in die Listen eingetragen. Dadurch kann man sie in der GUI auswählen. Die erste Klasse bleibt der Standard.
3. **`enter(...)`:** Vorher `entrance.processIncoming()`. Jetzt `entrance.processIncoming(vehicle.getVehicleType(), calculateOccupancy(), clock.now())`. Lot gibt der Einfahrt den **Fahrzeugtyp**, die **Auslastung** und die **aktuelle Uhrzeit**.
4. **`exit(...)`:** Vorher `exit.processOutgoing()`. Jetzt `exit.processOutgoing(vehicle.getVehicleType(), ticket.getTimeStamp(), clock.now())`. Lot gibt der Ausfahrt den **Fahrzeugtyp**, die **Einfahrtszeit aus dem Ticket** und die **aktuelle Uhrzeit**.

**Warum in Lot?** Die Aufgabe sagt: „Alle Aufrufe der Interface-Methoden liegen in der Klasse Lot.“ Lot kennt alle Informationen: das Fahrzeug, die Auslastung (eigene Methode) und die Uhr (eigenes Attribut `clock`).

> 🇹🇷 Lot bilgileri topluyor ve görevlilere veriyor. Yeni sınıflar pencerede seçilebilsin diye listelere eklendi.

## Fertig?

1. **File → Save All**
2. Unten im Tab **Problems**: keine roten ❌ mehr (gelbe ⚠ sind egal).
3. Falls noch rote Fehler: **Strg + Shift + P** → `Java: Clean Java Language Server Workspace` → **Reload and delete**.
4. `Main.java` öffnen → **Run** → im Fenster bei *Einfahrt-Logik* und *Ausfahrt-Logik* die neuen Klassen auswählen.
