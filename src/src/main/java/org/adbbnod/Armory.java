package org.adbbnod;

import jdk.internal.net.http.common.Pair;

import java.util.ArrayList;

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

    public Weapon generateWeapon(){
        //TODO : VALIDACION DE QUE TENGA SUFICIENTE ACERO
        if (weapon == TypesOfWeapons.CANON){
            return new Canon();
        } else if (weapon == TypesOfWeapons.SUPERCANON){
            return new SuperCanon();
        } else if (weapon == TypesOfWeapons.ULTRACANON) {
            return new UltraCanon();
        } else if (weapon == TypesOfWeapons.BOMB) {
            return new Bomb();
        } else {
            return null;
        }
    }
}
