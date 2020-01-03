package pt.ulusofona.lp2.fandeisiaGame;

public class Tesouro {
    int pontos;
    int id;
    int x, y;

    public Tesouro(int id, int x, int y, String type) {
        this.id = id;
        this.x = x;
        this.y = y;
        if (type.equals("gold")){
            pontos = 3;
        }
        if (type.equals("silver")){
            pontos = 2;
        }
        if(type.equals("bronze")){
            pontos = 1;
        }
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPontos() {
        return pontos;
    }
}
