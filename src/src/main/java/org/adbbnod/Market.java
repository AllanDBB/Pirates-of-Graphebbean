package org.adbbnod;

import org.adbbnod.enums.TypesOfItems;

public class Market extends Component{

    Market(){
        super();
        this.setPrice(2000);
    }

    public void marketBuysComponent(Player seller, Component component){
        if (seller.getComponents().contains(component)){
            seller.getComponents().remove(component);
            seller.setMoney(seller.getMoney() + (component.getPrice()/2));
        }
    }

    public void marketBuysIron(Player seller, int quantity){
        if (seller.getIron() >= quantity){
            seller.setIron(seller.getIron() - quantity);
            seller.setMoney(seller.getMoney() + (quantity/2));
        }
    }

    public void marketBuysWeapons(Player seller, Weapon weapon){
        if (seller.getWeapons().contains(weapon)){
            seller.getWeapons().remove(weapon);
            seller.setMoney(seller.getMoney() + (weapon.getIron()/2));
        }
    }

    public Item marketSells(Player buyer, TypesOfItems item){

        if (item == TypesOfItems.ENERGYSOURCE && buyer.getMoney() >= 12000){
            buyer.setMoney(buyer.getMoney() - 12000);
            return new EnergySource();
        } else if (item == TypesOfItems.CONNECTOR && buyer.getMoney() >= 100){
            buyer.setMoney(buyer.getMoney() - 100);
            return new Connector();
        } else if (item == TypesOfItems.MARKET && buyer.getMoney() >= 2000) {
            buyer.setMoney(buyer.getMoney() - 2000);
            return new Market();
        } else if (item == TypesOfItems.MINE && buyer.getMoney() >= 1000) {
            buyer.setMoney(buyer.getMoney() - 1000);
            return new Mine();
        } else if (item == TypesOfItems.WITCHTEMPLE && buyer.getMoney() >= 2500) {
            buyer.setMoney(buyer.getMoney() - 2500);
            return new WitchTemple();
        } else if (item == TypesOfItems.ARMORY && buyer.getMoney() >= 1500){
            buyer.setMoney(buyer.getMoney() - 1500);
            return new Armory();
        } else if (item == TypesOfItems.SHIP){
            buyer.setMoney(buyer.getMoney() - 2500);
            return new Ship();
        } else {
            return null;
        }
    }

    public void playerTransactionComponent(Player buyer, Player seller, Component component, int price){
        if (seller.getComponents().contains(component) && buyer.getMoney() >= price){

            seller.getComponents().remove(component);
            seller.setMoney(seller.getMoney() + price);

            buyer.getComponents().add(component);
            buyer.setMoney(buyer.getMoney() - price);
        }
    }

    public void playerTransactionIron(Player buyer, Player seller, int quantity, int price){
        if (seller.getIron() >= quantity && buyer.getMoney() >= price){

            seller.setIron(seller.getIron() - quantity);
            seller.setMoney(seller.getMoney() + price);

            buyer.setIron(buyer.getIron() + quantity);
            buyer.setMoney(buyer.getMoney() - price);
        }
    }

    public void playerTransactionWeapons(Player seller, Player buyer, Weapon weapon, int price){
        if (seller.getWeapons().contains(weapon) && buyer.getMoney() >= price){

            seller.getWeapons().remove(weapon);
            seller.setMoney(seller.getMoney() + price);

            buyer.getWeapons().add(weapon);
            buyer.setMoney(buyer.getMoney() - price);
        }
    }

}
