package org.abno.logic.components;

public class Mine extends Component{

    int time;
    int quantity;

    public Mine(){
        super();
        this.setPrice(1000);
        this.time = 60; // 1 min default pero es configurable
        this.quantity = 50; // 50 kg de acero se fabrican por time, también configurable
    }


    public void mine (Player player){
        player.setIron(player.getIron()+quantity);
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
