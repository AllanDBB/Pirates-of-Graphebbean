package org.adbbnod;

import org.adbbnod.enums.TypesOfWeapons;
import org.adbbnod.weapons.Bomb;
import org.adbbnod.weapons.Canon;
import org.adbbnod.weapons.SuperCanon;
import org.adbbnod.weapons.UltraCanon;

public class Armory extends Component{

    TypesOfWeapons weapon;

    Armory(){
        super();
        this.setPrice(1500);
    }

    public TypesOfWeapons getWeapon() {
        return weapon;
    }

    public void setWeapon(TypesOfWeapons weapon) {
        this.weapon = weapon;
    }

    public Weapon generateWeapon(Player player){
        if (weapon == TypesOfWeapons.CANON && player.getIron()>= 500){
            player.setIron(player.getIron()-500);
            return new Canon();
        } else if (weapon == TypesOfWeapons.SUPERCANON && player.getIron()>= 1000){
            player.setIron(player.getIron()-1000);
            return new SuperCanon();
        } else if (weapon == TypesOfWeapons.ULTRACANON && player.getIron()>= 5000) {
            player.setIron(player.getIron()-5000);
            return new UltraCanon();
        } else if (weapon == TypesOfWeapons.BOMB && player.getIron()>= 2000) {
            player.setIron(player.getIron()-2000);
            return new Bomb();
        } else {
            return null;
        }
    }
}
