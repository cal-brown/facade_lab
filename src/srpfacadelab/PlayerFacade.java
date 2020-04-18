package srpfacadelab;

public class PlayerFacade {

    RpgPlayer player;
    IGameEngine gameEngine;

    public PlayerFacade(RpgPlayer player, IGameEngine gameEngine) {

    }

    public boolean  pickUpItem(Item item) {
        int weight = calculateInventoryWeight();
        if (weight + item.getWeight() > player.getCarryingCapacity())
            return false;

        if (item.isUnique() && checkIfItemExistsInInventory(item))
            return false;



        // Don't pick up items that give health, just consume them.
        if (item.getHeal() > 0) {
            player.setHealth(player.getHealth() + item.getHeal());

            if (player.getHealth() > player.getMaxHealth())
                player.setHealth(player.getMaxHealth());

            if (item.getHeal() > 500) {
                gameEngine.playSpecialEffect("green_swirly");
            }

            return true;
        }

        if (item.isRare())
            gameEngine.playSpecialEffect("cool_swirly_particles");

        if (item.isRare() && item.isUnique())
            gameEngine.playSpecialEffect("blue_swirly");

        player.getInventory().add(item);

        calculateStats();

        return true;
    }

    private void calculateStats() {
        for (Item i: player.getInventory()) {
            player.setArmour(player.getArmour() + i.getArmour());
        }
    }

    private boolean checkIfItemExistsInInventory(Item item) {
        for (Item i: player.getInventory()) {
            if (i.getId() == item.getId())
                return true;
        }
        return false;
    }

    private int calculateInventoryWeight() {
        int sum=0;
        for (Item i: player.getInventory()) {
            sum += i.getWeight();
        }
        return sum;
    }

    public void takeDamage(int damage) {

        if (damage < player.getArmour()) {
            gameEngine.playSpecialEffect("parry");
        }

        if (this.calculateInventoryWeight() < .5* player.getCarryingCapacity()) {
            damage *= .75;
        }

        int damageToDeal = damage - player.getArmour();
        player.setHealth(player.getHealth() - damageToDeal);

        gameEngine.playSpecialEffect("lots_of_gore");
    }
}
