/* parking/
├── Lot.java                     ✏️ DEĞİŞİYOR (4 yer)
└── gate/
    ├── iface/
    │   ├── Entrance.java        ✏️ DEĞİŞİYOR (1 satır)
    │   └── Exit.java            ✏️ DEĞİŞİYOR (1 satır)
    └── impl/
        ├── EntranceGate.java    ✏️ DEĞİŞİYOR (sadece 1 satır, metodun başı)
        ├── ExitGate.java        ✏️ DEĞİŞİYOR (sadece 1 satır, metodun başı)
        ├── FullLotEntrance.java     🆕 Kural 1
        ├── SemiLimitEntrance.java   🆕 Kural 2
        ├── HourlyRateExit.java      🆕 Kural 3
        └── VehicleTypeExit.java     🆕 Kural 4 */


//Entrance.java:
// ÖNCE:
public Ticket processIncoming();
// SONRA:
public Ticket processIncoming(VehicleTypes type, double occupancy, LocalDateTime now);



//Exit.java:
// ÖNCE:
public Receipt processOutgoing();
// SONRA:
public Receipt processOutgoing(VehicleTypes type, LocalDateTime entryTime, LocalDateTime now);



//EntranceGate.java:
//En üste de bir import ekliyorsun:
import de.hsd.inflab.parkinggarage.models.vehicles.VehicleTypes;
//ÖNCE:
public class EntranceGate implements Entrance {

    public Ticket processIncoming(){
        return Ticket.issue(LocalDateTime.of(2026, 10, 6, 12, 0));
    }
}
//SONRA:
public class EntranceGate implements Entrance {

    public Ticket processIncoming(VehicleTypes type, double occupancy, LocalDateTime now){
        return Ticket.issue(LocalDateTime.of(2026, 10, 6, 12, 0));
    }
}



//ExitGate.java.
////En üste de bir import ekliyorsun:
import java.time.LocalDateTime;
//ÖNCE:
public Receipt processOutgoing() {
    return new Receipt(500, Duration.ofMinutes(90), VehicleTypes.CAR);
}
//SONRA:
public Receipt processOutgoing(VehicleTypes type, LocalDateTime entryTime, LocalDateTime now) {
    return new Receipt(500, Duration.ofMinutes(90), VehicleTypes.CAR);
}



//Lot.java:
// enter(...) içinde, ÖNCE:
Ticket ticket = entrance.processIncoming();
// SONRA:
Ticket ticket = entrance.processIncoming(vehicle.getVehicleType(), calculateOccupancy(), clock.now());

// exit(...) içinde, ÖNCE:
Receipt receipt = exit.processOutgoing();
// SONRA:
Receipt receipt = exit.processOutgoing(vehicle.getVehicleType(), ticket.getTimeStamp(), clock.now());

//list lerin oldugu yerde sirasiyla giris ve cikis kisimlarina: 
return List.of(new EntranceGate(), new FullLotEntrance(), new SemiLimitEntrance());
return List.of(new ExitGate(), new HourlyRateExit(), new VehicleTypeExit());



//NEUE JAVA DATEIEN:

//FullLotEntrance.java
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


//SemiLimitEntrance.java
package de.hsd.inflab.parkinggarage.parking.gate.impl;

import de.hsd.inflab.parkinggarage.models.ticket.Ticket;
import de.hsd.inflab.parkinggarage.models.vehicles.VehicleTypes;
import de.hsd.inflab.parkinggarage.parking.gate.iface.Entrance;
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


//HourlyRateExit.java
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


//VehicleTypeExit.java
package de.hsd.inflab.parkinggarage.parking.gate.impl;

import de.hsd.inflab.parkinggarage.models.Receipt;
import de.hsd.inflab.parkinggarage.models.vehicles.VehicleTypes;
import de.hsd.inflab.parkinggarage.parking.gate.iface.Exit;
import java.time.Duration;
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




