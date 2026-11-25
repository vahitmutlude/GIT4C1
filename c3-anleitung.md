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

## 3. `EntranceGate.java` (Ordner `impl`) – Standard-Einfahrt (nur Signatur geändert)

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

## 4. `ExitGate.java` (Ordner `impl`) – Standard-Ausfahrt (nur Signatur geändert)

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

## 5. `FullLotEntrance.java` (Ordner `impl`) – NEU – Regel 1: Parkhaus voll → abweisen

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

## 6. `SemiLimitEntrance.java` (Ordner `impl`) – NEU – Regel 2: ab 80 % keine LKW

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

## 7. `HourlyRateExit.java` (Ordner `impl`) – NEU – Regel 3: 2 € je angefangene Stunde, ≤ 15 min gratis

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

## 8. `VehicleTypeExit.java` (Ordner `impl`) – NEU – Regel 4: LKW +5 €, Motorrad −1 €

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

## 9. `Lot.java` (Ordner `parking`) – Lot – ganze Datei ersetzen

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

## Fertig?

1. **File → Save All**
2. Unten im Tab **Problems**: keine roten ❌ mehr (gelbe ⚠ sind egal).
3. Falls noch rote Fehler: **Strg + Shift + P** → `Java: Clean Java Language Server Workspace` → **Reload and delete**.
4. `Main.java` öffnen → **Run** → im Fenster bei *Einfahrt-Logik* und *Ausfahrt-Logik* die neuen Klassen auswählen.
