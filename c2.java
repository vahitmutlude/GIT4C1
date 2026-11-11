//RegularDice.java
package hsd.inflab.dice.types;

public class RegularDice extends Dice {

    @Override
    public int roll() {
        return getRandomNumber(1, 6);
    }

    @Override
    public int getSides() {
        return 6;
    }
}




//MultisidedDice.java
package hsd.inflab.dice.types;

public class MultisidedDice extends Dice {

    private final int sides;

    public MultisidedDice(int sides) {
        this.sides = sides;
    }

    @Override
    public int roll() {
        return getRandomNumber(1, sides);
    }

    @Override
    public int getSides() {
        return sides;
    }
}




//LoadedDice.java
package hsd.inflab.dice.types;

public class LoadedDice extends RegularDice {

    @Override
    public int roll() {
        if (getRandomNumber(1, 2) == 1) {
            return 6;
        }
        return getRandomNumber(1, 5);
    }
}




//ChaosDice.java
package hsd.inflab.dice.types;

public class ChaosDice extends Dice {

    private int sides;

    public ChaosDice() {
        sides = getRandomNumber(4, 20);
    }

    @Override
    public int roll() {
        int result = getRandomNumber(1, sides);
        sides = getRandomNumber(4, 20);
        return result;
    }

    @Override
    public int getSides() {
        return sides;
    }
}




//CustomDice.java
package hsd.inflab.dice.types;

public class CustomDice extends RegularDice {

    @Override
    public int roll() {
        return getRandomNumber(2, 6);
    }
}
